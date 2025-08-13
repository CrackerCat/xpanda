package jmp0.abc.disasm.ins.load;

import jmp0.abc.disasm.param.PandaInstructionACC;
import jmp0.abc.disasm.types.PandaOPCode;
import jmp0.abc.file.method.PandaMethod;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class LDNULL_NONEPandaInstruction extends LoadPandaInstruction{
    public LDNULL_NONEPandaInstruction(int pc, PandaOPCode opCode, byte[] param, PandaMethod pandaMethod) {
        super(pc, opCode, param, pandaMethod);
        this.type = PandaInstructionACC.TYPE.NULL;
    }
}
