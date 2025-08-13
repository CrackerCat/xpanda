package jmp0.abc.disasm.ins.call;

import jmp0.abc.disasm.param.IPandaInstructionParam;
import jmp0.abc.disasm.types.PandaOPCode;
import jmp0.abc.file.method.PandaMethod;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class CALLARG1_IMM8_V8PandaInstruction extends CallPandaInstruction{
    public CALLARG1_IMM8_V8PandaInstruction(int pc, PandaOPCode opCode, byte[] param, PandaMethod method) {
        super(pc, opCode, param, method);
        this.thisObj = null;
        this.callParams = new IPandaInstructionParam[]{this.getParams()[1]};
    }
}
