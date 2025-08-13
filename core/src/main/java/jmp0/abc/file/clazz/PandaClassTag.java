package jmp0.abc.file.clazz;
import lombok.Getter;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
@Getter
public enum PandaClassTag {

    NOTHING((byte)0x00),
    INTERFACES((byte)0x01),
    SOURCE_LANG((byte)0x02),
    RUNTIME_ANNOTATION((byte)0x03),
    ANNOTATION((byte)0x04),
    RUNTIME_TYPE_ANNOTATION((byte)0x05),
    TYPE_ANNOTATION((byte)0x06),
    SOURCE_FILE((byte) 0x07);
    private final byte value;
    PandaClassTag(byte value) {
        this.value = value;
    }
}
