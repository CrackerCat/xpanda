package jmp0.abc.decompiler.ins;

import jmp0.abc.decompiler.PandaDecompilerMethodHandler;
import jmp0.abc.disasm.block.PandaIRBasicBlock;
import jmp0.abc.disasm.ins.creaters.NewObjectRangePandaInstruction;
import jmp0.abc.disasm.lexical.PandaLexical;
import jmp0.abc.disasm.param.PandaInstructionVReg;
import jmp0.abc.decompiler.codegen.CodeGenerator;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class NewObjectRangePandaInstructionHandle implements IInstructionHandle<NewObjectRangePandaInstruction>{
    @Override
    public HandleStatus handle(NewObjectRangePandaInstruction instruction, PandaIRBasicBlock block, PandaDecompilerMethodHandler methodHandler, PandaLexical pandaLexical, Object node) {
        CodeGenerator codeGenerator = methodHandler.getCodeGenerator();
        Object objNode = codeGenerator.getNodeFactory().identifierNode(instruction.getObjectVReg().toString());
        Object params = codeGenerator.arrayObject();
        for (PandaInstructionVReg pandaInstructionVReg : instruction.getParamsVReg()) {
            codeGenerator.addToArray(params,codeGenerator.getNodeFactory().identifierNode(pandaInstructionVReg.toString()));
        }
        Object newObj = codeGenerator.getNodeFactory().newExpressionNode(objNode,params);
        Object assignmentExpressionNode = codeGenerator.getNodeFactory().assignmentExpressionNode("=",codeGenerator.getNodeFactory().identifierNode("acc"),newObj);
        Object expressionStatementNode = codeGenerator.getNodeFactory().expressionStatementNode(assignmentExpressionNode);
        codeGenerator.getNodeUtils().insertNodeToBody(expressionStatementNode,node);
        return HandleStatus.createNormalStatus();
    }
}
