package jmp0.abc.disasm.ins.unary;

import jmp0.abc.disasm.types.PandaOPCode;
import jmp0.abc.file.method.PandaMethod;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class DEC_IMM8PandaInstruction extends UnaryPandaInstruction{
    public DEC_IMM8PandaInstruction(int pc, PandaOPCode opCode, byte[] param, PandaMethod pandaMethod) {
        super(pc, opCode, param, pandaMethod, TYPE.DEC);
    }
}
