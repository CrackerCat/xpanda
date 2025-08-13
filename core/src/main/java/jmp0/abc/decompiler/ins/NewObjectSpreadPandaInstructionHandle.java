package jmp0.abc.decompiler.ins;

import jmp0.abc.decompiler.PandaDecompilerMethodHandler;
import jmp0.abc.decompiler.codegen.CodeGenerator;
import jmp0.abc.disasm.block.PandaIRBasicBlock;
import jmp0.abc.disasm.ins.creaters.spread.NewObjectSpreadPandaInstruction;
import jmp0.abc.disasm.lexical.PandaLexical;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class NewObjectSpreadPandaInstructionHandle implements IInstructionHandle<NewObjectSpreadPandaInstruction>{
    @Override
    public HandleStatus handle(NewObjectSpreadPandaInstruction instruction, PandaIRBasicBlock block, PandaDecompilerMethodHandler methodHandler, PandaLexical pandaLexical, Object node) {
        CodeGenerator codeGenerator = methodHandler.getCodeGenerator();
        Object spreadElementNode = codeGenerator.getNodeFactory().spreadElementNode(codeGenerator.getNodeFactory().identifierNode("acc"));
        Object arrayObject = codeGenerator.arrayObject();
        codeGenerator.addToArray(arrayObject,spreadElementNode);
        Object newExpressionNode = codeGenerator.getNodeFactory().newExpressionNode(codeGenerator.getNodeFactory().identifierNode(instruction.getCotr().toString()),arrayObject);
        Object assignmentExpressionNode = codeGenerator.getNodeFactory().assignmentExpressionNode("=",codeGenerator.getNodeFactory().identifierNode("acc"),newExpressionNode);
        Object expressionStatementNode = codeGenerator.getNodeFactory().expressionStatementNode(assignmentExpressionNode);
        codeGenerator.getNodeUtils().insertNodeToBody(expressionStatementNode,node);
        return HandleStatus.createNormalStatus();
    }
}
