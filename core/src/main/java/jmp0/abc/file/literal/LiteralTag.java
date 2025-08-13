package jmp0.abc.file.literal;

import lombok.Getter;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
@Getter
public enum LiteralTag {
    TAGVALUE((byte)0x00),
    BOOL((byte)0x01),
    INTEGER((byte)0x02),
    FLOAT((byte)0x03),
    DOUBLE((byte)0x04),
    STRING((byte)0x05),
    METHOD((byte)0x06),
    GENERATORMETHOD((byte)0x07),
    ACCESSOR((byte)0x08),
    METHODAFFILIATE((byte)0x09),
    ARRAY_U1((byte)0x0a),
    ARRAY_U8((byte)0x0b),
    ARRAY_I8((byte)0x0c),
    ARRAY_U16((byte)0x0d),
    ARRAY_I16((byte)0x0e),
    ARRAY_U32((byte)0x0f),
    ARRAY_I32((byte)0x10),
    ARRAY_U64((byte)0x11),
    ARRAY_I64((byte)0x12),
    ARRAY_F32((byte)0x13),
    ARRAY_F64((byte)0x14),
    ARRAY_STRING((byte)0x15),
    ASYNCGENERATORMETHOD((byte)0x16),
    LITERALBUFFERINDEX((byte)0x17),
    LITERALARRAY((byte)0x18),
    BUILTINTYPEINDEX((byte)0x19),
    GETTER((byte) 0x1a),
    SETTER((byte) 0x1b),
    NULLVALU((byte) 0xff);
    private final byte value;
    LiteralTag(byte value) {
        this.value = value;
    }

    public static LiteralTag getType(byte value){
        for (LiteralTag literalTag : LiteralTag.values()) {
            if (literalTag.value == value){
                return literalTag;
            }
        }
        return null;
    }
}
