package jmp0.abc.disasm.ins.visitors.stlex;

import jmp0.abc.disasm.types.PandaOPCode;
import jmp0.abc.file.method.PandaMethod;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class STLEXVAR_IMM8_IMM8PandaInstruction extends StoreLexPandaInstruction{
    public STLEXVAR_IMM8_IMM8PandaInstruction(int pc, PandaOPCode opCode, byte[] param, PandaMethod pandaMethod) {
        super(pc, opCode, param, pandaMethod);
    }
}
