package jmp0.abc.disasm.ins.visitors.ldlex;

import jmp0.abc.disasm.types.PandaOPCode;
import jmp0.abc.file.method.PandaMethod;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class CALLRUNTIME_LDSENDABLEVAR_PREF_IMM8_IMM8PandaInstruction extends LoadLexPandaInstruction{
    public CALLRUNTIME_LDSENDABLEVAR_PREF_IMM8_IMM8PandaInstruction(int pc, PandaOPCode opCode, byte[] param, PandaMethod pandaMethod) {
        super(pc, opCode, param, pandaMethod);
    }
}
