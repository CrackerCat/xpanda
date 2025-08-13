package jmp0.abc.decompiler.codegen;

import lombok.SneakyThrows;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.Reader;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class JSBridge {
    private final ScriptEngine scriptEngine;
    @SneakyThrows
    public JSBridge(){
//        System.setProperty("polyglot.engine.WarnInterpreterOnly","false");
        scriptEngine = new ScriptEngineManager().getEngineByName("graal.js");
    }

    @SneakyThrows
    public Object eval(Reader reader){
        return scriptEngine.eval(reader);
    }

    @SneakyThrows
    public Object eval(String content){
        return scriptEngine.eval(content);
    }

    @SneakyThrows
    public Object invokeFunction(String name, Object ...arg){
        return ((Invocable)scriptEngine).invokeFunction(name,arg);
    }

    @SneakyThrows
    public Object invokeMethod(Object thiz, String name, Object ...arg){
        return ((Invocable)scriptEngine).invokeMethod(thiz,name,arg);
    }
}
