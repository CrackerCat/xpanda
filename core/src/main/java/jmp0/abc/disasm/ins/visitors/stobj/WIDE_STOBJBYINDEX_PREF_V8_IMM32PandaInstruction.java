package jmp0.abc.disasm.ins.visitors.stobj;

import jmp0.abc.disasm.types.PandaOPCode;
import jmp0.abc.file.method.PandaMethod;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class WIDE_STOBJBYINDEX_PREF_V8_IMM32PandaInstruction extends StoreObjectPandaInstruction{
    public WIDE_STOBJBYINDEX_PREF_V8_IMM32PandaInstruction(int pc, PandaOPCode opCode, byte[] param, PandaMethod pandaMethod) {
        super(pc, opCode, param, pandaMethod);
    }
}
