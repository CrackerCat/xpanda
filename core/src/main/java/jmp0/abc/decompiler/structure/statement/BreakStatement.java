package jmp0.abc.decompiler.structure.statement;

import jmp0.abc.decompiler.PandaDecompilerMethodHandler;
import jmp0.abc.disasm.lexical.PandaLexical;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class BreakStatement implements PandaStatement{
    private final String comment;
    public BreakStatement(String comment){
        this.comment = comment;
    }
    @Override
    public void decompile(PandaDecompilerMethodHandler methodHandler, PandaLexical pandaLexical, Object astNode) {
        Object breakStatement = methodHandler.getCodeGenerator().getNodeFactory().breakStatement();
        if (comment != null)
            methodHandler.getCodeGenerator().getNodeFactory().addTrailingComment(breakStatement,comment);
        methodHandler.getCodeGenerator().getNodeUtils().insertNodeToBody(breakStatement, astNode);
    }
}
