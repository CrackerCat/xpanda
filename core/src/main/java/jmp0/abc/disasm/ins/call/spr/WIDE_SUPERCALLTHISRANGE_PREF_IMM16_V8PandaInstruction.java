package jmp0.abc.disasm.ins.call.spr;

import jmp0.abc.disasm.types.PandaOPCode;
import jmp0.abc.file.method.PandaMethod;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class WIDE_SUPERCALLTHISRANGE_PREF_IMM16_V8PandaInstruction extends SuperCallPandaInstruction{
    public WIDE_SUPERCALLTHISRANGE_PREF_IMM16_V8PandaInstruction(int pc, PandaOPCode opCode, byte[] param, PandaMethod pandaMethod) {
        super(pc, opCode, param, pandaMethod);
    }
}
