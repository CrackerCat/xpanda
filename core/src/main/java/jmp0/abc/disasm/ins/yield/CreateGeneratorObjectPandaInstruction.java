package jmp0.abc.disasm.ins.yield;

import jmp0.abc.disasm.ins.PandaInstruction;
import jmp0.abc.disasm.types.PandaOPCode;
import jmp0.abc.file.method.PandaMethod;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public abstract class CreateGeneratorObjectPandaInstruction extends PandaInstruction {
    public CreateGeneratorObjectPandaInstruction(int pc, PandaOPCode opCode, byte[] param, PandaMethod pandaMethod) {
        super(pc, opCode, param, pandaMethod);
    }
}
