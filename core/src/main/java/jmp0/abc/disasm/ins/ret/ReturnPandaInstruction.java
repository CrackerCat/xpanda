package jmp0.abc.disasm.ins.ret;

import jmp0.abc.disasm.ins.PandaInstruction;
import jmp0.abc.disasm.types.PandaOPCode;
import jmp0.abc.file.method.PandaMethod;
import lombok.Getter;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
@Getter
public abstract class ReturnPandaInstruction extends PandaInstruction {
    protected boolean undefined;
    public ReturnPandaInstruction(int pc, PandaOPCode opCode, byte[] param, PandaMethod pandaMethod,boolean undefined) {
        super(pc, opCode, param, pandaMethod);
        this.undefined = undefined;
    }

    @Override
    public String toString() {
        if (undefined) return baseToString() + "return";
        else return baseToString() + "return acc";
    }
}
