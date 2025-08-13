package jmp0.abc.disasm.ins.definition;

import jmp0.abc.disasm.ins.PandaInstruction;
import jmp0.abc.disasm.param.PandaInstructionID;
import jmp0.abc.disasm.param.PandaInstructionIMM;
import jmp0.abc.disasm.types.PandaOPCode;
import jmp0.abc.file.method.PandaMethod;
import lombok.Getter;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
@Getter
public abstract class DefineFuncPandaInstruction extends PandaInstruction {
    private final PandaMethod func;
    private final Number paramSize;
    public DefineFuncPandaInstruction(int pc, PandaOPCode opCode, byte[] param, PandaMethod pandaMethod) {
        super(pc, opCode, param, pandaMethod);
        this.func = (PandaMethod) ((PandaInstructionID)(this.getParams()[1])).getObj();
        this.paramSize =  ((PandaInstructionIMM)(this.getParams()[2])).getImm();
    }

    @Override
    public String toString() {
        return baseToString() + "method:" +this.func.getName().getContent()+"(..."+paramSize.intValue()+"){...}";
    }
}
