package jmp0.abc.decompiler.ins;

import jmp0.abc.decompiler.PandaDecompilerMethodHandler;
import jmp0.abc.disasm.block.PandaIRBasicBlock;
import jmp0.abc.disasm.ins.visitors.ldobj.LoadObjectByPandaInstruction;
import jmp0.abc.disasm.lexical.PandaLexical;
import jmp0.abc.disasm.param.PandaInstructionID;
import jmp0.abc.disasm.param.PandaInstructionVReg;
import jmp0.abc.decompiler.codegen.CodeGenerator;
import jmp0.abc.file.desc.PandaString;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class LoadObjectByPandaInstructionHandle implements IInstructionHandle<LoadObjectByPandaInstruction>{
    @Override
    public HandleStatus handle(LoadObjectByPandaInstruction instruction, PandaIRBasicBlock block, PandaDecompilerMethodHandler methodHandler, PandaLexical pandaLexical, Object node) {
        CodeGenerator codeGenerator = methodHandler.getCodeGenerator();
        Object memberExpressionNode;
        if (instruction.isDynamicImport()) memberExpressionNode = codeGenerator.getNodeFactory().dynamicImportExpressionNode();
        else if (instruction.isGlobal()){
            if (instruction.getObj() instanceof PandaInstructionID && ((PandaInstructionID) instruction.getObj()).getType() == PandaInstructionID.TYPE.STRING){
                memberExpressionNode = codeGenerator.getNodeFactory().memberExpressionNode(codeGenerator.getNodeFactory().identifierNode("globalThis"),codeGenerator.getNodeFactory().stringLiteralNode(((PandaString) ((PandaInstructionID) instruction.getObj()).getObj()).getContent()),true);
            }else memberExpressionNode = codeGenerator.getNodeFactory().memberExpressionNode(codeGenerator.getNodeFactory().identifierNode("globalThis"),codeGenerator.getNodeFactory().identifierNode(instruction.getObj().toString()),true);
        } else if (instruction.getObj() instanceof PandaInstructionVReg) memberExpressionNode = codeGenerator.getNodeFactory().memberExpressionNode(codeGenerator.getNodeFactory().identifierNode(instruction.getObj().toString()),codeGenerator.getNodeFactory().identifierNode("acc"),true);
        else if (instruction.getObj() instanceof PandaInstructionID && ((PandaInstructionID) instruction.getObj()).getType() == PandaInstructionID.TYPE.STRING) memberExpressionNode = codeGenerator.getNodeFactory().memberExpressionNode(codeGenerator.getNodeFactory().identifierNode("acc"),codeGenerator.getNodeFactory().stringLiteralNode(((PandaString) ((PandaInstructionID) instruction.getObj()).getObj()).getContent()),true);
        else memberExpressionNode = codeGenerator.getNodeFactory().memberExpressionNode(codeGenerator.getNodeFactory().identifierNode("acc"),codeGenerator.getNodeFactory().identifierNode(instruction.getObj().toString()),true);
        Object assignmentExpressionNode = codeGenerator.getNodeFactory().assignmentExpressionNode("=",codeGenerator.getNodeFactory().identifierNode("acc"),memberExpressionNode);
        Object expressionStatementNode = codeGenerator.getNodeFactory().expressionStatementNode(assignmentExpressionNode);
        codeGenerator.getNodeUtils().insertNodeToBody(expressionStatementNode,node);
        return HandleStatus.createNormalStatus();
    }
}
