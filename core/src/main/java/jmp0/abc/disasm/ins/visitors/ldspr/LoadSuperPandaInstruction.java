package jmp0.abc.disasm.ins.visitors.ldspr;

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
public abstract class LoadSuperPandaInstruction extends PandaInstruction {
    protected String funcName;
    public LoadSuperPandaInstruction(int pc, PandaOPCode opCode, byte[] param, PandaMethod pandaMethod) {
        super(pc, opCode, param, pandaMethod);
        PandaInstructionID id = (PandaInstructionID) getParams()[1];
        this.funcName = ((PandaString)id.getObj()).getContent();
    }

    @Override
    public String toString() {
        return baseToString() + "super." + funcName;
    }
}
