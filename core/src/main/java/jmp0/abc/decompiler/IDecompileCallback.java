package jmp0.abc.decompiler;

import jmp0.abc.file.clazz.PandaClass;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public interface IDecompileCallback {
    enum STATUS{
        SUCCESS,
        NO_MAIN,
        SIMPLIFY_FAILED,
        DECOMPILE_FAILED
    }
    void onDecompileComplete(PandaClass pandaClass,STATUS status,String content);
}
