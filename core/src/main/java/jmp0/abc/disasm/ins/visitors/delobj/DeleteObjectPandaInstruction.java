package jmp0.abc.disasm.ins.visitors.delobj;

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
public abstract class DeleteObjectPandaInstruction extends PandaInstruction {
    private final PandaInstructionVReg obj;
    public DeleteObjectPandaInstruction(int pc, PandaOPCode opCode, byte[] param, PandaMethod pandaMethod) {
        super(pc, opCode, param, pandaMethod);
        this.obj = (PandaInstructionVReg) this.getParams()[0];
    }

    @Override
    public String toString() {
        return baseToString() + "delete " + obj.toString() + "[acc]";
    }
}
