package jmp0.abc.decompiler.structure.statement;

import jmp0.abc.decompiler.PandaDecompilerMethodHandler;
import jmp0.abc.decompiler.structure.Region;
import jmp0.abc.disasm.lexical.PandaLexical;
import lombok.SneakyThrows;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public abstract class JumpStatement implements PandaStatement{
    private final boolean exp;
    private LogicalExpressionStatement logicalExpressionStatement = null;
    public JumpStatement(Region conditionalRegion){
        if (conditionalRegion.isLogicalRegion()){
            if (conditionalRegion.getStatements().getLast() instanceof LogicalExpressionStatement)
                logicalExpressionStatement = (LogicalExpressionStatement) conditionalRegion.getStatements().getLast();
            else
                logicalExpressionStatement = (LogicalExpressionStatement) conditionalRegion.getStatements().getFirst();
        }
        this.exp = conditionalRegion.isConditionalExp();
    }

    protected boolean isLogicalExpressionStatement(){
        return logicalExpressionStatement != null;
    }

    @SneakyThrows
    protected Object generateTestExpression(PandaDecompilerMethodHandler methodHandler, PandaLexical pandaLexical){
        if (logicalExpressionStatement != null){
            return logicalExpressionStatement.generateTestExpression(methodHandler,pandaLexical);
        }
        return generateTestExpression(methodHandler,exp);
    }

    protected Object generateTestExpression(PandaDecompilerMethodHandler methodHandler, boolean exp){
        if (!exp) return methodHandler.getCodeGenerator().getNodeFactory().unaryExpressionNode("!",methodHandler.getCodeGenerator().getNodeFactory().identifierNode("acc"));
        else return methodHandler.getCodeGenerator().getNodeFactory().identifierNode("acc");
    }
}
