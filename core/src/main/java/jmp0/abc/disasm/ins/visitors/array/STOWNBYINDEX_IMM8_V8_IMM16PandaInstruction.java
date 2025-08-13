package jmp0.abc.disasm.ins.visitors.array;

import jmp0.abc.disasm.types.PandaOPCode;
import jmp0.abc.file.method.PandaMethod;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class STOWNBYINDEX_IMM8_V8_IMM16PandaInstruction extends StoreByIndexPandaInstruction{
    public STOWNBYINDEX_IMM8_V8_IMM16PandaInstruction(int pc, PandaOPCode opCode, byte[] param, PandaMethod pandaMethod) {
        super(pc, opCode, param, pandaMethod);
    }
}
