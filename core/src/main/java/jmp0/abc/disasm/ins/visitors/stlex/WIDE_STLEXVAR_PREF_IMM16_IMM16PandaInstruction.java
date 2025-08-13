package jmp0.abc.disasm.ins.visitors.stlex;

import jmp0.abc.disasm.types.PandaOPCode;
import jmp0.abc.file.method.PandaMethod;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public class WIDE_STLEXVAR_PREF_IMM16_IMM16PandaInstruction extends StoreLexPandaInstruction{
    public WIDE_STLEXVAR_PREF_IMM16_IMM16PandaInstruction(int pc, PandaOPCode opCode, byte[] param, PandaMethod pandaMethod) {
        super(pc, opCode, param, pandaMethod);
    }
}
