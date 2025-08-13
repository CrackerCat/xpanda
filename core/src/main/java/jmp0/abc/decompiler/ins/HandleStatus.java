package jmp0.abc.decompiler.ins;

import lombok.Getter;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
@Getter
public final class HandleStatus {
    public enum STATUS{
        NORMAL,
        STOP
    }
    private final STATUS status;
    public HandleStatus(HandleStatus.STATUS status){
        this.status = status;
    }

    public static HandleStatus createNormalStatus(){
        return new HandleStatus(STATUS.NORMAL);
    }
    public static HandleStatus createStopStatus(){
        return new HandleStatus(STATUS.STOP);
    }
}
