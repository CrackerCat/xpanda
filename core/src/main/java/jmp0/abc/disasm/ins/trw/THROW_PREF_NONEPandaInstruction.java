package jmp0.abc.disasm.ins.trw;

import jmp0.abc.disasm.ins.PandaInstruction;
import jmp0.abc.disasm.types.PandaOPCode;
import jmp0.abc.file.method.PandaMethod;

public final class THROW_PREF_NONEPandaInstruction extends PandaInstruction {
    public THROW_PREF_NONEPandaInstruction(int pc, PandaOPCode opCode, byte[] param, PandaMethod pandaMethod) {
        super(pc, opCode, param, pandaMethod);
    }
}
