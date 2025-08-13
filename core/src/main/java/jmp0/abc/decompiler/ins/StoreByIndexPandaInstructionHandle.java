package jmp0.abc.decompiler.ins;

import jmp0.abc.decompiler.PandaDecompilerMethodHandler;
import jmp0.abc.disasm.block.PandaIRBasicBlock;
import jmp0.abc.disasm.ins.visitors.array.StoreByIndexPandaInstruction;
import jmp0.abc.decompiler.codegen.CodeGenerator;
import jmp0.abc.disasm.lexical.PandaLexical;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class StoreByIndexPandaInstructionHandle implements IInstructionHandle<StoreByIndexPandaInstruction>{
    @Override
    public HandleStatus handle(StoreByIndexPandaInstruction instruction, PandaIRBasicBlock block, PandaDecompilerMethodHandler methodHandler, PandaLexical pandaLexical, Object node) {
        CodeGenerator codeGenerator = methodHandler.getCodeGenerator();
        Object memberExpressionNode = codeGenerator.getNodeFactory().memberExpressionNode(codeGenerator.getNodeFactory().identifierNode(instruction.getArr().toString()),codeGenerator.getNodeFactory().numberLiteralNode(instruction.getIndex().intValue()),true);
        Object assignmentExpressionNode = codeGenerator.getNodeFactory().assignmentExpressionNode("=",memberExpressionNode,codeGenerator.getNodeFactory().identifierNode("acc"));
        Object expressionStatementNode = codeGenerator.getNodeFactory().expressionStatementNode(assignmentExpressionNode);
        codeGenerator.getNodeUtils().insertNodeToBody(expressionStatementNode,node);
        return HandleStatus.createNormalStatus();
    }
}
