package jmp0.abc.disasm.ins.visitors.global;

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
public abstract class LoadGlobalPandaInstruction extends PandaInstruction {
    private final PandaString name;
    public LoadGlobalPandaInstruction(int pc, PandaOPCode opCode, byte[] param, PandaMethod pandaMethod) {
        super(pc, opCode, param, pandaMethod);
        this.name = (PandaString)((((PandaInstructionID)this.getParams()[1]).getObj()));
    }

    @Override
    public String toString() {
        return baseToString() + name;
    }
}
