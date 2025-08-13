package jmp0.abc.disasm.block;

import jmp0.abc.PandaParseException;
import jmp0.abc.disasm.ins.PandaInstruction;
import jmp0.abc.disasm.types.PandaOPCode;
import jmp0.abc.file.method.PandaMethod;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class PandaIRBasicBlock {
    @Getter private final String name;
    @Getter private final PandaInstruction[] pandaInstructions;
    @Getter private LinkedList<PandaIRBasicBlock> catchBlockList = null;
    @Getter private final PandaMethod pandaMethod;
    private final HashMap<Integer,Integer> pandaInstructionHashMap = new HashMap<>();
    public PandaIRBasicBlock(String name, PandaInstruction[] pandaInstructions, PandaMethod pandaMethod){
        this.name = name;
        this.pandaInstructions = pandaInstructions;
        this.pandaMethod = pandaMethod;
        for (int i = 0; i < this.pandaInstructions.length; i++) {
            pandaInstructionHashMap.put(pandaInstructions[i].getPC(),i);
        }
    }

    public PandaInstruction getTerminator(){
        return pandaInstructions[pandaInstructions.length -1];
    }

    public int getStartPC(){
        return pandaInstructions[0].getPC();
    }

    public int getSize(){
        int start = getStartPC();
        PandaInstruction terminator = getTerminator();
        return terminator.getPC() - start + terminator.getOpCode().getInstructionSize();
    }

    public PandaInstruction getByPC(int pc){
        Integer idx = pandaInstructionHashMap.get(pc);
        if (idx != null) return pandaInstructions[idx];
        return null;
    }

    public PandaInstruction getPreInstructionByPc(int pc){
        Integer idx = pandaInstructionHashMap.get(pc);
        if (idx != null && idx != 0) return pandaInstructions[idx-1];
        return null;
    }

    public void setCatchInfo(LinkedList<PandaIRBasicBlock> catchBlockList){
        this.catchBlockList = catchBlockList;
    }

    public PandaMethod getParent(){
        return this.pandaMethod;
    }

    @SneakyThrows
    public PandaIRBasicBlock[] splitBasicBlock(int pc){
        Integer idx = pandaInstructionHashMap.get(pc);
        if (idx == null) throw new PandaParseException(pc + " not found");
        PandaInstruction[] a = Arrays.copyOfRange(pandaInstructions,0,idx);
        PandaInstruction[] b = Arrays.copyOfRange(pandaInstructions,idx,pandaInstructions.length);
        PandaIRBasicBlock[] pandaIRBasicBlocks = new PandaIRBasicBlock[2];
        pandaIRBasicBlocks[0] = new PandaIRBasicBlock(this.name,a,this.pandaMethod);
        pandaIRBasicBlocks[1] = new PandaIRBasicBlock("label_"+b[0].getPC(),b,this.pandaMethod);
        return pandaIRBasicBlocks;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.name);
        if (this.catchBlockList != null){
            stringBuilder.append(" catch by [");
            for (PandaIRBasicBlock pandaIRBasicBlock : this.catchBlockList) {
                stringBuilder.append(pandaIRBasicBlock.getName());
            }
            stringBuilder.append("]");
        }
        stringBuilder.append(":\n");
        for (PandaInstruction pandaInstruction : this.pandaInstructions) {
            stringBuilder.append("\t").append(pandaInstruction.toString()).append("\n");
        }
        return stringBuilder.toString();
    }
}
