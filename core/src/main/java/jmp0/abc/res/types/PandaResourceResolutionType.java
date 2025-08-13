package jmp0.abc.res.types;

import lombok.Getter;

import java.util.Arrays;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
@Getter
public enum PandaResourceResolutionType {
    SDPI(120,"sdpi"),
    MDPI(160,"mdpi"),
    LDPI(240,"ldpi"),
    XLDPI(320,"xldpi"),
    XXLDPI(480,"xxldpi"),
    XXXLDPI(640,"xxxldpi");
    private final int value;
    private final String name;
    PandaResourceResolutionType(int value,String name){
        this.value = value;
        this.name = name;
    }

    public static PandaResourceResolutionType resolve(int value){
        return Arrays.stream(PandaResourceResolutionType.values()).filter(v->v.getValue()==value).findFirst().orElse(null);
    }
}
