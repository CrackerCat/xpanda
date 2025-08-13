package jmp0.abc.decompiler.ins;

import jmp0.abc.decompiler.PandaDecompilerMethodHandler;
import jmp0.abc.disasm.block.PandaIRBasicBlock;
import jmp0.abc.disasm.ins.visitors.stobj.StoreObjectPandaInstruction;
import jmp0.abc.decompiler.codegen.CodeGenerator;
import jmp0.abc.disasm.lexical.PandaLexical;
import jmp0.abc.disasm.param.PandaInstructionID;
import jmp0.abc.disasm.param.PandaInstructionIMM;
import jmp0.abc.file.desc.PandaString;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class StoreObjectPandaInstructionHandle implements IInstructionHandle<StoreObjectPandaInstruction>{
    @Override
    public HandleStatus handle(StoreObjectPandaInstruction instruction, PandaIRBasicBlock block, PandaDecompilerMethodHandler methodHandler, PandaLexical pandaLexical, Object node) {
        CodeGenerator codeGenerator = methodHandler.getCodeGenerator();
        Object memberExpressionNode = null;
        if (instruction.getKey() instanceof PandaInstructionID && ((PandaInstructionID) instruction.getKey()).getType() == PandaInstructionID.TYPE.STRING)
            memberExpressionNode = codeGenerator.getNodeFactory().memberExpressionNode(codeGenerator.getNodeFactory().identifierNode(instruction.getObj().toString()),codeGenerator.getNodeFactory().stringLiteralNode(
                    ((PandaString)(((PandaInstructionID) instruction.getKey()).getObj())).getContent()),true);
        else if (instruction.getKey() instanceof PandaInstructionIMM)
            memberExpressionNode = codeGenerator.getNodeFactory().memberExpressionNode(codeGenerator.getNodeFactory().identifierNode(instruction.getObj().toString()),codeGenerator.getNodeFactory().numberLiteralNode(
                    ((PandaInstructionIMM) instruction.getKey()).getImm()),true);
        else memberExpressionNode = codeGenerator.getNodeFactory().memberExpressionNode(codeGenerator.getNodeFactory().identifierNode(instruction.getObj().toString()),codeGenerator.getNodeFactory().identifierNode(instruction.getKey().toString()),true);
        Object assignmentExpressionNode = codeGenerator.getNodeFactory().assignmentExpressionNode("=",memberExpressionNode,codeGenerator.getNodeFactory().identifierNode("acc"));
        Object expressionStatementNode = codeGenerator.getNodeFactory().expressionStatementNode(assignmentExpressionNode);
        codeGenerator.getNodeUtils().insertNodeToBody(expressionStatementNode,node);
        return HandleStatus.createNormalStatus();
    }
}
