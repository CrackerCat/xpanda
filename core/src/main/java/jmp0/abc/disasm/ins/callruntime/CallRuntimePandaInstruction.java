package jmp0.abc.disasm.ins.callruntime;

import jmp0.abc.disasm.ins.PandaInstruction;
import jmp0.abc.disasm.types.PandaOPCode;
import jmp0.abc.file.method.PandaMethod;
import lombok.Getter;

@Getter
public abstract class CallRuntimePandaInstruction extends PandaInstruction {
    public enum TYPE{
        NONE,
        CALL_INIT,
        IS_FALSE,
        IS_TRUE
    }
    private final TYPE type;
    public CallRuntimePandaInstruction(int pc, PandaOPCode opCode, byte[] param, PandaMethod pandaMethod) {
        super(pc, opCode, param, pandaMethod);
        if (opCode == PandaOPCode.CALLRUNTIME_CALLINIT_PREF_IMM8_V8){
            this.type = TYPE.CALL_INIT;
        }else if (opCode == PandaOPCode.CALLRUNTIME_ISFALSE_PREF_IMM8){
            this.type = TYPE.IS_FALSE;
        }else if (opCode == PandaOPCode.CALLRUNTIME_ISTRUE_PREF_IMM8){
            this.type = TYPE.IS_TRUE;
        }else {
            this.type = TYPE.NONE;
        }
    }
}
