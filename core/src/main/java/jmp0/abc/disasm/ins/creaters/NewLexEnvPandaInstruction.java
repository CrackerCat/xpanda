package jmp0.abc.disasm.ins.creaters;

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
public abstract class NewLexEnvPandaInstruction extends PandaInstruction {
    private final Number imm;
    private boolean isSendAble = false;
    public NewLexEnvPandaInstruction(int pc, PandaOPCode opCode, byte[] param, PandaMethod pandaMethod) {
        super(pc, opCode, param, pandaMethod);
        this.imm = ((PandaInstructionIMM)getParams()[0]).getImm();
        if (opCode == PandaOPCode.CALLRUNTIME_NEWSENDABLEENV_PREF_IMM8 || opCode == PandaOPCode.CALLRUNTIME_WIDENEWSENDABLEENV_PREF_IMM16){
            this.isSendAble = true;
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(baseToString());
        builder.append("let ");
        for (int i = 0; i < imm.intValue(); i++) {
            if (isSendAble){
                builder.append("sendable").append(i);
            }else {
                builder.append("lex").append(i);
            }
            if (i != imm.intValue() -1) builder.append(',');
        }
        return builder.toString();
    }
}
