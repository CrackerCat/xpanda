package jmp0.abc.disasm.ins.unary;

import jmp0.abc.disasm.types.PandaOPCode;
import jmp0.abc.file.method.PandaMethod;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class ISTRUE_NONEPandaInstruction extends UnaryPandaInstruction{
    public ISTRUE_NONEPandaInstruction(int pc, PandaOPCode opCode, byte[] param, PandaMethod pandaMethod) {
        super(pc, opCode, param, pandaMethod, TYPE.ISTRUE);
    }
}
