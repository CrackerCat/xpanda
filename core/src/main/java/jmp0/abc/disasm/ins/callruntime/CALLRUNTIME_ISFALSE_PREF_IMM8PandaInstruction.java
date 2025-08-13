package jmp0.abc.disasm.ins.callruntime;

import jmp0.abc.disasm.types.PandaOPCode;
import jmp0.abc.file.method.PandaMethod;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class CALLRUNTIME_ISFALSE_PREF_IMM8PandaInstruction extends CallRuntimePandaInstruction{
    public CALLRUNTIME_ISFALSE_PREF_IMM8PandaInstruction(int pc, PandaOPCode opCode, byte[] param, PandaMethod pandaMethod) {
        super(pc, opCode, param, pandaMethod);
    }
}
