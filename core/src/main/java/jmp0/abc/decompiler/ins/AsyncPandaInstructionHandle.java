package jmp0.abc.decompiler.ins;

import jmp0.abc.decompiler.PandaDecompilerMethodHandler;
import jmp0.abc.decompiler.codegen.CodeGenerator;
import jmp0.abc.disasm.block.PandaIRBasicBlock;
import jmp0.abc.disasm.ins.async.ASYNCFUNCTIONAWAITUNCAUGHT_V8PandaInstruction;
import jmp0.abc.disasm.ins.async.ASYNCFUNCTIONENTER_NONEPandaInstruction;
import jmp0.abc.disasm.ins.async.AsyncPandaInstruction;
import jmp0.abc.disasm.lexical.PandaLexical;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class AsyncPandaInstructionHandle implements IInstructionHandle<AsyncPandaInstruction>{
    @Override
    public HandleStatus handle(AsyncPandaInstruction instruction, PandaIRBasicBlock block, PandaDecompilerMethodHandler methodHandler, PandaLexical pandaLexical, Object node) {
        CodeGenerator codeGenerator = methodHandler.getCodeGenerator();
        if (instruction instanceof ASYNCFUNCTIONENTER_NONEPandaInstruction){
            codeGenerator.getNodeUtils().setIsAsyncBlock(node);
            return HandleStatus.createStopStatus();
        } else if (instruction instanceof ASYNCFUNCTIONAWAITUNCAUGHT_V8PandaInstruction) {
            Object awaitNode = codeGenerator.getNodeFactory().awaitExpressionNode(codeGenerator.getNodeFactory().identifierNode("acc"));
            Object assignmentExpressionNode = codeGenerator.getNodeFactory().assignmentExpressionNode("=",codeGenerator.getNodeFactory().identifierNode(instruction.getParams()[0].toString()),awaitNode);
            Object expressionStatementNode = codeGenerator.getNodeFactory().expressionStatementNode(assignmentExpressionNode);
            codeGenerator.getNodeUtils().insertNodeToBody(expressionStatementNode,node);
            return HandleStatus.createNormalStatus();
        }
        return HandleStatus.createNormalStatus();
    }
}
