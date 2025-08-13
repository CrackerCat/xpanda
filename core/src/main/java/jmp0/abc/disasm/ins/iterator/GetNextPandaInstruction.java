package jmp0.abc.disasm.ins.iterator;

import jmp0.abc.disasm.ins.PandaInstruction;
import jmp0.abc.disasm.param.PandaInstructionVReg;
import jmp0.abc.disasm.types.PandaOPCode;
import jmp0.abc.file.method.PandaMethod;
import lombok.Getter;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
@Getter
public abstract class GetNextPandaInstruction extends PandaInstruction {
    private final PandaInstructionVReg iterator;
    public GetNextPandaInstruction(int pc, PandaOPCode opCode, byte[] param, PandaMethod pandaMethod) {
        super(pc, opCode, param, pandaMethod);
        this.iterator = (PandaInstructionVReg) this.getParams()[0];
    }

    @Override
    public String toString() {
        return baseToString() + iterator.toString() + ".next().value";
    }
}
