package jmp0.abc.disasm.ins.visitors.stobj.spr;

import jmp0.abc.disasm.ins.PandaInstruction;
import jmp0.abc.disasm.param.PandaInstructionID;
import jmp0.abc.disasm.param.PandaInstructionVReg;
import jmp0.abc.disasm.types.PandaOPCode;
import jmp0.abc.file.desc.PandaString;
import jmp0.abc.file.method.PandaMethod;
import lombok.Getter;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */

@Getter
public abstract class StoreSuperObjectPandaInstruction extends PandaInstruction {
    private final PandaString prop;
    private final PandaInstructionVReg obj;
    public StoreSuperObjectPandaInstruction(int pc, PandaOPCode opCode, byte[] param, PandaMethod pandaMethod) {
        super(pc, opCode, param, pandaMethod);
        PandaInstructionID pandaInstructionID = (PandaInstructionID) getParams()[1];
        this.prop = (PandaString) pandaInstructionID.getObj();
        this.obj = (PandaInstructionVReg) getParams()[2];
    }

    @Override
    public String toString() {
        return baseToString() + "super["+obj+"]["+prop+"] = acc";
    }
}
