package jmp0.abc.file.field;

import lombok.Getter;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */

@Getter
public enum PandaFieldTag {
    NOTHING((byte)0x00),
    INT_VALUE((byte)0x01),
    VALUE((byte)0x02),
    RUNTIME_ANNOTATION((byte)0x03),
    ANNOTATION((byte)0x04),
    RUNTIME_TYPE_ANNOTATION((byte)0x05),
    TYPE_ANNOTATION((byte)0x06);
    private final byte value;
    PandaFieldTag(byte value) {
        this.value = value;
    }
}
