package jmp0.abc.decompiler.ins;

import jmp0.abc.decompiler.PandaDecompilerMethodHandler;
import jmp0.abc.decompiler.codegen.CodeGenerator;
import jmp0.abc.disasm.block.PandaIRBasicBlock;
import jmp0.abc.disasm.ins.iterator.GetNextPandaInstruction;
import jmp0.abc.disasm.lexical.PandaLexical;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class GetNextPandaInstructionHandle implements IInstructionHandle<GetNextPandaInstruction>{
    @Override
    public HandleStatus handle(GetNextPandaInstruction instruction, PandaIRBasicBlock block, PandaDecompilerMethodHandler methodHandler, PandaLexical pandaLexical, Object node) {
        CodeGenerator codeGenerator = methodHandler.getCodeGenerator();
        Object memberNode = codeGenerator.getNodeFactory().memberExpressionNode(codeGenerator.getNodeFactory().identifierNode(instruction.getIterator().toString()),
                codeGenerator.getNodeFactory().identifierNode("next"),false);
        Object callNode = codeGenerator.getNodeFactory().callExpressionNode(memberNode,codeGenerator.arrayObject());
        memberNode = codeGenerator.getNodeFactory().memberExpressionNode(callNode,
                codeGenerator.getNodeFactory().identifierNode("value"),false);
        Object assignmentExpressionNode = codeGenerator.getNodeFactory().assignmentExpressionNode("=",codeGenerator.getNodeFactory().identifierNode("acc"),memberNode);
        Object expressionStatementNode = codeGenerator.getNodeFactory().expressionStatementNode(assignmentExpressionNode);
        codeGenerator.getNodeUtils().insertNodeToBody(expressionStatementNode,node);
        return HandleStatus.createNormalStatus();
    }
}
