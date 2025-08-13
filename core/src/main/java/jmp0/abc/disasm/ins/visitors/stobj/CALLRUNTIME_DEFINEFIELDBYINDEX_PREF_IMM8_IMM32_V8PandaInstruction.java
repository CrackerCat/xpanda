package jmp0.abc.disasm.ins.visitors.stobj;

import jmp0.abc.disasm.types.PandaOPCode;
import jmp0.abc.file.method.PandaMethod;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class CALLRUNTIME_DEFINEFIELDBYINDEX_PREF_IMM8_IMM32_V8PandaInstruction extends StoreObjectPandaInstruction {
    public CALLRUNTIME_DEFINEFIELDBYINDEX_PREF_IMM8_IMM32_V8PandaInstruction(int pc, PandaOPCode opCode, byte[] param, PandaMethod pandaMethod) {
        super(pc, opCode, param, pandaMethod);
    }
}
