package jmp0.abc.disasm.ins.trw;

import jmp0.abc.disasm.types.PandaOPCode;
import jmp0.abc.file.method.PandaMethod;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class THROW_UNDEFINEDIFHOLEWITHNAME_PREF_ID16PandaInstruction extends AssertPandaInstruction{
    public THROW_UNDEFINEDIFHOLEWITHNAME_PREF_ID16PandaInstruction(int pc, PandaOPCode opCode, byte[] param, PandaMethod pandaMethod) {
        super(pc, opCode, param, pandaMethod);
    }
}
