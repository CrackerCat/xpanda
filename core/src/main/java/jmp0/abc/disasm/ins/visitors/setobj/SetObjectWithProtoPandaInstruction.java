package jmp0.abc.disasm.ins.visitors.setobj;

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
public abstract class SetObjectWithProtoPandaInstruction extends PandaInstruction {
    private final PandaInstructionVReg proto;
    public SetObjectWithProtoPandaInstruction(int pc, PandaOPCode opCode, byte[] param, PandaMethod pandaMethod) {
        super(pc, opCode, param, pandaMethod);
        this.proto = (PandaInstructionVReg) this.getParams()[1];
    }

    @Override
    public String toString() {
        return baseToString() + "Object.setPrototypeOf(acc,"+this.proto+")";
    }
}
