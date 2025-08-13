package jmp0.abc.decompiler.structure.statement;

import jmp0.abc.decompiler.PandaDecompilerMethodHandler;
import jmp0.abc.decompiler.codegen.CodeGenerator;
import jmp0.abc.decompiler.structure.IPandaDecompileAble;
import jmp0.abc.decompiler.structure.Region;
import jmp0.abc.disasm.lexical.PandaLexical;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class WhileStatement extends JumpStatement{
    private final IPandaDecompileAble[] headStms;
    private final IPandaDecompileAble[] bodyStms;
    public WhileStatement(Region region,Region body){
        super(region);
        this.bodyStms = body.getStatements().toArray(new IPandaDecompileAble[]{});
        if (region == null){
            headStms = null;
            return;
        }
        this.headStms = region.getStatements().toArray(new IPandaDecompileAble[]{});
    }
    @Override
    public void decompile(PandaDecompilerMethodHandler methodHandler, PandaLexical pandaLexical, Object astNode) {
        CodeGenerator codeGenerator = methodHandler.getCodeGenerator();
        Object block = codeGenerator.getNodeFactory().blockStatementNode();
        if (headStms != null){
            for (IPandaDecompileAble headStm : headStms) {
                headStm.decompile(methodHandler,pandaLexical , block);
            }
            Object testNode = generateTestExpression(methodHandler,pandaLexical);
            //revert logical expression
            if (isLogicalExpressionStatement()){
                testNode = codeGenerator.getNodeFactory().unaryExpressionNode("!",testNode);
            }
            Object jumpOutBlock = codeGenerator.getNodeFactory().ifStatementNode(testNode,codeGenerator.getNodeFactory().breakStatement(),null);
            codeGenerator.getNodeUtils().insertNodeToBody(jumpOutBlock,block);
        }
        for (IPandaDecompileAble bodyStm : bodyStms) {
            bodyStm.decompile(methodHandler,pandaLexical , block);
        }
        Object trueNode = codeGenerator.getNodeFactory().booleanLiteralNode(true);
        Object doWhileNode = codeGenerator.getNodeFactory().whileStatementNode(trueNode,block);
        codeGenerator.getNodeUtils().insertNodeToBody(doWhileNode, astNode);
    }
}
