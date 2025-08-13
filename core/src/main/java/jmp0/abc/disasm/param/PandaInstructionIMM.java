package jmp0.abc.disasm.param;

import lombok.Getter;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
@Getter
public final class PandaInstructionIMM implements IPandaInstructionParam{
    private final Number imm;
    public PandaInstructionIMM(Number number){
        this.imm = number;
    }

    @Override
    public String toString() {
        return String.valueOf(imm);
    }
}
