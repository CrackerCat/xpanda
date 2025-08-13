package jmp0.abc.file.type;

import lombok.Getter;

import java.util.Locale;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */

@Getter
public enum PandaRawType {
    VOID((byte)  0x01 ),
    U1((byte)  0x02 ),
    I8((byte)  0x03 ),
    U8((byte)  0x04 ),
    I16((byte) 0x05 ),
    U16((byte) 0x06 ),
    I32((byte) 0x07 ),
    U32((byte) 0x08 ),
    F32((byte) 0x09 ),
    F64((byte) 0x0a ),
    I64((byte) 0x0b ),
    U64((byte) 0x0c ),
    REF((byte) 0X0d ),
    TAG((byte) 0x0e );
    private final byte value;

    PandaRawType(byte value) {
        this.value = value;
    }

    public boolean isPrimitive(){
        return this != REF;
    }

    public PandaRawType toFieldType(){
        return PandaRawType.getType((byte) (getValue() + U1.getValue()));
    }

    public static PandaRawType getType(byte value){
        for (PandaRawType pandaRawType : PandaRawType.values()) {
            if (pandaRawType.value == value){
                return pandaRawType;
            }
        }
        return null;
    }

    public String getTypeString(boolean isField) {
        PandaRawType type;
        if (isField) type = toFieldType();
        else type = this;
        if (type == TAG){
            return "any";
        }else if (type == REF){
            return "ref";
        }else return type.name().toLowerCase(Locale.ROOT);
    }
}
