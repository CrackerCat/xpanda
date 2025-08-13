package jmp0.abc.decompiler.structure.statement;

import jmp0.abc.decompiler.PandaDecompilerMethodHandler;
import jmp0.abc.decompiler.codegen.CodeGenerator;
import jmp0.abc.decompiler.structure.Region;
import jmp0.abc.disasm.lexical.PandaLexical;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class LabelStatement implements PandaStatement{
    private final Region region;
    public LabelStatement(Region region){
        this.region = region;
    }
    @Override
    public void decompile(PandaDecompilerMethodHandler methodHandler, PandaLexical pandaLexical, Object astNode) {
        CodeGenerator codeGenerator = methodHandler.getCodeGenerator();
        Object label = codeGenerator.getNodeFactory().labeledStatementNode(region.getName(),null);
        codeGenerator.getNodeUtils().insertNodeToBody(label, astNode);
    }
}
