package jmp0.abc.decompiler.ins;

import jmp0.abc.decompiler.PandaDecompilerMethodHandler;
import jmp0.abc.decompiler.codegen.CodeGenerator;
import jmp0.abc.disasm.block.PandaIRBasicBlock;
import jmp0.abc.disasm.ins.callruntime.CallRuntimePandaInstruction;
import jmp0.abc.disasm.lexical.PandaLexical;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class CallRuntimePandaInstructionHandle implements IInstructionHandle<CallRuntimePandaInstruction>{
    @Override
    public HandleStatus handle(CallRuntimePandaInstruction instruction, PandaIRBasicBlock block, PandaDecompilerMethodHandler methodHandler, PandaLexical pandaLexical, Object node) {
        CodeGenerator codeGenerator = methodHandler.getCodeGenerator();
        if (instruction.getType() == CallRuntimePandaInstruction.TYPE.CALL_INIT){
            Object memberExpressionNode = codeGenerator.getNodeFactory().memberExpressionNode(codeGenerator.getNodeFactory().identifierNode("acc"),
                    codeGenerator.getNodeFactory().identifierNode("call"),false);
            Object arrayObject = codeGenerator.arrayObject();
            codeGenerator.addToArray(arrayObject,codeGenerator.getNodeFactory().identifierNode("panda_jmp0_reserved_param_p2"));
            Object callExpressionNode = codeGenerator.getNodeFactory().callExpressionNode(memberExpressionNode,arrayObject);
            Object expressionStatementNode = codeGenerator.getNodeFactory().expressionStatementNode(callExpressionNode);
            codeGenerator.getNodeUtils().insertNodeToBody(expressionStatementNode,node);
        } else if (instruction.getType() == CallRuntimePandaInstruction.TYPE.IS_FALSE) {
            Object unaryExpressionNode = codeGenerator.getNodeFactory().unaryExpressionNode("!",codeGenerator.getNodeFactory().identifierNode("acc"));
            Object expressionNode = codeGenerator.getNodeFactory().assignmentExpressionNode("=",codeGenerator.getNodeFactory().identifierNode("acc"),unaryExpressionNode);
            codeGenerator.getNodeUtils().insertNodeToBody(codeGenerator.getNodeFactory().expressionStatementNode(expressionNode),node);
        }
        //just ignore call.istrue
        return HandleStatus.createNormalStatus();
    }
}
