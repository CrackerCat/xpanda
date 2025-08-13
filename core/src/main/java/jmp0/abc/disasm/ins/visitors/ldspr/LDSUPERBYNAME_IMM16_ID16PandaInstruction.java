package jmp0.abc.disasm.ins.visitors.ldspr;

import jmp0.abc.disasm.types.PandaOPCode;
import jmp0.abc.file.method.PandaMethod;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class LDSUPERBYNAME_IMM16_ID16PandaInstruction extends LoadSuperPandaInstruction{
    public LDSUPERBYNAME_IMM16_ID16PandaInstruction(int pc, PandaOPCode opCode, byte[] param, PandaMethod pandaMethod) {
        super(pc, opCode, param, pandaMethod);
    }
}
