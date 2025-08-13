package jmp0.abc.decompiler.ins;

import jmp0.abc.decompiler.PandaDecompilerMethodHandler;
import jmp0.abc.disasm.block.PandaIRBasicBlock;
import jmp0.abc.disasm.ins.visitors.ldspr.LoadSuperPandaInstruction;
import jmp0.abc.decompiler.codegen.CodeGenerator;
import jmp0.abc.disasm.lexical.PandaLexical;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class LoadSuperPandaInstructionHandle implements IInstructionHandle<LoadSuperPandaInstruction>{
    @Override
    public HandleStatus handle(LoadSuperPandaInstruction instruction, PandaIRBasicBlock block, PandaDecompilerMethodHandler methodHandler, PandaLexical pandaLexical, Object node) {
        CodeGenerator codeGenerator = methodHandler.getCodeGenerator();
        Object memberExpressionNode = codeGenerator.getNodeFactory().memberExpressionNode(codeGenerator.getNodeFactory().superNode(),codeGenerator.getNodeFactory().identifierNode(instruction.getFuncName()));
        Object assignmentExpressionNode = codeGenerator.getNodeFactory().assignmentExpressionNode("=",codeGenerator.getNodeFactory().identifierNode("acc"),memberExpressionNode);
        Object expressionStatementNode = codeGenerator.getNodeFactory().expressionStatementNode(assignmentExpressionNode);
        codeGenerator.getNodeUtils().insertNodeToBody(expressionStatementNode, node);
        return HandleStatus.createNormalStatus();
    }
}
