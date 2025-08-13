package jmp0.abc.disasm.ins.creaters.spread;

import jmp0.abc.disasm.types.PandaOPCode;
import jmp0.abc.file.method.PandaMethod;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class NEWOBJAPPLY_IMM8_V8PandaInstruction extends NewObjectSpreadPandaInstruction{
    public NEWOBJAPPLY_IMM8_V8PandaInstruction(int pc, PandaOPCode opCode, byte[] param, PandaMethod pandaMethod) {
        super(pc, opCode, param, pandaMethod);
    }
}
