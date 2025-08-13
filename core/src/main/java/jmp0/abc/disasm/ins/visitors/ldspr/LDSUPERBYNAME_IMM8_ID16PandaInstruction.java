package jmp0.abc.disasm.ins.visitors.ldspr;

import jmp0.abc.disasm.param.PandaInstructionID;
import jmp0.abc.disasm.types.PandaOPCode;
import jmp0.abc.file.desc.PandaString;
import jmp0.abc.file.method.PandaMethod;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class LDSUPERBYNAME_IMM8_ID16PandaInstruction extends LoadSuperPandaInstruction{
    public LDSUPERBYNAME_IMM8_ID16PandaInstruction(int pc, PandaOPCode opCode, byte[] param, PandaMethod pandaMethod) {
        super(pc, opCode, param, pandaMethod);
        this.funcName = ((PandaString)((PandaInstructionID)this.getParams()[1]).getObj()).getContent();
    }
}
