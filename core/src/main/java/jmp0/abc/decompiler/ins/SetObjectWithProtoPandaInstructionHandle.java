package jmp0.abc.decompiler.ins;

import jmp0.abc.decompiler.PandaDecompilerMethodHandler;
import jmp0.abc.decompiler.codegen.CodeGenerator;
import jmp0.abc.disasm.block.PandaIRBasicBlock;
import jmp0.abc.disasm.ins.visitors.setobj.SetObjectWithProtoPandaInstruction;
import jmp0.abc.disasm.lexical.PandaLexical;
import jmp0.abc.disasm.param.PandaInstructionVReg;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class SetObjectWithProtoPandaInstructionHandle implements IInstructionHandle<SetObjectWithProtoPandaInstruction>{
    @Override
    public HandleStatus handle(SetObjectWithProtoPandaInstruction instruction, PandaIRBasicBlock block, PandaDecompilerMethodHandler methodHandler, PandaLexical pandaLexical, Object node) {
        PandaInstructionVReg proto = instruction.getProto();
        CodeGenerator codeGenerator = methodHandler.getCodeGenerator();
        Object memberExpressionNode = codeGenerator.getNodeFactory().memberExpressionNode(codeGenerator.getNodeFactory().identifierNode("Object"),codeGenerator.getNodeFactory().identifierNode("setPrototypeOf"));
        Object arrayObject = codeGenerator.arrayObject();
        codeGenerator.addToArray(arrayObject,codeGenerator.getNodeFactory().identifierNode("acc"));
        codeGenerator.addToArray(arrayObject,codeGenerator.getNodeFactory().identifierNode(proto.toString()));
        Object callExpressionNode = codeGenerator.getNodeFactory().callExpressionNode(memberExpressionNode,arrayObject);
        Object expressionStatementNode = codeGenerator.getNodeFactory().expressionStatementNode(callExpressionNode);
        codeGenerator.getNodeUtils().insertNodeToBody(expressionStatementNode,node);
        return HandleStatus.createNormalStatus();
    }
}
