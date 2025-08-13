package jmp0.abc.disasm.ins.visitors.array;

import jmp0.abc.disasm.types.PandaOPCode;
import jmp0.abc.file.method.PandaMethod;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class STARRAYSPREAD_V8_V8 extends StoreArraySpreadPandaInstruction{
    public STARRAYSPREAD_V8_V8(int pc, PandaOPCode opCode, byte[] param, PandaMethod pandaMethod) {
        super(pc, opCode, param, pandaMethod);
    }
}
