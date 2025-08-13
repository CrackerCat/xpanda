package jmp0.abc.decompiler.ins;

import jmp0.abc.decompiler.PandaDecompilerMethodHandler;
import jmp0.abc.disasm.block.PandaIRBasicBlock;
import jmp0.abc.disasm.ins.visitors.copyrestargs.CopyRestArgsPandaInstruction;
import jmp0.abc.decompiler.codegen.CodeGenerator;
import jmp0.abc.disasm.lexical.PandaLexical;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class CopyRestArgsPandaInstructionHandle implements IInstructionHandle<CopyRestArgsPandaInstruction>{
    @Override
    public HandleStatus handle(CopyRestArgsPandaInstruction instruction, PandaIRBasicBlock block, PandaDecompilerMethodHandler methodHandler, PandaLexical pandaLexical, Object node) {
        CodeGenerator codeGenerator = methodHandler.getCodeGenerator();
        Object memberExpressionNode = codeGenerator.getNodeFactory().memberExpressionNode(codeGenerator.getNodeFactory().identifierNode("arguments"),codeGenerator.getNodeFactory().numberLiteralNode(instruction.getIndex()),true);
        Object spreadElementNode = codeGenerator.getNodeFactory().spreadElementNode(memberExpressionNode);
        Object arrayObject = codeGenerator.arrayObject();
        codeGenerator.addToArray(arrayObject,spreadElementNode);
        Object arrayExpressionNode = codeGenerator.getNodeFactory().arrayExpressionNode(arrayObject);
        Object assignmentExpressionNode = codeGenerator.getNodeFactory().assignmentExpressionNode("=",codeGenerator.getNodeFactory().identifierNode("acc"),arrayExpressionNode);
        Object expressionStatementNode = codeGenerator.getNodeFactory().expressionStatementNode(assignmentExpressionNode);
        codeGenerator.getNodeUtils().insertNodeToBody(expressionStatementNode,node);
        return HandleStatus.createNormalStatus();
    }
}
