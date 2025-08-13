package jmp0.abc.disasm.ins.call;

import jmp0.abc.disasm.types.PandaOPCode;
import jmp0.abc.file.method.PandaMethod;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class WIDE_CALLRANGE_PREF_IMM16_V8PandaInstruction extends CallPandaInstruction{
    public WIDE_CALLRANGE_PREF_IMM16_V8PandaInstruction(int pc, PandaOPCode opCode, byte[] param, PandaMethod method) {
        super(pc, opCode, param, method);
    }
}
