package jmp0.abc.disasm.ins.visitors.ldlex;

import jmp0.abc.disasm.ins.PandaInstruction;
import jmp0.abc.disasm.param.PandaInstructionIMM;
import jmp0.abc.disasm.types.PandaOPCode;
import jmp0.abc.file.method.PandaMethod;
import lombok.Getter;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
@Getter
public abstract class LoadLexPandaInstruction extends PandaInstruction {
    private final Number level;
    private final Number index;
    private boolean isSendAble = false;
    public LoadLexPandaInstruction(int pc, PandaOPCode opCode, byte[] param, PandaMethod pandaMethod) {
        super(pc, opCode, param, pandaMethod);
        this.level = ((PandaInstructionIMM)getParams()[0]).getImm();
        this.index = ((PandaInstructionIMM)getParams()[1]).getImm();
        if (opCode == PandaOPCode.CALLRUNTIME_LDSENDABLEVAR_PREF_IMM4_IMM4 || opCode == PandaOPCode.CALLRUNTIME_LDSENDABLEVAR_PREF_IMM8_IMM8
        || opCode == PandaOPCode.CALLRUNTIME_WIDELDSENDABLEVAR_PREF_IMM16_IMM16){
            this.isSendAble = true;
        }
    }

    public String toSignature(){
        String name = isSendAble ? "sendable":"local";
        return name + "_" + level + "_" + index;
    }

    @Override
    public String toString() {
        return baseToString() + toSignature() + " = acc";
    }
}
