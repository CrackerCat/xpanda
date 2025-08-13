package jmp0.abc.decompiler.ins;

import jmp0.abc.decompiler.PandaDecompilerMethodHandler;
import jmp0.abc.disasm.block.PandaIRBasicBlock;
import jmp0.abc.disasm.ins.visitors.stlex.StoreLexPandaInstruction;
import jmp0.abc.decompiler.codegen.CodeGenerator;
import jmp0.abc.disasm.lexical.PandaLexical;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class StoreLexPandaInstructionHandle implements IInstructionHandle<StoreLexPandaInstruction>{
    @Override
    public HandleStatus handle(StoreLexPandaInstruction instruction, PandaIRBasicBlock block, PandaDecompilerMethodHandler methodHandler, PandaLexical pandaLexical, Object node) {
        CodeGenerator codeGenerator = methodHandler.getCodeGenerator();
        String name = instruction.isSendAble() ? "sendable":"local";
        int nowLevel = pandaLexical.getLevel(block.getParent(),instruction.getPC()) - instruction.getLevel().intValue() -1;
        name += "_" + nowLevel + "_";
        Object assignmentExpressionNode = codeGenerator.getNodeFactory().assignmentExpressionNode("=",codeGenerator.getNodeFactory().identifierNode(name + instruction.getIndex()),codeGenerator.getNodeFactory().identifierNode("acc"));
        Object expressionStatementNode = codeGenerator.getNodeFactory().expressionStatementNode(assignmentExpressionNode);
        codeGenerator.getNodeUtils().insertNodeToBody(expressionStatementNode,node);
        return HandleStatus.createNormalStatus();
    }
}
