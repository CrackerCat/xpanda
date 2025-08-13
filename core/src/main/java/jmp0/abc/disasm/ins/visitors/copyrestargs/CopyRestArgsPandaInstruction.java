package jmp0.abc.disasm.ins.visitors.copyrestargs;

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
public abstract class CopyRestArgsPandaInstruction extends PandaInstruction {
    private final Number index;
    public CopyRestArgsPandaInstruction(int pc, PandaOPCode opCode, byte[] param, PandaMethod pandaMethod) {
        super(pc, opCode, param, pandaMethod);
        this.index = ((PandaInstructionIMM)(this.getParams()[0])).getImm();
    }

    @Override
    public String toString() {
        /*
        fixme
            ls = []
            for (let i = index; i < arguments.length; i++) {
                ls.push[arguments[i]]
            }
            acc = ls
         */
        return baseToString() + "[...p"+index.intValue()+"]";
    }
}
