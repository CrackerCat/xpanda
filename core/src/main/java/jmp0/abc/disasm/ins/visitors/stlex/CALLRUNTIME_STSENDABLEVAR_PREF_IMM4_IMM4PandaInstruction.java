package jmp0.abc.disasm.ins.visitors.stlex;

import jmp0.abc.disasm.types.PandaOPCode;
import jmp0.abc.file.method.PandaMethod;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class CALLRUNTIME_STSENDABLEVAR_PREF_IMM4_IMM4PandaInstruction extends StoreLexPandaInstruction{
    public CALLRUNTIME_STSENDABLEVAR_PREF_IMM4_IMM4PandaInstruction(int pc, PandaOPCode opCode, byte[] param, PandaMethod pandaMethod) {
        super(pc, opCode, param, pandaMethod);
    }
}
