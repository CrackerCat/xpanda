package jmp0.abc.disasm.ins.jump;

import jmp0.abc.disasm.types.PandaOPCode;
import jmp0.abc.file.method.PandaMethod;
import lombok.SneakyThrows;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class JNEZ_IMM16PandaInstruction extends JumpNotEqualPandaInstruction {
    private final int imm;
    @SneakyThrows
    public JNEZ_IMM16PandaInstruction(int pc, PandaOPCode opCode, byte[] param, PandaMethod pandaMethod) {
        super(pc, opCode, param,pandaMethod );
        this.imm = ByteBuffer.wrap(param).order(ByteOrder.LITTLE_ENDIAN).getShort();
    }

    @Override
    public int getDestinationPC() {
        return getPC() + this.imm;
    }
}
