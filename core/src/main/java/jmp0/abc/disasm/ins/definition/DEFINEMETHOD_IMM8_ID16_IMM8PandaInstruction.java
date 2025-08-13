package jmp0.abc.disasm.ins.definition;

import jmp0.abc.disasm.types.PandaOPCode;
import jmp0.abc.file.method.PandaMethod;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class DEFINEMETHOD_IMM8_ID16_IMM8PandaInstruction extends DefineFuncPandaInstruction{
    public DEFINEMETHOD_IMM8_ID16_IMM8PandaInstruction(int pc, PandaOPCode opCode, byte[] param, PandaMethod pandaMethod) {
        super(pc, opCode, param, pandaMethod);
    }
}
