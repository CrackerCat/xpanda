package jmp0.abc.disasm.ins.yield;

import jmp0.abc.disasm.param.PandaInstructionVReg;
import jmp0.abc.disasm.types.PandaOPCode;
import jmp0.abc.file.method.PandaMethod;
import lombok.Getter;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
@Getter
public final class CREATEITERRESULTOBJ_V8_V8PandaInstruction extends CreateGeneratorObjectPandaInstruction {
    private final PandaInstructionVReg yieldReg;
    public CREATEITERRESULTOBJ_V8_V8PandaInstruction(int pc, PandaOPCode opCode, byte[] param, PandaMethod pandaMethod) {
        super(pc, opCode, param, pandaMethod);
        this.yieldReg = (PandaInstructionVReg) getParams()[0];
    }
}
