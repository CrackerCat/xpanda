package jmp0.abc.decompiler.codegen;

import lombok.SneakyThrows;

import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public abstract class JSBridgePandaExport {
    private final JSBridge bridge;
    protected Object instance;
    @SneakyThrows
    public JSBridgePandaExport(JSBridge bridge){
        InputStream inputStream = this.getClass().getClassLoader().getResource("dist.js").openStream();
        this.bridge = bridge;
        if(bridge.eval("typeof pandaExport").equals("undefined")){
            bridge.eval(new InputStreamReader(inputStream));
        }
    }

    protected Object invokeInstanceMethod(String name, Object ...arg){
        return bridge.invokeMethod(instance,name,arg);
    }
}
