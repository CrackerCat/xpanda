package jmp0.abc.decompiler.ins;

import jmp0.abc.decompiler.PandaDecompilerMethodHandler;
import jmp0.abc.disasm.block.PandaIRBasicBlock;
import jmp0.abc.disasm.ins.visitors.array.StoreArraySpreadPandaInstruction;
import jmp0.abc.decompiler.codegen.CodeGenerator;
import jmp0.abc.disasm.lexical.PandaLexical;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class StoreArraySpreadPandaInstructionHandle implements IInstructionHandle<StoreArraySpreadPandaInstruction>{
    @Override
    public HandleStatus handle(StoreArraySpreadPandaInstruction instruction, PandaIRBasicBlock block, PandaDecompilerMethodHandler methodHandler, PandaLexical pandaLexical, Object node) {
        CodeGenerator codeGenerator = methodHandler.getCodeGenerator();
        // acc = dest.splice(index,0,...acc)
        Object memberExpressionNode = codeGenerator.getNodeFactory().memberExpressionNode(codeGenerator.getNodeFactory().identifierNode(instruction.getDest().toString()),codeGenerator.getNodeFactory().identifierNode("splice"));
        Object arrayObject = codeGenerator.arrayObject();
        codeGenerator.addToArray(arrayObject,codeGenerator.getNodeFactory().identifierNode(instruction.getIndex().toString()));
        codeGenerator.addToArray(arrayObject,codeGenerator.getNodeFactory().numberLiteralNode(0));
        codeGenerator.addToArray(arrayObject,codeGenerator.getNodeFactory().spreadElementNode(codeGenerator.getNodeFactory().identifierNode("acc")));
        Object callExpressionNode = codeGenerator.getNodeFactory().callExpressionNode(memberExpressionNode,arrayObject);
        Object assignmentExpressionNode = codeGenerator.getNodeFactory().assignmentExpressionNode("=",codeGenerator.getNodeFactory().identifierNode("acc"),callExpressionNode);
        codeGenerator.getNodeUtils().insertNodeToBody(codeGenerator.getNodeFactory().expressionStatementNode(assignmentExpressionNode),node);
        return HandleStatus.createNormalStatus();
    }
}
