package jmp0.abc.disasm.ins.creaters;

import jmp0.abc.disasm.types.PandaOPCode;
import jmp0.abc.file.method.PandaMethod;
/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */

public final class WIDE_NEWLEXENV_PREF_IMM16PandaInstruction extends NewLexEnvPandaInstruction {
    public WIDE_NEWLEXENV_PREF_IMM16PandaInstruction(int pc, PandaOPCode opCode, byte[] param, PandaMethod pandaMethod) {
        super(pc, opCode, param, pandaMethod);
    }
}
