package jmp0.abc.disasm.ins.call;

import jmp0.abc.disasm.param.IPandaInstructionParam;
import jmp0.abc.disasm.types.PandaOPCode;
import jmp0.abc.file.method.PandaMethod;
/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class APPLY_IMM8_V8_V8PandaInstruction extends CallPandaInstruction{
    public APPLY_IMM8_V8_V8PandaInstruction(int pc, PandaOPCode opCode, byte[] param, PandaMethod method) {
        super(pc, opCode, param, method);
        this.thisObj = this.getParams()[1];
        this.callParams = new IPandaInstructionParam[]{this.getParams()[2]};
        this.apply = true;
    }
}
