package jmp0.abc.res.types;

import lombok.Getter;

import java.util.Arrays;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
@Getter
public enum PandaResourceResType {
    ELEMENT(0),
    RAW(6),
    INTEGER(8),
    STRING(9),
    STRARRAY(10),
    INTARRAY(11),
    BOOLEAN(12),
    COLOR(14),
    ID(15),
    THEME(16),
    PLURAL(17),
    FLOAT(18),
    MEDIA(19),
    PROF(20),
    PATTERN(22),
    SYMBOL(23),
    RES(24),
    INVALID_RES_TYPE(-1);
    private final int value;
    PandaResourceResType(int value){
        this.value = value;
    }

    public static PandaResourceResType resolve(int value){
        return Arrays.stream(PandaResourceResType.values()).filter(v->v.value==value).findFirst().orElse(INVALID_RES_TYPE);
    }
}
