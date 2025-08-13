package jmp0.abc.disasm.ins.call.spr;

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
public abstract class SuperCallSpreadPandaInstruction extends PandaInstruction {
    private final PandaInstructionVReg arr;
    public SuperCallSpreadPandaInstruction(int pc, PandaOPCode opCode, byte[] param, PandaMethod pandaMethod) {
        super(pc, opCode, param, pandaMethod);
        this.arr = (PandaInstructionVReg)(this.getParams()[1]);
    }

    @Override
    public String toString() {
        return baseToString() + "acc = super(..."+arr+")";
    }
}
