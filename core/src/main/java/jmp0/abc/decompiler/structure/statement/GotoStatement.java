package jmp0.abc.decompiler.structure.statement;

import jmp0.abc.decompiler.PandaDecompilerMethodHandler;
import jmp0.abc.decompiler.codegen.CodeGenerator;
import jmp0.abc.decompiler.structure.Region;
import jmp0.abc.disasm.lexical.PandaLexical;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class GotoStatement implements PandaStatement{
    private final Region dest;
    public GotoStatement(Region dest){
        this.dest = dest;
    }
    @Override
    public void decompile(PandaDecompilerMethodHandler methodHandler, PandaLexical pandaLexical, Object astNode) {
        String name = dest.getName();
        CodeGenerator codeGenerator  = methodHandler.getCodeGenerator();
        if (!codeGenerator.getNodeUtils().tryFixGoto(astNode,name)){
            Object identifierNode = codeGenerator.getNodeFactory().identifierNode("xpanda_goto");
            Object arrayObject = codeGenerator.arrayObject();
            codeGenerator.addToArray(arrayObject,codeGenerator.getNodeFactory().identifierNode(name));
            Object callExpressionNode = codeGenerator.getNodeFactory().callExpressionNode(identifierNode,arrayObject);
            Object expressionStatementNode = codeGenerator.getNodeFactory().expressionStatementNode(callExpressionNode);
            codeGenerator.getNodeUtils().insertNodeToBody(expressionStatementNode, astNode);
        }
    }
}
