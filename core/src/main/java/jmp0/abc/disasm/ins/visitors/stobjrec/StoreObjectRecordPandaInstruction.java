package jmp0.abc.disasm.ins.visitors.stobjrec;

import jmp0.abc.disasm.ins.PandaInstruction;
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
public abstract class StoreObjectRecordPandaInstruction extends PandaInstruction {
    public enum TYPE{
        CONST,
        LET
    }
    private final TYPE type;
    private final PandaInstructionID name;

    public StoreObjectRecordPandaInstruction(int pc, PandaOPCode opCode, byte[] param, PandaMethod pandaMethod) {
        super(pc, opCode, param, pandaMethod);
        if (opCode == PandaOPCode.STCONSTTOGLOBALRECORD_IMM16_ID16){
            type = TYPE.CONST;
        } else if (opCode == PandaOPCode.STTOGLOBALRECORD_IMM16_ID16) {
            type = TYPE.LET;
        }else type = TYPE.LET;
        this.name = (PandaInstructionID) getParams()[1];
        System.out.println();
    }

    @Override
    public String toString() {
        return baseToString() + this.type.name().toLowerCase() + " " + ((PandaString)this.name.getObj()).getContent() + " = acc";
    }
}
