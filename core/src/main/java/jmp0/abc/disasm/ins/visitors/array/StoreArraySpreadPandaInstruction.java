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
public abstract class StoreArraySpreadPandaInstruction extends PandaInstruction {
    private final PandaInstructionVReg dest;
    private final PandaInstructionVReg index;
    public StoreArraySpreadPandaInstruction(int pc, PandaOPCode opCode, byte[] param, PandaMethod pandaMethod) {
        super(pc, opCode, param, pandaMethod);
        this.dest = (PandaInstructionVReg)(this.getParams()[0]);
        this.index = (PandaInstructionVReg)(this.getParams()[1]);
    }

    public String selfToString(){
        return dest + ".splice("+ index +",0,...acc)";
    }

    @Override
    public String toString() {
        return baseToString() + selfToString();
    }
}
