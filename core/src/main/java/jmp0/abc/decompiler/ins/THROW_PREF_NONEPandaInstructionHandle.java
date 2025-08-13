package jmp0.abc.decompiler.ins;

import jmp0.abc.decompiler.PandaDecompilerMethodHandler;
import jmp0.abc.disasm.block.PandaIRBasicBlock;
import jmp0.abc.disasm.ins.trw.THROW_PREF_NONEPandaInstruction;
import jmp0.abc.decompiler.codegen.CodeGenerator;
import jmp0.abc.disasm.lexical.PandaLexical;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class THROW_PREF_NONEPandaInstructionHandle implements IInstructionHandle<THROW_PREF_NONEPandaInstruction>{
    @Override
    public HandleStatus handle(THROW_PREF_NONEPandaInstruction instruction, PandaIRBasicBlock block, PandaDecompilerMethodHandler methodHandler, PandaLexical pandaLexical, Object node) {
        CodeGenerator codeGenerator = methodHandler.getCodeGenerator();
        Object throwStatementNode = codeGenerator.getNodeFactory().throwStatementNode(codeGenerator.getNodeFactory().identifierNode("acc"));
        codeGenerator.getNodeUtils().insertNodeToBody(throwStatementNode,node);
        return HandleStatus.createNormalStatus();
    }
}
