package jmp0.abc.disasm.ins.iterator;

import jmp0.abc.disasm.ins.PandaInstruction;
import jmp0.abc.disasm.types.PandaOPCode;
import jmp0.abc.file.method.PandaMethod;
import lombok.Getter;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
@Getter
public abstract class GetIteratorPandaInstruction extends PandaInstruction {
    protected boolean isProp = false;
    public GetIteratorPandaInstruction(int pc, PandaOPCode opCode, byte[] param, PandaMethod pandaMethod) {
        super(pc, opCode, param, pandaMethod);
    }

    @Override
    public String toString() {
        String obj;
        if (isProp) obj = "Object.keys(acc)";
        else obj = "acc";
        return baseToString() + obj + "[Symbol.iterator]()";
    }
}
