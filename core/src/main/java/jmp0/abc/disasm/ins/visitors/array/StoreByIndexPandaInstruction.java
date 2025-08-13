package jmp0.abc.disasm.ins.visitors.array;

import jmp0.abc.disasm.ins.PandaInstruction;
import jmp0.abc.disasm.param.PandaInstructionIMM;
import jmp0.abc.disasm.param.PandaInstructionVReg;
import jmp0.abc.disasm.types.PandaOPCode;
import jmp0.abc.file.method.PandaMethod;
import lombok.Getter;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
@Getter
public abstract class StoreByIndexPandaInstruction extends PandaInstruction {
    private final PandaInstructionVReg arr;
    private final Number index;
    public StoreByIndexPandaInstruction(int pc, PandaOPCode opCode, byte[] param, PandaMethod pandaMethod) {
        super(pc, opCode, param, pandaMethod);
        if (opCode == PandaOPCode.WIDE_STOWNBYINDEX_PREF_V8_IMM32){
            this.arr = (PandaInstructionVReg)(this.getParams()[0]);
            this.index = ((PandaInstructionIMM)(this.getParams()[1])).getImm();
            return;
        }
        this.arr = (PandaInstructionVReg)(this.getParams()[1]);
        this.index = ((PandaInstructionIMM)(this.getParams()[2])).getImm();
    }

    @Override
    public String toString() {
        return baseToString() + arr.toString() + "[" + index.toString() + "] = acc";
    }
}
