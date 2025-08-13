package jmp0.abc.decompiler.structure.statement;

import jmp0.abc.decompiler.PandaDecompilerMethodHandler;
import jmp0.abc.decompiler.codegen.CodeGenerator;
import jmp0.abc.decompiler.structure.IPandaDecompileAble;
import jmp0.abc.decompiler.structure.Region;
import jmp0.abc.disasm.lexical.PandaLexical;
import jmp0.abc.util.PandaLogger;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class IfPandaStatement extends JumpStatement {
    private final PandaLogger logger = new PandaLogger(IfPandaStatement.class);
    private IPandaDecompileAble consequent;
    private IPandaDecompileAble alternate;
    private TryCatchPandaStatement tryCatchPandaStatement = null;
    public IfPandaStatement(Region test, IPandaDecompileAble consequent, IPandaDecompileAble alternate){
        super(test);
        if (test.isTryCatchHelperRegion()){
            logger.logD("reduceTryCatchRegion",String.format("reduce try catch %s %s %s",test,consequent,alternate));
            tryCatchPandaStatement = new TryCatchPandaStatement(consequent,alternate);
            return;
        }
        this.consequent = consequent;
        this.alternate = alternate;
    }

    @Override
    public void decompile(PandaDecompilerMethodHandler methodHandler, PandaLexical pandaLexical, Object astNode) {
        // this is try-catch region
        if (tryCatchPandaStatement != null){
            tryCatchPandaStatement.decompile(methodHandler,pandaLexical , astNode);
            return;
        }
        CodeGenerator codeGenerator = methodHandler.getCodeGenerator();
        Object exp = generateTestExpression(methodHandler,pandaLexical);
        if (consequent == null){
            exp = codeGenerator.getNodeFactory().unaryExpressionNode("!",exp);
            Object alertBlock = codeGenerator.getNodeFactory().blockStatementNode();
            alternate.decompile(methodHandler,pandaLexical , alertBlock);
            Object object1 = codeGenerator.getNodeFactory().ifStatementNode(exp,alertBlock,null);
            codeGenerator.getNodeUtils().insertNodeToBody(object1, astNode);
        } else if (alternate == null){
            Object consequentBlock = codeGenerator.getNodeFactory().blockStatementNode();
            consequent.decompile(methodHandler,pandaLexical , consequentBlock);
            Object object1 = codeGenerator.getNodeFactory().ifStatementNode(exp,consequentBlock,null);
            codeGenerator.getNodeUtils().insertNodeToBody(object1, astNode);
        }else {
            Object consequentBlock = codeGenerator.getNodeFactory().blockStatementNode();
            consequent.decompile(methodHandler,pandaLexical , consequentBlock);
            Object alertBlock = codeGenerator.getNodeFactory().blockStatementNode();
            alternate.decompile(methodHandler,pandaLexical , alertBlock);
            Object object1 = codeGenerator.getNodeFactory().ifStatementNode(exp,consequentBlock,alertBlock);
            codeGenerator.getNodeUtils().insertNodeToBody(object1, astNode);
        }
    }
}
