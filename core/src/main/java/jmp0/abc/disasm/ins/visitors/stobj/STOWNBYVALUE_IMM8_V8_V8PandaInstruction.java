package jmp0.abc.disasm.ins.visitors.stobj;

import jmp0.abc.disasm.types.PandaOPCode;
import jmp0.abc.file.method.PandaMethod;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class STOWNBYVALUE_IMM8_V8_V8PandaInstruction extends StoreObjectPandaInstruction{
    public STOWNBYVALUE_IMM8_V8_V8PandaInstruction(int pc, PandaOPCode opCode, byte[] param, PandaMethod pandaMethod) {
        super(pc, opCode, param, pandaMethod);
    }
}
