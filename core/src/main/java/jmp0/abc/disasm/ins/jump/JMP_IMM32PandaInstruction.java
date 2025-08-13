package jmp0.abc.disasm.ins.jump;

import jmp0.abc.PandaParseException;
import jmp0.abc.disasm.types.PandaOPCode;
import jmp0.abc.file.method.PandaMethod;
import lombok.SneakyThrows;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class JMP_IMM32PandaInstruction extends JumpDirectPandaInstruction {
    private final int imm;
    @SneakyThrows
    public JMP_IMM32PandaInstruction(int pc, PandaOPCode opCode, byte[] param, PandaMethod pandaMethod) {
        super(pc, opCode, param, pandaMethod);
        this.imm = ByteBuffer.wrap(param).order(ByteOrder.LITTLE_ENDIAN).getInt();
    }

    @Override
    public int getDestinationPC() {
        return getPC() + this.imm;
    }
}
