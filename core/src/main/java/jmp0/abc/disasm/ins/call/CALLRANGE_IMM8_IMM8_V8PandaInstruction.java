package jmp0.abc.disasm.ins.call;

import jmp0.abc.disasm.param.IPandaInstructionParam;
import jmp0.abc.disasm.param.PandaInstructionIMM;
import jmp0.abc.disasm.param.PandaInstructionVReg;
import jmp0.abc.disasm.types.PandaOPCode;
import jmp0.abc.file.method.PandaMethod;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class CALLRANGE_IMM8_IMM8_V8PandaInstruction extends CallPandaInstruction{
    public CALLRANGE_IMM8_IMM8_V8PandaInstruction(int pc, PandaOPCode opCode, byte[] param, PandaMethod method) {
        super(pc, opCode, param, method);
        this.thisObj = null;
        PandaInstructionVReg callParam = (PandaInstructionVReg) this.getParams()[2];
        int size = ((PandaInstructionIMM)this.getParams()[1]).getImm().intValue();
        this.callParams = new IPandaInstructionParam[size];
        for (int i = 0; i < size; i++) {
            this.callParams[i] = new PandaInstructionVReg((short) ((callParam).getIndex()+i), PandaInstructionVReg.TYPE.VREG,method);
        }
    }
}
