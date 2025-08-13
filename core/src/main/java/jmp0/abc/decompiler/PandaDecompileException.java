package jmp0.abc.decompiler;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public class PandaDecompileException extends Exception{
    public PandaDecompileException(String message){
        super(message);
    }
    public PandaDecompileException(Exception exception){
        super(exception);
    }
}
