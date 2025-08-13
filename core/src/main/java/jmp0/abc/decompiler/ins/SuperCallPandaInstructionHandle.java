package jmp0.abc.decompiler.ins;

import jmp0.abc.decompiler.PandaDecompilerMethodHandler;
import jmp0.abc.disasm.block.PandaIRBasicBlock;
import jmp0.abc.disasm.ins.call.spr.SuperCallPandaInstruction;
import jmp0.abc.disasm.lexical.PandaLexical;
import jmp0.abc.disasm.param.PandaInstructionVReg;
import jmp0.abc.decompiler.codegen.CodeGenerator;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class SuperCallPandaInstructionHandle implements IInstructionHandle<SuperCallPandaInstruction>{
    @Override
    public HandleStatus handle(SuperCallPandaInstruction instruction, PandaIRBasicBlock block, PandaDecompilerMethodHandler methodHandler, PandaLexical pandaLexical, Object node) {
        CodeGenerator codeGenerator = methodHandler.getCodeGenerator();
        PandaInstructionVReg[] vRegs = instruction.getCallParams();
        Object arrayObject = codeGenerator.arrayObject();
        for (PandaInstructionVReg vReg : vRegs) {
            codeGenerator.addToArray(arrayObject,codeGenerator.getNodeFactory().identifierNode(vReg.toString()));
        }
        Object callExpressionNode = codeGenerator.getNodeFactory().callExpressionNode(codeGenerator.getNodeFactory().superNode(),arrayObject);
        Object assignmentExpressionNode = codeGenerator.getNodeFactory().assignmentExpressionNode("=",codeGenerator.getNodeFactory().identifierNode("acc"),callExpressionNode);
        Object expressionStatementNode = codeGenerator.getNodeFactory().expressionStatementNode(assignmentExpressionNode);
        codeGenerator.getNodeUtils().insertNodeToBody(expressionStatementNode,node);
        return HandleStatus.createNormalStatus();
    }
}
