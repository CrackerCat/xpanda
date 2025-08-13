package jmp0.abc;

import lombok.Getter;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
@Getter
public final class Pair<T,P> {
    private final T first;
    private final P second;
    public Pair(T first,P second){
        this.first = first;
        this.second = second;
    }
}
