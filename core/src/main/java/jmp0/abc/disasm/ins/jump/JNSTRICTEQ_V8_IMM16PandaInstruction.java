package jmp0.abc.disasm.ins.jump;

import jmp0.abc.PandaParseException;
import jmp0.abc.disasm.types.PandaOPCode;
import jmp0.abc.file.method.PandaMethod;
import lombok.SneakyThrows;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class JNSTRICTEQ_V8_IMM16PandaInstruction extends JumpPandaInstruction {
    private final int imm;
    @SneakyThrows
    public JNSTRICTEQ_V8_IMM16PandaInstruction(int pc, PandaOPCode opCode, byte[] param, PandaMethod pandaMethod) {
        super(pc, opCode, param, pandaMethod);
        throw new PandaParseException("JNSTRICTEQ_V8_IMM16PandaInstruction not implemented");
    }

    @Override
    public int getDestinationPC() {
        return getPC() + this.imm;
    }
}
