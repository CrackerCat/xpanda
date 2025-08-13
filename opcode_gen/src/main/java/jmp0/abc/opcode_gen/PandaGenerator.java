package jmp0.abc.opcode_gen;

import jmp0.abc.opcode_gen.ins.FlagGenDescription;
import jmp0.abc.opcode_gen.ins.FormatGenDescription;
import jmp0.abc.opcode_gen.ins.InstructionGenDescription;

import java.io.IOException;
import java.util.*;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class PandaGenerator {
    private static final String REPLACED_TAG = "/*replace padding*/";
    private static String HEADER_TEMPLATE;
    private final PandaIsaParser parser;

    static {
        try {
            HEADER_TEMPLATE = new String(PandaGenerator.class.getClassLoader().getResource("template/header.txt").openStream().readAllBytes());
        } catch (IOException ignore) {
            HEADER_TEMPLATE = "";
        }

    }

    public PandaGenerator(){
        this.parser = new PandaIsaParser();
    }

    private String commonHeader(){
        String version = this.parser.getByteCodeVersion();
        return HEADER_TEMPLATE.replace(REPLACED_TAG, version);
    }
    private String generateInstructions(){
        StringBuilder builder = new StringBuilder();
        LinkedList<InstructionGenDescription> linkedList = this.parser.getInstructionGenDescriptions();
        Collections.sort(linkedList, Comparator.comparingInt(InstructionGenDescription::getOpCode));
        for (InstructionGenDescription instructionGenDescription : linkedList) {
            String name = instructionGenDescription.getName().replace(".","_");
            builder.append(String.format("\t%s(%d,\"%s\",PandaOPCodeFormat.%s,0x%x),\n",name.toUpperCase()+'_'+instructionGenDescription.getFormat().getEnumName(),
                    instructionGenDescription.getOpCode(),instructionGenDescription.getName(),instructionGenDescription.getFormat().getEnumName(),instructionGenDescription.getFlag()));
        }
        String str = builder.toString();
        return str.substring(0,str.length() - 2) + ';';
    }

    private String generateFormats(){
        StringBuilder builder = new StringBuilder();
        for (FormatGenDescription value : this.parser.getFormatMap().values()) {
            builder.append(String.format("\t%s(%d),\n",value.getEnumName(),value.getSize()));
        }
        String str = builder.toString();
        return str.substring(0,str.length() - 2) + ';';
    }

    private String generateFlags(){
        StringBuilder builder = new StringBuilder();
        for (FlagGenDescription value : this.parser.getFlagMap().values()) {
            builder.append(String.format("\t%s(0x%x),\n",value.getEnumName(),value.getValue()));
        }
        String str = builder.toString();
        return str.substring(0,str.length() - 2) + ';';
    }

    public String generateFormatFile(){
        StringBuilder builder = new StringBuilder();
        builder.append(commonHeader());
        try {
            byte[] bs = PandaGenerator.class.getClassLoader().getResource("template/PandaOPCodeFormat.txt").openStream().readAllBytes();
            String template = new String(bs);
            builder.append(template.replace(REPLACED_TAG,generateFormats()));
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
        return builder.toString();
    }

    public String generateFlagsFile(){
        StringBuilder builder = new StringBuilder();
        builder.append(commonHeader());
        try {
            byte[] bs = PandaGenerator.class.getClassLoader().getResource("template/PandaOPCodeFlag.txt").openStream().readAllBytes();
            String template = new String(bs);
            builder.append(template.replace(REPLACED_TAG,generateFlags()));
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
        return builder.toString();
    }

    public String generateOPCodeFile(){
        StringBuilder builder = new StringBuilder();
        builder.append(commonHeader());
        try {
            byte[] bs = PandaGenerator.class.getClassLoader().getResource("template/PandaOPCode.txt").openStream().readAllBytes();
            String template = new String(bs);
            builder.append(template.replace(REPLACED_TAG,generateInstructions()));
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
        return builder.toString();
    }
}
