package jmp0.abc.res.types;

import lombok.Getter;

import java.util.Arrays;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
@Getter
public enum PandaResourceDeviceType {
    PHONE(0,"phone"),
    TABLET(1,"tablet"),
    CAR(2,"car"),
    RESERVEED(3,"reserved"),
    TV(4,"tv"),
    WEARABLE(6,"wearable"),
    TWOINONE(7,"2in1");

    private final int value;
    private final String name;
    PandaResourceDeviceType(int value,String name){
        this.value = value;
        this.name = name;
    }

    public static PandaResourceDeviceType resolve(int value){
        return Arrays.stream(PandaResourceDeviceType.values()).filter(v->v.getValue()==value).findFirst().orElse(RESERVEED);
    }
}
