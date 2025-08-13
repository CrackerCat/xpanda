package jmp0.abc.disasm.ins.visitors.stlex;

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
public abstract class StoreLexPandaInstruction extends PandaInstruction {
    private final Number level;
    private final Number index;
    private boolean isSendAble = false;

    public StoreLexPandaInstruction(int pc, PandaOPCode opCode, byte[] param, PandaMethod pandaMethod) {
        super(pc, opCode, param, pandaMethod);
        this.level = ((PandaInstructionIMM)getParams()[0]).getImm();
        this.index = ((PandaInstructionIMM)getParams()[1]).getImm();
        if (opCode == PandaOPCode.CALLRUNTIME_STSENDABLEVAR_PREF_IMM4_IMM4 || opCode == PandaOPCode.CALLRUNTIME_STSENDABLEVAR_PREF_IMM8_IMM8
        || opCode == PandaOPCode.CALLRUNTIME_WIDESTSENDABLEVAR_PREF_IMM16_IMM16){
            this.isSendAble = true;
        }
    }

    @Override
    public String toString() {
        String name = isSendAble ? "sendable":"local";
        return baseToString() + name + "_" + level + "_" + index + " = acc";
    }
}
