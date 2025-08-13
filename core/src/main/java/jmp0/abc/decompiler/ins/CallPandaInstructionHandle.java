package jmp0.abc.decompiler.ins;

import jmp0.abc.decompiler.PandaDecompilerMethodHandler;
import jmp0.abc.disasm.block.PandaIRBasicBlock;
import jmp0.abc.disasm.ins.call.CallPandaInstruction;
import jmp0.abc.disasm.lexical.PandaLexical;
import jmp0.abc.disasm.param.IPandaInstructionParam;
import jmp0.abc.decompiler.codegen.CodeGenerator;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class CallPandaInstructionHandle implements IInstructionHandle<CallPandaInstruction>{
    @Override
    public HandleStatus handle(CallPandaInstruction instruction, PandaIRBasicBlock block, PandaDecompilerMethodHandler methodHandler, PandaLexical pandaLexical, Object node) {
        CodeGenerator codeGenerator = methodHandler.getCodeGenerator();
        Object arrayObject = codeGenerator.arrayObject();
        IPandaInstructionParam thisObjc = instruction.getThisObj();
        if (thisObjc != null) codeGenerator.addToArray(arrayObject,codeGenerator.getNodeFactory().identifierNode(thisObjc.toString()));
        if (instruction.isApply()){
            codeGenerator.addToArray(arrayObject,codeGenerator.getNodeFactory().spreadElementNode(codeGenerator.getNodeFactory().identifierNode(instruction.getCallParams()[0].toString())));
        }else {
            for (IPandaInstructionParam param : instruction.getCallParams()) {
                codeGenerator.addToArray(arrayObject,codeGenerator.getNodeFactory().identifierNode(param.toString()));
            }
        }
        Object calleeNode;
        if (thisObjc == null)
            calleeNode = codeGenerator.getNodeFactory().identifierNode("acc");
        else
            calleeNode = codeGenerator.getNodeFactory().memberExpressionNode(codeGenerator.getNodeFactory().identifierNode("acc"),codeGenerator.getNodeFactory().identifierNode("call"),false);
        Object callExpressionNode = codeGenerator.getNodeFactory().callExpressionNode(calleeNode,arrayObject);
        codeGenerator.getNodeUtils().setReturnTypeCall(callExpressionNode);
        Object assignmentExpressionNode = codeGenerator.getNodeFactory().assignmentExpressionNode("=",codeGenerator.getNodeFactory().identifierNode("acc"),callExpressionNode);
        Object expressionStatementNode = codeGenerator.getNodeFactory().expressionStatementNode(assignmentExpressionNode);
        codeGenerator.getNodeUtils().insertNodeToBody(expressionStatementNode,node);
        return HandleStatus.createNormalStatus();
    }
}
