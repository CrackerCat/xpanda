package jmp0.abc.file.method;

import lombok.Getter;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
@Getter
public enum PandaMethodTag {
    NOTHING((byte) 0x00),
    CODE ((byte)0x01),
    SOURCE_LANG((byte)0x02),
    RUNTIME_ANNOTATION ((byte)0x03),
    RUNTIME_PARAM_ANNOTATION ((byte)0x04),
    DEBUG_INFO ((byte)0x05),
    ANNOTATION ((byte)0x06),
    PARAM_ANNOTATION ((byte)0x07),
    TYPE_ANNOTATION ((byte)0x08),
    RUNTIME_TYPE_ANNOTATION ((byte)0x09);

    private final byte value;
    PandaMethodTag(byte value) {
        this.value = value;
    }
}
