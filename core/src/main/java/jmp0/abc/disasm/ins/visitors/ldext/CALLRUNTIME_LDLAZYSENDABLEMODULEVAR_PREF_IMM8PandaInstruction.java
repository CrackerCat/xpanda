package jmp0.abc.disasm.ins.visitors.ldext;

import jmp0.abc.disasm.types.PandaOPCode;
import jmp0.abc.file.method.PandaMethod;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class CALLRUNTIME_LDLAZYSENDABLEMODULEVAR_PREF_IMM8PandaInstruction extends LoadExternalModuleVarPandaInstruction{
    public CALLRUNTIME_LDLAZYSENDABLEMODULEVAR_PREF_IMM8PandaInstruction(int pc, PandaOPCode opCode, byte[] param, PandaMethod pandaMethod) {
        super(pc, opCode, param, pandaMethod);
    }
}
