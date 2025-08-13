package jmp0.abc;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */

public class PandaParseException extends Exception{
    public PandaParseException(String message){
        super(message);
    }
    public PandaParseException(Exception exception){
        super(exception);
    }
}
