package jmp0.abc.decompiler.ins;

import jmp0.abc.decompiler.PandaDecompilerMethodHandler;
import jmp0.abc.decompiler.codegen.CodeGenerator;
import jmp0.abc.disasm.block.PandaIRBasicBlock;
import jmp0.abc.disasm.ins.visitors.stobj.spr.StoreSuperObjectPandaInstruction;
import jmp0.abc.disasm.lexical.PandaLexical;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class StoreSuperObjectPandaInstructionHandle implements IInstructionHandle<StoreSuperObjectPandaInstruction>{
    @Override
    public HandleStatus handle(StoreSuperObjectPandaInstruction instruction, PandaIRBasicBlock block, PandaDecompilerMethodHandler methodHandler, PandaLexical pandaLexical, Object node) {
        CodeGenerator codeGenerator = methodHandler.getCodeGenerator();
        Object memberExpressionNode = codeGenerator.getNodeFactory().memberExpressionNode(codeGenerator.getNodeFactory().superNode(),codeGenerator.getNodeFactory().identifierNode(instruction.getObj().toString()),true);
        memberExpressionNode = codeGenerator.getNodeFactory().memberExpressionNode(memberExpressionNode,codeGenerator.getNodeFactory().identifierNode(instruction.getProp().getContent()),true);
        Object assignmentExpressionNode = codeGenerator.getNodeFactory().assignmentExpressionNode("=",memberExpressionNode,codeGenerator.getNodeFactory().identifierNode("acc"));
        Object expressionStatementNode = codeGenerator.getNodeFactory().expressionStatementNode(assignmentExpressionNode);
        codeGenerator.getNodeUtils().insertNodeToBody(expressionStatementNode,node);
        return HandleStatus.createNormalStatus();
    }
}
