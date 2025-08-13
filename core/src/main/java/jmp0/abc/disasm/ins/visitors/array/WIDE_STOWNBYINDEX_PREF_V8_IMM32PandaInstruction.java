package jmp0.abc.disasm.ins.visitors.array;

import jmp0.abc.disasm.types.PandaOPCode;
import jmp0.abc.file.method.PandaMethod;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class WIDE_STOWNBYINDEX_PREF_V8_IMM32PandaInstruction extends StoreByIndexPandaInstruction{
    public WIDE_STOWNBYINDEX_PREF_V8_IMM32PandaInstruction(int pc, PandaOPCode opCode, byte[] param, PandaMethod pandaMethod) {
        super(pc, opCode, param, pandaMethod);
    }
}
