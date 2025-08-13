package jmp0.abc.disasm.ins.jump;

import jmp0.abc.disasm.types.PandaOPCode;
import jmp0.abc.file.method.PandaMethod;
import lombok.SneakyThrows;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class JMP_IMM8PandaInstruction extends JumpDirectPandaInstruction {
    private final int imm;
    @SneakyThrows
    public JMP_IMM8PandaInstruction(int pc, PandaOPCode opCode, byte[] param, PandaMethod pandaMethod) {
        super(pc, opCode, param,pandaMethod);
        this.imm = param[0];
    }

    @Override
    public int getDestinationPC() {
        return getPC() + this.imm;
    }
}
