package jmp0.abc.decompiler.ins;

import jmp0.abc.decompiler.PandaDecompilerMethodHandler;
import jmp0.abc.disasm.block.PandaIRBasicBlock;
import jmp0.abc.disasm.ins.visitors.copyobj.CopyDataPropertiesPandaInstruction;
import jmp0.abc.decompiler.codegen.CodeGenerator;
import jmp0.abc.disasm.lexical.PandaLexical;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class CopyDataPropertiesPandaInstructionHandle implements IInstructionHandle<CopyDataPropertiesPandaInstruction>{
    @Override
    public HandleStatus handle(CopyDataPropertiesPandaInstruction instruction, PandaIRBasicBlock block, PandaDecompilerMethodHandler methodHandler, PandaLexical pandaLexical, Object node) {
        CodeGenerator codeGenerator = methodHandler.getCodeGenerator();
        Object memberExpressionNode = codeGenerator.getNodeFactory().memberExpressionNode(codeGenerator.getNodeFactory().identifierNode("Object"),codeGenerator.getNodeFactory().identifierNode("assign"));
        Object arrayObject = codeGenerator.arrayObject();
        codeGenerator.addToArray(arrayObject,codeGenerator.getNodeFactory().objectExpressionNode(codeGenerator.arrayObject()));
        codeGenerator.addToArray(arrayObject,codeGenerator.getNodeFactory().identifierNode("acc"));
        Object callExpressionNode = codeGenerator.getNodeFactory().callExpressionNode(memberExpressionNode,arrayObject);
        Object assignmentExpressionNode = codeGenerator.getNodeFactory().assignmentExpressionNode("=",codeGenerator.getNodeFactory().identifierNode(instruction.getObj().toString()),callExpressionNode);
        Object expressionStatementNode = codeGenerator.getNodeFactory().expressionStatementNode(assignmentExpressionNode);
        codeGenerator.getNodeUtils().insertNodeToBody(expressionStatementNode,node);
        return HandleStatus.createNormalStatus();

    }
}
