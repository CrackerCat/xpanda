package jmp0.abc.decompiler.ins;

import jmp0.abc.decompiler.PandaDecompilerMethodHandler;
import jmp0.abc.disasm.block.PandaIRBasicBlock;
import jmp0.abc.disasm.ins.mov.MovPandaInstruction;
import jmp0.abc.disasm.lexical.PandaLexical;
import jmp0.abc.disasm.param.PandaInstructionVReg;
import jmp0.abc.decompiler.codegen.CodeGenerator;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class MovPandaInstructionHandle implements IInstructionHandle<MovPandaInstruction>{
    @Override
    public HandleStatus handle(MovPandaInstruction instruction, PandaIRBasicBlock block, PandaDecompilerMethodHandler methodHandler, PandaLexical pandaLexical, Object node) {
        CodeGenerator codeGenerator = methodHandler.getCodeGenerator();
        PandaInstructionVReg srcObj = instruction.getSrcObj();
        PandaInstructionVReg destObj = instruction.getDestObj();
        Object assignmentExpressionNode = codeGenerator.getNodeFactory().assignmentExpressionNode("=",codeGenerator.getNodeFactory().identifierNode(destObj.toString()),codeGenerator.getNodeFactory().identifierNode(srcObj.toString()));
        Object expressionStatementNode = codeGenerator.getNodeFactory().expressionStatementNode(assignmentExpressionNode);
        codeGenerator.getNodeUtils().insertNodeToBody(expressionStatementNode,node);
        return HandleStatus.createNormalStatus();
    }
}
