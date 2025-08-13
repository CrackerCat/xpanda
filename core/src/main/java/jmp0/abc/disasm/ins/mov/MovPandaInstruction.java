package jmp0.abc.disasm.ins.mov;

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
public abstract class MovPandaInstruction extends PandaInstruction {
    private final PandaInstructionVReg destObj;
    private final PandaInstructionVReg srcObj;
    public MovPandaInstruction(int pc, PandaOPCode opCode, byte[] param, PandaMethod pandaMethod) {
        super(pc, opCode, param, pandaMethod);
        this.destObj = ((PandaInstructionVReg)this.getParams()[0]);
        this.srcObj = ((PandaInstructionVReg)this.getParams()[1]);
    }

    @Override
    public String toString() {
        return baseToString() + this.destObj.toString() + " = " + this.srcObj.toString();
    }
}
