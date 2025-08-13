package jmp0.abc.disasm.ins.visitors.stmod;

import jmp0.abc.disasm.types.PandaOPCode;
import jmp0.abc.file.method.PandaMethod;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class WIDE_STMODULEVAR_PREF_IMM16PandaInstruction extends StoreModuleVarPandaInstruction {
    public WIDE_STMODULEVAR_PREF_IMM16PandaInstruction(int pc, PandaOPCode opCode, byte[] param, PandaMethod pandaMethod) {
        super(pc, opCode, param, pandaMethod);
    }
}
