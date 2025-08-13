package jmp0.abc.disasm.ins.call;

import jmp0.abc.disasm.ins.PandaInstruction;
import jmp0.abc.disasm.param.IPandaInstructionParam;
import jmp0.abc.disasm.types.PandaOPCode;
import jmp0.abc.file.method.PandaMethod;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class CALLTHIS0_IMM8_V8PandaInstruction extends CallPandaInstruction {
    public CALLTHIS0_IMM8_V8PandaInstruction(int pc, PandaOPCode opCode, byte[] param, PandaMethod pandaMethod) {
        super(pc, opCode, param, pandaMethod);
        this.thisObj = this.getParams()[1];
        this.callParams = new IPandaInstructionParam[0];
    }
}
