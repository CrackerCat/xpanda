package jmp0.abc.decompiler.codegen;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class JSNodeFactory extends JSBridgePandaExport{
    public JSNodeFactory(JSBridge bridge) {
        super(bridge);
        this.instance = bridge.eval("pandaExport.NodeFactory");
    }

    public Object programNode(Object ...arg){
        return invokeInstanceMethod("programNode",arg);
    }

    public Object thisExpressionNode(){
        return invokeInstanceMethod("thisExpressionNode");
    }

    public Object arrayExpressionNode(Object ...arg){
        return invokeInstanceMethod("arrayExpressionNode",arg);
    }

    public Object assignmentExpressionNode(Object ...arg){
        return invokeInstanceMethod("assignmentExpressionNode",arg);
    }

    public Object binaryExpressionNode(Object ...arg){
        return invokeInstanceMethod("binaryExpressionNode",arg);
    }

    public Object blockStatementNode(Object ...arg){
        return invokeInstanceMethod("blockStatementNode",arg);
    }

    public Object breakStatement(Object ...arg){
        return invokeInstanceMethod("breakStatement",arg);
    }

    public Object callExpressionNode(Object ...arg){
        return invokeInstanceMethod("callExpressionNode",arg);
    }

    public Object conditionalExpressionNode(Object ...arg){
        return invokeInstanceMethod("conditionalExpressionNode",arg);
    }

    public Object continueStatement(Object ...arg){
        return invokeInstanceMethod("continueStatement",arg);
    }

    public Object directiveNode(Object ...arg){
        return invokeInstanceMethod("directiveNode",arg);
    }

    public Object doWhileStatementNode(Object ...arg){
        return invokeInstanceMethod("doWhileStatementNode",arg);
    }

    public Object expressionStatementNode(Object expression){
        return invokeInstanceMethod("expressionStatementNode",expression);
    }

    public Object forStatementNode(Object ...arg){
        return invokeInstanceMethod("forStatementNode",arg);
    }

    public Object forInStatementNode(Object ...arg){
        return invokeInstanceMethod("forInStatementNode",arg);
    }

    public Object forOfStatementNode(Object ...arg){
        return invokeInstanceMethod("forOfStatementNode",arg);
    }

    public Object functionDeclarationNode(Object ...arg){
        return invokeInstanceMethod("functionDeclarationNode",arg);
    }

    public Object functionExpressionNode(Object id,Object params,Object block){
        return invokeInstanceMethod("functionExpressionNode",id,params,block);
    }

    public Object identifierNode(String name){
        return invokeInstanceMethod("identifierNode",name);
    }

    public Object identifierNode(String name,Object typeAnnotation){
        return invokeInstanceMethod("identifierNode",name,typeAnnotation);
    }

    public Object labeledStatementNode(Object ...arg){
        return invokeInstanceMethod("labeledStatementNode",arg);
    }
    public Object nullLiteralNode(){
    return invokeInstanceMethod("nullLiteralNode");
}
    public Object stringLiteralNode(String value){
        return invokeInstanceMethod("stringLiteralNode",value);
    }
    public Object numberLiteralNode(Number value){
        return invokeInstanceMethod("numberLiteralNode",value);
    }

    public Object booleanLiteralNode(boolean flag){
        return invokeInstanceMethod("booleanLiteralNode",flag);
    }

    public Object logicalExpressionNode(Object ...arg){
        return invokeInstanceMethod("logicalExpressionNode",arg);
    }

    public Object memberExpressionNode(Object ...arg){
        return invokeInstanceMethod("memberExpressionNode",arg);
    }

    public Object objectExpressionNode(Object props){
        return invokeInstanceMethod("objectExpressionNode",props);
    }

    public Object objectPropertyNode(Object ...arg){
        return invokeInstanceMethod("objectPropertyNode",arg);
    }

    public Object restElementNode(Object ...arg){
        return invokeInstanceMethod("restElementNode",arg);
    }

    public Object returnStatementNode(Object ...arg){
        return invokeInstanceMethod("returnStatementNode",arg);
    }

    public Object sequenceExpressionNode(Object ...arg){
        return invokeInstanceMethod("sequenceExpressionNode",arg);
    }

    public Object spreadElementNode(Object ...arg){
        return invokeInstanceMethod("spreadElementNode",arg);
    }

    public Object staticBlockNode(Object ...arg){
        return invokeInstanceMethod("staticBlockNode",arg);
    }

    public Object switchStatementNode(Object ...arg){
        return invokeInstanceMethod("switchStatementNode",arg);
    }

    public Object switchCaseNode(Object ...arg){
        return invokeInstanceMethod("switchCaseNode",arg);
    }

    public Object unaryExpressionNode(Object ...arg){
        return invokeInstanceMethod("unaryExpressionNode",arg);
    }

    @SuppressWarnings("unused")
    public Object updateExpressionNode(Object ...arg){
        return invokeInstanceMethod("updateExpressionNode",arg);
    }

    public Object variableDeclarationNode(Object ...arg){
        return invokeInstanceMethod("variableDeclarationNode",arg);
    }

    public Object variableDeclaratorNode(Object ...arg){
        return invokeInstanceMethod("variableDeclaratorNode",arg);
    }

    public Object whileStatementNode(Object ...arg){
        return invokeInstanceMethod("whileStatementNode",arg);
    }

    public Object classMethodNode(Object key,Object value,Object kind,boolean computed,boolean isStatic){
        return invokeInstanceMethod("classMethodNode",key,value,kind,computed,isStatic);
    }

    public Object classBodyNode(Object body){
        return invokeInstanceMethod("classBodyNode",body);
    }

    public Object classPropertyNode(Object key,Object value){
        return invokeInstanceMethod("classPropertyNode" ,key, value);
    }

    public Object classExpressionNode(Object id,Object superClass,Object body){
        return invokeInstanceMethod("classExpressionNode",id,superClass,body);
    }

    public Object superNode(){
        return invokeInstanceMethod("superNode");
    }

    public Object newExpressionNode(Object callee,Object params){return invokeInstanceMethod("newExpressionNode",callee,params);}
    public Object tryStatementNode(Object tryBody,Object catchBody){return invokeInstanceMethod("tryStatementNode",tryBody,catchBody);}

    public Object throwStatementNode(Object exp){return invokeInstanceMethod("throwStatementNode",exp);}

    public void addComment(Object node,String value){
        invokeInstanceMethod("addComment",node,value);
    }
    public void addTrailingComment(Object node,String value){
        invokeInstanceMethod("addTrailingComment",node,value);
    }
    public void addLeadingComment(Object node,String value){
        invokeInstanceMethod("addLeadingComment",node,value);
    }

    public Object ifStatementNode(Object test,Object consequent,Object alternate){return invokeInstanceMethod("ifStatementNode",test,consequent,alternate);}

    public Object awaitExpressionNode(Object exp){
        return invokeInstanceMethod("awaitExpressionNode",exp);
    }
    public Object dynamicImportExpressionNode(){
        return invokeInstanceMethod("dynamicImportExpressionNode");
    }

    public Object yieldExpressionNode(Object exp){return invokeInstanceMethod("yieldExpressionNode",exp);}

    public Object importSpecifierNode(String local,String imported){return invokeInstanceMethod("importSpecifierNode",local,imported);}
    public Object importDefaultSpecifierNode(String local){return invokeInstanceMethod("importDefaultSpecifierNode",local);}
    public Object importNamespaceSpecifierNode(String local){return invokeInstanceMethod("importNamespaceSpecifierNode",local);}
    public Object importDeclarationNode(Object exp,String source){return invokeInstanceMethod("importDeclarationNode",exp,source);}

    public Object exportDefaultSpecifierNode(String exported){
        return invokeInstanceMethod("exportDefaultSpecifierNode",exported);
    }
    public Object exportSpecifierNode(String local,String exported){
        return invokeInstanceMethod("exportSpecifierNode",local,exported);
    }
    public Object exportDefaultDeclarationNode(String name){
        return invokeInstanceMethod("exportDefaultDeclarationNode",name);
    }
    public Object exportAllDeclarationNode(String path){
        return invokeInstanceMethod("exportAllDeclarationNode",path);
    }

    public Object exportNamedDeclarationNode(Object exp,String path){
        return invokeInstanceMethod("exportNamedDeclarationNode",exp,path);
    }
    public Object parse(String content){return invokeInstanceMethod("parse",content);}

    public String generate(Object ...arg){
        return (String) invokeInstanceMethod("generate",arg);
    }


}
