package jmp0.abc.opcode_gen;

import jmp0.abc.opcode_gen.bean.PandaIsaBean;
import jmp0.abc.opcode_gen.bean.groups.GroupItemBean;
import jmp0.abc.opcode_gen.bean.groups.InstructionItemBean;
import jmp0.abc.opcode_gen.bean.prefixes.PrefixItemBean;
import jmp0.abc.opcode_gen.bean.properties.PropertyItemBean;
import jmp0.abc.opcode_gen.ins.FlagGenDescription;
import jmp0.abc.opcode_gen.ins.FormatGenDescription;
import jmp0.abc.opcode_gen.ins.InstructionGenDescription;
import lombok.Getter;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.util.*;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class PandaIsaParser {
    private final PandaIsaBean bean;
    @Getter private String byteCodeVersion;
    @Getter private final LinkedList<InstructionGenDescription> instructionGenDescriptions = new LinkedList<>();
    @Getter private final HashMap<String,FormatGenDescription> formatMap = new LinkedHashMap<>();
    private final HashMap<String,Integer> prefixMap = new LinkedHashMap<>();
    @Getter private final HashMap<String, FlagGenDescription> flagMap = new LinkedHashMap<>();

    public PandaIsaParser(){
        Yaml yaml = new Yaml(new Constructor(PandaIsaBean.class,new LoaderOptions()));
        this.bean = yaml.load(this.getClass().getClassLoader().getResourceAsStream("isa.yaml"));
        this.parse();
    }

    private void parseByteCodeVersion(){
        this.byteCodeVersion = this.bean.getVersion();
    }

    private void parsePrefix(){
        for (PrefixItemBean prefix : this.bean.getPrefixes()) {
            this.prefixMap.put(prefix.getName(),prefix.getOpcode_idx());
        }
    }

    private FormatGenDescription genFormatDescWithFormatString(String formatString){
        String[] format = formatString.split("_");
        int idx = 0;
        float size = 0;
        StringBuilder enumNameBuilder = new StringBuilder();
        while(idx < format.length){
            String v = format[idx];
            if (v.equals("op")){
                idx += 1;
            }else if (v.equals("pref")){
                idx += 2;
                enumNameBuilder.append("PREF");
            }else {
                enumNameBuilder.append("_");
                if (v.startsWith("v")){
                    int paramSize = Integer.parseInt(format[idx+1]);
                    size += (float) paramSize / 8;
                    enumNameBuilder.append("V").append(paramSize);
                    idx += 2;
                }else if (v.startsWith("imm")){
                    int paramSize = Integer.parseInt(format[idx+1]);
                    size += (float) paramSize / 8;
                    enumNameBuilder.append("IMM").append(paramSize);
                    idx += 2;
                }else if (v.startsWith("id")){
                    int paramSize = Integer.parseInt(format[idx+1]);
                    size += (float) paramSize / 8;
                    enumNameBuilder.append("ID").append(paramSize);
                    idx += 2;
                }else if (v.startsWith("none")){
                    enumNameBuilder.append("NONE");
                    idx++;
                }else {
                    System.err.println("format not support");
                }
            }
        }
        String enumName = enumNameBuilder.toString();
        if (enumName.startsWith("_")){
            enumName = enumName.substring(1);
        }
        FormatGenDescription formatGenDescription = new FormatGenDescription();
        formatGenDescription.setSize((int)size);
        formatGenDescription.setEnumName(enumName);
        return formatGenDescription;
    }

    private void parseFormat(){
        Set<String> formatStringSet = new HashSet<>();
        for (GroupItemBean group : this.bean.getGroups()) {
            for (InstructionItemBean instruction : group.getInstructions()) {
                formatStringSet.addAll(instruction.getFormat());
            }
        }
        for (String s : formatStringSet) {
            this.formatMap.put(s, this.genFormatDescWithFormatString(s));
        }
    }

    private void parseFlag(){
        int idx = 0;
        LinkedList<String> flagNames = new LinkedList<>();
        for (PropertyItemBean property : this.bean.getProperties()) {
            flagNames.add(property.getTag());
        }
        flagNames.add("acc_none");
        flagNames.add("acc_read");
        flagNames.add("acc_write");
        for (String tag : flagNames) {
            FlagGenDescription flagGenDescription = new FlagGenDescription();
            flagGenDescription.setEnumName(tag.toUpperCase());
            flagGenDescription.setValue(1<<(idx++));
            this.flagMap.put(tag,flagGenDescription);
        }
    }

    private int calcFlagWithProperties(List<String> groupProps,List<String> selfProps){
        int result = 0;
        if (groupProps!=null){
            for (String prop : groupProps) {
                FlagGenDescription flagGenDescription = this.flagMap.get(prop);
                result |= flagGenDescription.getValue();
            }
        }
        if (selfProps!=null){
            for (String prop : selfProps) {
                FlagGenDescription flagGenDescription = this.flagMap.get(prop);
                result |= flagGenDescription.getValue();
            }
        }
        return result;
    }

    private void parseInstruction(){
        //get instruction opcode
        for (GroupItemBean groupItemBean : this.bean.getGroups()) {
            List<String> groupProps = groupItemBean.getProperties();
            for (InstructionItemBean instruction : groupItemBean.getInstructions()) {
                //name
                String sig = instruction.getSig();
                String[] sigArr = sig.split(" ");
                String name = sigArr[0];

                //opcode_idx
                Integer[] opcodeIdxArr = instruction.getOpcode_idx().toArray(new Integer[]{});
                String[] formatArr = instruction.getFormat().toArray(new String[]{});

                //prefix
                String prefix = instruction.getPrefix();

                //properties
                List<String> flags = instruction.getProperties();

                for (int i = 0; i < opcodeIdxArr.length; i++) {
                    InstructionGenDescription instructionGenDescription = new InstructionGenDescription();
                    int opcode = opcodeIdxArr[i];
                    String format = formatArr[i];
                    instructionGenDescription.setName(name);
                    FormatGenDescription formatGenDescription = this.formatMap.get(format);
                    instructionGenDescription.setFormat(formatGenDescription);
                    instructionGenDescription.setFlag(this.calcFlagWithProperties(groupProps,flags));
                    if (prefix != null){
                        int value = this.prefixMap.get(prefix);
                        opcode = (opcode << 8) | value;
                    }
                    instructionGenDescription.setOpCode(opcode);
                    this.instructionGenDescriptions.add(instructionGenDescription);
                }
            }
        }
    }

    private void parse(){
        this.parseByteCodeVersion();
        this.parsePrefix();
        this.parseFormat();
        this.parseFlag();
        this.parseInstruction();
    }

}
