package jmp0.abc.decompiler.ins;

import jmp0.abc.decompiler.PandaDecompilerMethodHandler;
import jmp0.abc.disasm.block.PandaIRBasicBlock;
import jmp0.abc.disasm.ins.visitors.delobj.DeleteObjectPandaInstruction;
import jmp0.abc.decompiler.codegen.CodeGenerator;
import jmp0.abc.disasm.lexical.PandaLexical;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class DeleteObjectPandaInstructionHandle implements IInstructionHandle<DeleteObjectPandaInstruction>{
    @Override
    public HandleStatus handle(DeleteObjectPandaInstruction instruction, PandaIRBasicBlock block, PandaDecompilerMethodHandler methodHandler, PandaLexical pandaLexical, Object node) {
        CodeGenerator codeGenerator = methodHandler.getCodeGenerator();
        Object memberExpressionNode = codeGenerator.getNodeFactory().memberExpressionNode(codeGenerator.getNodeFactory().identifierNode(instruction.getObj().toString()),codeGenerator.getNodeFactory().identifierNode("acc"),true);
        Object unaryObject = codeGenerator.getNodeFactory().unaryExpressionNode("delete", memberExpressionNode);
        Object expressionStatementNode = codeGenerator.getNodeFactory().expressionStatementNode(unaryObject);
        codeGenerator.getNodeUtils().insertNodeToBody(expressionStatementNode, node);
        return HandleStatus.createNormalStatus();
    }
}
