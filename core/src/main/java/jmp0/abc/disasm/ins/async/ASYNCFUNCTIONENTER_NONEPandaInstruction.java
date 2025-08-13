package jmp0.abc.disasm.ins.async;

import jmp0.abc.disasm.types.PandaOPCode;
import jmp0.abc.file.method.PandaMethod;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class ASYNCFUNCTIONENTER_NONEPandaInstruction extends AsyncPandaInstruction {
    public ASYNCFUNCTIONENTER_NONEPandaInstruction(int pc, PandaOPCode opCode, byte[] param, PandaMethod pandaMethod) {
        super(pc, opCode, param, pandaMethod);
    }
}
