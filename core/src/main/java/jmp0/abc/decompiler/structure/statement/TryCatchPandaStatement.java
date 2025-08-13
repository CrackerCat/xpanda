package jmp0.abc.decompiler.structure.statement;

import jmp0.abc.decompiler.PandaDecompilerMethodHandler;
import jmp0.abc.decompiler.codegen.CodeGenerator;
import jmp0.abc.decompiler.structure.IPandaDecompileAble;
import jmp0.abc.disasm.lexical.PandaLexical;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class TryCatchPandaStatement implements PandaStatement{
    private final IPandaDecompileAble tryBody;
    private final IPandaDecompileAble catchBody;
    public TryCatchPandaStatement(IPandaDecompileAble tryBody, IPandaDecompileAble catchBody){
        this.tryBody = tryBody;
        this.catchBody = catchBody;
    }
    @Override
    public void decompile(PandaDecompilerMethodHandler methodHandler, PandaLexical pandaLexical, Object astNode) {
        CodeGenerator codeGenerator = methodHandler.getCodeGenerator();
        Object tryBodyNode = codeGenerator.getNodeFactory().blockStatementNode();
        tryBody.decompile(methodHandler,pandaLexical , tryBodyNode);
        Object catchBodyNode = codeGenerator.getNodeFactory().blockStatementNode();
        catchBody.decompile(methodHandler,pandaLexical , catchBodyNode);
        Object tryNode = codeGenerator.getNodeFactory().tryStatementNode(tryBodyNode,catchBodyNode);
        codeGenerator.getNodeUtils().insertNodeToBody(tryNode, astNode);
    }
}
