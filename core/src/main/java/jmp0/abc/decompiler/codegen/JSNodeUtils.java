package jmp0.abc.decompiler.codegen;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public class JSNodeUtils extends JSBridgePandaExport{
    public JSNodeUtils(JSBridge bridge) {
        super(bridge);
        this.instance = bridge.eval("pandaExport.NodeUtils");
    }

    public void insertToProgram(Object programA,Object programB){
        invokeInstanceMethod("insertToProgram",programA,programB);
    }

    public void insertNodeToBody(Object statement, Object program){
        invokeInstanceMethod("insertNodeToBody",statement,program);
    }

    public void insertToSequenceExpression(Object exp,Object sequenceNode){
        invokeInstanceMethod("insertToSequenceExpression",exp,sequenceNode);
    }

    public void decompileFailedBlock(Object block){
        invokeInstanceMethod("decompileFailedBlock",block);
    }

    public void setIsAsyncBlock(Object block){
        invokeInstanceMethod("setIsAsyncBlock",block);
    }

    public void setIsGeneratorBlock(Object block){
        invokeInstanceMethod("setIsGeneratorBlock",block);
    }

    public void copyBlockExtra(Object block,Object block1){
        invokeInstanceMethod("copyBlockExtra",block,block1);
    }

    public void compare(Object node){
        invokeInstanceMethod("compare",node);
    }

    public String toJson(Object node){return (String) invokeInstanceMethod("toJson",node);}

    public Object toObject(Object node){return invokeInstanceMethod("toObject",node);}

    public boolean isSequenceExpression(Object node){return (Boolean) invokeInstanceMethod("isSequenceExpression",node);}

    public void covert2SingleBlock(Object node){invokeInstanceMethod("covert2SingleBlock",node);}

    public boolean tryFixGoto(Object node,String name ){return (Boolean) invokeInstanceMethod("tryFixGoto",node,name);}

    public void setReturnTypeCall(Object node){
        invokeInstanceMethod("setReturnTypeCall",node);
    }

}
