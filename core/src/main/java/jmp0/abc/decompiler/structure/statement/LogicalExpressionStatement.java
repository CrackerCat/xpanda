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
public abstract class LogicalExpressionStatement extends JumpStatement {

    public enum TYPE{
        OR("||"),
        AND("&&");
        private final String op;
        TYPE(String op){
            this.op = op;
        }

    }
    protected final boolean firstExp;
    protected final boolean secondExp;
    protected final IPandaDecompileAble first;
    protected final IPandaDecompileAble second;
    public LogicalExpressionStatement(Region first,Region second) {
        super(first);
        this.first = first.getStatements().get(0);
        this.firstExp = first.isConditionalExp();
        this.second = second.getStatements().get(0);
        this.secondExp = second.isConditionalExp();
    }

    @Override
    protected Object generateTestExpression(PandaDecompilerMethodHandler methodHandler,PandaLexical pandaLexical) {
        Object sequenceFirst = methodHandler.getCodeGenerator().getNodeFactory().sequenceExpressionNode();
        first.decompile(methodHandler,pandaLexical , sequenceFirst);
        if (getType() == TYPE.OR)
            methodHandler.getCodeGenerator().getNodeUtils().insertToSequenceExpression(generateTestExpression(methodHandler, !firstExp),sequenceFirst);
        else
            methodHandler.getCodeGenerator().getNodeUtils().insertToSequenceExpression(generateTestExpression(methodHandler, firstExp),sequenceFirst);
        Object sequenceSecond = methodHandler.getCodeGenerator().getNodeFactory().sequenceExpressionNode();
        second.decompile(methodHandler,pandaLexical , sequenceSecond);
        methodHandler.getCodeGenerator().getNodeUtils().insertToSequenceExpression(generateTestExpression(methodHandler,secondExp),sequenceSecond);
        return methodHandler.getCodeGenerator().getNodeFactory().logicalExpressionNode(getType().op,sequenceFirst,sequenceSecond);
    }

    @Override
    public void decompile(PandaDecompilerMethodHandler methodHandler, PandaLexical pandaLexical, Object astNode) {
        //if LogicalExpressionStatement contain LogicalExpressionStatement ,the method will be called!
        CodeGenerator codeGenerator = methodHandler.getCodeGenerator();
        if (codeGenerator.getNodeUtils().isSequenceExpression(astNode)){
            Object node = generateTestExpression(methodHandler,pandaLexical);
            methodHandler.getCodeGenerator().getNodeUtils().insertToSequenceExpression(node,astNode);
        }
    }

    public abstract LogicalExpressionStatement.TYPE getType();

}
