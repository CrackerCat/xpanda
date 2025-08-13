package jmp0.abc.decompiler.ins;

import jmp0.abc.decompiler.PandaDecompilerMethodHandler;
import jmp0.abc.disasm.block.PandaIRBasicBlock;
import jmp0.abc.disasm.ins.visitors.stmod.StoreModuleVarPandaInstruction;
import jmp0.abc.decompiler.codegen.CodeGenerator;
import jmp0.abc.disasm.lexical.PandaLexical;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class StoreModuleVarPandaInstructionHandle implements IInstructionHandle<StoreModuleVarPandaInstruction>{
    @Override
    public HandleStatus handle(StoreModuleVarPandaInstruction instruction, PandaIRBasicBlock block, PandaDecompilerMethodHandler methodHandler, PandaLexical pandaLexical, Object node) {
        CodeGenerator codeGenerator = methodHandler.getCodeGenerator();
        String localName = instruction.getModule().getLocalName();
        Object ass = codeGenerator.getNodeFactory().assignmentExpressionNode("=",codeGenerator.getNodeFactory().identifierNode(localName),codeGenerator.getNodeFactory().identifierNode("acc"));
        codeGenerator.getNodeUtils().insertNodeToBody(codeGenerator.getNodeFactory().expressionStatementNode(ass),node);
        return HandleStatus.createNormalStatus();
    }
}
