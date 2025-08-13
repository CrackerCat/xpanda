package jmp0.abc.disasm.ins.visitors.stobj;

import jmp0.abc.disasm.ins.PandaInstruction;
import jmp0.abc.disasm.param.IPandaInstructionParam;
import jmp0.abc.disasm.param.PandaInstructionACC;
import jmp0.abc.disasm.types.PandaOPCode;
import jmp0.abc.file.method.PandaMethod;
import lombok.Getter;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */

@Getter
public abstract class StoreObjectPandaInstruction extends PandaInstruction {
    private final IPandaInstructionParam obj;
    private final IPandaInstructionParam key;
    public StoreObjectPandaInstruction(int pc, PandaOPCode opCode, byte[] param, PandaMethod pandaMethod) {
        super(pc, opCode, param, pandaMethod);
        if (opCode == PandaOPCode.STOBJBYNAME_IMM8_ID16_V8 || opCode == PandaOPCode.STOBJBYNAME_IMM16_ID16_V8||
        opCode == PandaOPCode.STOWNBYNAME_IMM8_ID16_V8 || opCode == PandaOPCode.STOWNBYNAME_IMM16_ID16_V8){
            this.obj = this.getParams()[2];
            this.key = this.getParams()[1];
            return;
        }else if (opCode == PandaOPCode.STGLOBALVAR_IMM16_ID16 || opCode == PandaOPCode.TRYSTGLOBALBYNAME_IMM8_ID16
        || opCode == PandaOPCode.TRYSTGLOBALBYNAME_IMM16_ID16){
            PandaInstructionACC acc = new PandaInstructionACC();
            acc.setType(PandaInstructionACC.TYPE.GLOBAL);
            this.obj = acc;
            this.key = this.getParams()[1];
            return;
        }else if (opCode == PandaOPCode.DEFINEFIELDBYNAME_IMM8_ID16_V8 || opCode == PandaOPCode.DEFINEPROPERTYBYNAME_IMM8_ID16_V8){
            this.obj = getParams()[2];
            this.key = getParams()[1];
            return;
        }else if (opCode == PandaOPCode.CALLRUNTIME_DEFINEFIELDBYINDEX_PREF_IMM8_IMM32_V8 || opCode == PandaOPCode.CALLRUNTIME_DEFINEFIELDBYVALUE_PREF_IMM8_V8_V8){
            this.obj = getParams()[2];
            this.key = getParams()[1];
            return;
        }
        this.obj = this.getParams()[1];
        this.key = this.getParams()[2];
    }

    @Override
    public String toString() {
        return baseToString() + this.obj+"["+key+"] = acc";
    }
}
