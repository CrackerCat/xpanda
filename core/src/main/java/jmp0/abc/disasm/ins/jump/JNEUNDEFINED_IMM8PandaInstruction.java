package jmp0.abc.disasm.ins.jump;

import jmp0.abc.PandaParseException;
import jmp0.abc.disasm.types.PandaOPCode;
import jmp0.abc.file.method.PandaMethod;
import lombok.SneakyThrows;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class JNEUNDEFINED_IMM8PandaInstruction extends JumpPandaInstruction {
    private final int imm;
    @SneakyThrows
    public JNEUNDEFINED_IMM8PandaInstruction(int pc, PandaOPCode opCode, byte[] param, PandaMethod pandaMethod) {
        super(pc, opCode, param,pandaMethod );
        throw new PandaParseException("JNEUNDEFINED_IMM8PandaInstruction not implemented");
    }

    @Override
    public int getDestinationPC() {
        return getPC() + this.imm;
    }
}
