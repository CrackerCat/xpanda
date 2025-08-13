package jmp0.abc.opcode_gen.ins;

import lombok.Data;

import java.util.List;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
@Data
public final class InstructionGenDescription {
    private String name;
    private int opCode;
    private FormatGenDescription format;
    private int flag;
}
