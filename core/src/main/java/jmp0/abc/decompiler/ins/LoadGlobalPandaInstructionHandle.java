package jmp0.abc.decompiler.ins;

import jmp0.abc.decompiler.PandaDecompilerMethodHandler;
import jmp0.abc.disasm.block.PandaIRBasicBlock;
import jmp0.abc.disasm.ins.visitors.global.LoadGlobalPandaInstruction;
import jmp0.abc.decompiler.codegen.CodeGenerator;
import jmp0.abc.disasm.lexical.PandaLexical;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class LoadGlobalPandaInstructionHandle implements IInstructionHandle<LoadGlobalPandaInstruction>{
    @Override
    public HandleStatus handle(LoadGlobalPandaInstruction instruction, PandaIRBasicBlock block, PandaDecompilerMethodHandler methodHandler, PandaLexical pandaLexical, Object node) {
        CodeGenerator codeGenerator = methodHandler.getCodeGenerator();
        Object assignmentExpressionNode = codeGenerator.getNodeFactory().assignmentExpressionNode("=",codeGenerator.getNodeFactory().identifierNode("acc"),codeGenerator.getNodeFactory().identifierNode(instruction.getName().getContent()));
        Object expressionStatementNode = codeGenerator.getNodeFactory().expressionStatementNode(assignmentExpressionNode);
        codeGenerator.getNodeUtils().insertNodeToBody(expressionStatementNode,node);
        return HandleStatus.createNormalStatus();
    }
}
