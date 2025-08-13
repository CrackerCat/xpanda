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
public final class DoWhileStatement extends JumpStatement {
    private final IPandaDecompileAble[] bodyStms;
    public DoWhileStatement(Region body){
        super(body);
        this.bodyStms = body.getStatements().toArray(new IPandaDecompileAble[]{});
    }
    @Override
    public void decompile(PandaDecompilerMethodHandler methodHandler, PandaLexical pandaLexical, Object astNode) {
        CodeGenerator codeGenerator = methodHandler.getCodeGenerator();
        Object block = codeGenerator.getNodeFactory().blockStatementNode();
        for (IPandaDecompileAble bodyStm : bodyStms) {
            bodyStm.decompile(methodHandler,pandaLexical , block);
        }
        Object testNode = codeGenerator.getNodeFactory().unaryExpressionNode("!",generateTestExpression(methodHandler,pandaLexical));
        Object doWhileNode = codeGenerator.getNodeFactory().doWhileStatementNode(testNode,block);
        codeGenerator.getNodeUtils().insertNodeToBody(doWhileNode, astNode);
    }
}
