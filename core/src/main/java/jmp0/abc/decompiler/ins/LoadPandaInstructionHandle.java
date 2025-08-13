package jmp0.abc.decompiler.ins;

import jmp0.abc.decompiler.PandaDecompilerMethodHandler;
import jmp0.abc.disasm.block.PandaIRBasicBlock;
import jmp0.abc.disasm.ins.load.LoadPandaInstruction;
import jmp0.abc.disasm.lexical.PandaLexical;
import jmp0.abc.disasm.param.PandaInstructionACC;
import jmp0.abc.decompiler.codegen.CodeGenerator;
import jmp0.abc.disasm.param.PandaInstructionIMM;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class LoadPandaInstructionHandle implements IInstructionHandle<LoadPandaInstruction>{
    @Override
    public HandleStatus handle(LoadPandaInstruction instruction, PandaIRBasicBlock block, PandaDecompilerMethodHandler methodHandler, PandaLexical pandaLexical, Object node) {
        CodeGenerator codeGenerator = methodHandler.getCodeGenerator();
        Object loadNode;
        if (instruction.isSendAble()){
            loadNode = codeGenerator.getNodeFactory().identifierNode("panda_jmp0_reserved_sendable_class");
        } else if (instruction.getType() == PandaInstructionACC.TYPE.STRING){
            loadNode = codeGenerator.getNodeFactory().stringLiteralNode(instruction.selfToString());
        }else if (instruction.getType() == PandaInstructionACC.TYPE.FALSE){
            loadNode = codeGenerator.getNodeFactory().booleanLiteralNode(false);
        }else if (instruction.getType() == PandaInstructionACC.TYPE.TRUE){
            loadNode = codeGenerator.getNodeFactory().booleanLiteralNode(true);
        }else if (instruction.getType() == PandaInstructionACC.TYPE.NULL){
            loadNode = codeGenerator.getNodeFactory().nullLiteralNode();
        }else if (instruction.getParam() instanceof PandaInstructionIMM){
            loadNode = codeGenerator.getNodeFactory().numberLiteralNode(((PandaInstructionIMM) instruction.getParam()).getImm());
        }else loadNode = codeGenerator.getNodeFactory().identifierNode(instruction.selfToString());

        Object assignmentExpressionNode = codeGenerator.getNodeFactory().assignmentExpressionNode("=",codeGenerator.getNodeFactory().identifierNode("acc"),loadNode);
        Object expressionStatementNode = codeGenerator.getNodeFactory().expressionStatementNode(assignmentExpressionNode);
        codeGenerator.getNodeUtils().insertNodeToBody(expressionStatementNode,node);
        return HandleStatus.createNormalStatus();
    }
}
