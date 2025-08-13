package jmp0.abc.decompiler.codegen;

import lombok.Getter;
import lombok.SneakyThrows;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */

@Getter
public final class CodeGenerator {
    private final JSNodeUtils nodeUtils;
    private final JSNodeFactory nodeFactory;
    private final JSNodeGenHelper nodeGenHelper;
    private final JSBridge jsBridge;
    @SneakyThrows
    public CodeGenerator(JSBridge bridge){
        this.jsBridge = bridge;
        this.nodeFactory = new JSNodeFactory(bridge);
        this.nodeUtils = new JSNodeUtils(bridge);
        this.nodeGenHelper = new JSNodeGenHelper(bridge);
    }

    public Object arrayObject(){
        return jsBridge.eval("[]");
    }

    public void addToArray(Object arr,Object obj){
        this.jsBridge.invokeMethod(arr,"push",obj);
    }
}
