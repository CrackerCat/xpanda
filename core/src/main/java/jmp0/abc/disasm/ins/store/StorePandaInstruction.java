package jmp0.abc.disasm.ins.store;

import jmp0.abc.disasm.ins.PandaInstruction;
import jmp0.abc.disasm.param.PandaInstructionVReg;
import jmp0.abc.disasm.types.PandaOPCode;
import jmp0.abc.file.method.PandaMethod;
import lombok.Getter;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
@Getter
public abstract class StorePandaInstruction extends PandaInstruction {
    private final PandaInstructionVReg destObj;
    public StorePandaInstruction(int pc, PandaOPCode opCode, byte[] param, PandaMethod pandaMethod) {
        super(pc, opCode, param, pandaMethod);
        this.destObj = ((PandaInstructionVReg)this.getParams()[0]);
    }

    @Override
    public String toString() {
        return baseToString() + this.destObj.toString() + " = acc";
    }
}
