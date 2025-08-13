package jmp0.abc.decompiler.ins;

import jmp0.abc.decompiler.PandaDecompilerMethodHandler;
import jmp0.abc.disasm.block.PandaIRBasicBlock;
import jmp0.abc.disasm.ins.call.spr.SuperCallSpreadPandaInstruction;
import jmp0.abc.decompiler.codegen.CodeGenerator;
import jmp0.abc.disasm.lexical.PandaLexical;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class SuperCallSpreadPandaInstructionHandle implements IInstructionHandle<SuperCallSpreadPandaInstruction>{
    @Override
    public HandleStatus handle(SuperCallSpreadPandaInstruction instruction, PandaIRBasicBlock block, PandaDecompilerMethodHandler methodHandler, PandaLexical pandaLexical, Object node) {
        CodeGenerator codeGenerator = methodHandler.getCodeGenerator();
        Object spreadElementNode = codeGenerator.getNodeFactory().spreadElementNode(codeGenerator.getNodeFactory().identifierNode(instruction.getArr().toString()));
        Object arrayObject = codeGenerator.arrayObject();
        codeGenerator.addToArray(arrayObject,spreadElementNode);
        Object callExpressionNode = codeGenerator.getNodeFactory().callExpressionNode(codeGenerator.getNodeFactory().superNode(),arrayObject);

        Object assignmentExpressionNode = codeGenerator.getNodeFactory().assignmentExpressionNode("=",codeGenerator.getNodeFactory().identifierNode("acc"),callExpressionNode);
        Object expressionStatementNode = codeGenerator.getNodeFactory().expressionStatementNode(assignmentExpressionNode);
        codeGenerator.getNodeUtils().insertNodeToBody(expressionStatementNode,node);
        return HandleStatus.createNormalStatus();
    }
}
