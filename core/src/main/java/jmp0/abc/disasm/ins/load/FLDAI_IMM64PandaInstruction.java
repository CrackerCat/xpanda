package jmp0.abc.disasm.ins.load;

import jmp0.abc.disasm.param.PandaInstructionACC;
import jmp0.abc.disasm.types.PandaOPCode;
import jmp0.abc.file.method.PandaMethod;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class FLDAI_IMM64PandaInstruction extends LoadPandaInstruction{
    public FLDAI_IMM64PandaInstruction(int pc, PandaOPCode opCode, byte[] param, PandaMethod pandaMethod) {
        super(pc, opCode, param, pandaMethod);
        this.type = PandaInstructionACC.TYPE.OBJECT;
        this.param = this.getParams()[0];
    }
}
