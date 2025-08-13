package jmp0.abc.res.types;

import lombok.Getter;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
@Getter
public enum PandaResourceKeyType {
    LANGUAGE(0),
    REGION(1),
    RESOLUTION(2),
    ORIENTATION(3),
    DEVICETYPE(4),
    SCRIPT(5),
    NIGHTMODE(6),
    MCC(7),
    MNC(8),
    RESERVER(9),
    INPUTDEVICE(10),
    KEY_TYPE_MAX(11);
    private final int value;
    PandaResourceKeyType(int value){
        this.value = value;
    }

    public static PandaResourceKeyType resolve(int value){
        return PandaResourceKeyType.values()[value];
    }
}
