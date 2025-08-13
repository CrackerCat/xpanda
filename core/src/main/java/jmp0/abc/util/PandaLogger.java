package jmp0.abc.util;

import lombok.Setter;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class PandaLogger {
    @Setter
    private static boolean enable = true;
    private final String name;
    public PandaLogger(Class<?> clazz){
        this.name = clazz.getName();
    }

    public void logD(String tag,String content){
        if (enable)
            System.out.printf("DEBUG [%s] [%s] %s%n",this.name,tag,content);
    }

    public void logW(String tag,String content){
        if (enable)
            System.out.printf("WARN [%s] [%s] %s%n",this.name,tag,content);
    }
}
