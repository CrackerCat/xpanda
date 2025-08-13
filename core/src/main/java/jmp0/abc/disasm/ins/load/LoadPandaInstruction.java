package jmp0.abc.disasm.ins.load;

import jmp0.abc.disasm.ins.PandaInstruction;
import jmp0.abc.disasm.param.IPandaInstructionParam;
import jmp0.abc.disasm.param.PandaInstructionACC;
import jmp0.abc.disasm.param.PandaInstructionID;
import jmp0.abc.disasm.types.PandaOPCode;
import jmp0.abc.file.desc.PandaString;
import jmp0.abc.file.method.PandaMethod;
import lombok.Getter;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
@Getter
public abstract class LoadPandaInstruction extends PandaInstruction {
    protected PandaInstructionACC.TYPE type = PandaInstructionACC.TYPE.OBJECT;
    protected IPandaInstructionParam param = null;
    private boolean isSendAble = false;
    public LoadPandaInstruction(int pc, PandaOPCode opCode, byte[] param, PandaMethod pandaMethod) {
        super(pc, opCode, param, pandaMethod);
        if (opCode == PandaOPCode.CALLRUNTIME_LDSENDABLECLASS_PREF_IMM16){
            this.isSendAble = true;
        }
    }

    public String selfToString(){
        if (isSendAble) return "this";
        if (this.type == PandaInstructionACC.TYPE.BIGINT || this.type == PandaInstructionACC.TYPE.STRING) return ((PandaString)(((PandaInstructionID)param).getObj())).getContent();
        if (this.type == PandaInstructionACC.TYPE.NAN) return "NaN";
        if (this.type == PandaInstructionACC.TYPE.INFINITY) return "Infinity";
        if (this.type == PandaInstructionACC.TYPE.GLOBAL) return "globalThis";
        if (this.type != PandaInstructionACC.TYPE.OBJECT) return type.name().toLowerCase();
        return param.toString();
    }

    @Override
    public String toString() {
        return baseToString() + selfToString();
    }
}
