package jmp0.abc.file.debug;

import lombok.Getter;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */

@Getter
public enum PandaLineNumberProgramOpcode {
    END_SEQUENCE(0x00),
    ADVANCE_PC(0x01),
    ADVANCE_LINE(0x02),
    START_LOCAL(0x03),
    START_LOCAL_EXTENDED(0x04),
    END_LOCAL(0x05),
    RESTART_LOCAL(0x06),
    SET_PROLOGUE_END(0x07),
    SET_EPILOGUE_BEGIN(0x08),
    SET_FILE(0x09),
    SET_SOURCE_CODE(0x0a),
    SET_COLUMN(0X0b);

    private final int opcode;
    public static final int LINE_RANGE = 15;
    public static final int LINE_BASE = -4;
    public static final int OPCODE_BASE = SET_COLUMN.opcode + 1;

    PandaLineNumberProgramOpcode(int opcode) {
        this.opcode = opcode;
    }
    public static PandaLineNumberProgramOpcode getType(byte value){
        for (PandaLineNumberProgramOpcode opcode : PandaLineNumberProgramOpcode.values()) {
            if (opcode.opcode == value){
                return opcode;
            }
        }
        return null;
    }

}
