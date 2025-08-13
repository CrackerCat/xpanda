package jmp0.abc.decompiler.codegen;

public final class JSNodeGenHelper extends JSBridgePandaExport{
    public JSNodeGenHelper(JSBridge bridge) {
        super(bridge);
        this.instance = bridge.eval("pandaExport.NodeGenHelper");
    }
}
