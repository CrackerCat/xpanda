package jmp0.abc.decompiler.ins;

import jmp0.abc.decompiler.PandaDecompilerMethodHandler;
import jmp0.abc.disasm.block.PandaIRBasicBlock;
import jmp0.abc.disasm.ins.definition.DEFINEGETTERSETTERBYVALUE_V8_V8_V8_V8PandaInstruction;
import jmp0.abc.decompiler.codegen.CodeGenerator;
import jmp0.abc.disasm.lexical.PandaLexical;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class DEFINEGETTERSETTERBYVALUE_V8_V8_V8_V8PandaInstructionHandle implements IInstructionHandle<DEFINEGETTERSETTERBYVALUE_V8_V8_V8_V8PandaInstruction>{
    @Override
    public HandleStatus handle(DEFINEGETTERSETTERBYVALUE_V8_V8_V8_V8PandaInstruction instruction, PandaIRBasicBlock block, PandaDecompilerMethodHandler methodHandler, PandaLexical pandaLexical, Object node) {
        CodeGenerator codeGenerator = methodHandler.getCodeGenerator();
        String objName = instruction.getObj().toString();
        Object objIdentifier = codeGenerator.getNodeFactory().identifierNode(objName);
        String propName = instruction.getProp().toString();
        Object propIdentifier = codeGenerator.getNodeFactory().identifierNode(propName);
        String getName = instruction.getGetter().toString();
        Object getIdentifier = codeGenerator.getNodeFactory().identifierNode(getName);
        String setName = instruction.getSetter().toString();
        Object setIdentifier = codeGenerator.getNodeFactory().identifierNode(setName);
        Object getterMember = codeGenerator.getNodeFactory().memberExpressionNode(objIdentifier,codeGenerator.getNodeFactory().identifierNode("__defineGetter__"));
        Object setterMember = codeGenerator.getNodeFactory().memberExpressionNode(objIdentifier,codeGenerator.getNodeFactory().identifierNode("__defineSetter__"));

        Object getArrObject = codeGenerator.arrayObject();
        codeGenerator.addToArray(getArrObject,propIdentifier);
        codeGenerator.addToArray(getArrObject,getIdentifier);

        Object setArrObject = codeGenerator.arrayObject();
        codeGenerator.addToArray(setArrObject,propIdentifier);
        codeGenerator.addToArray(setArrObject,setIdentifier);

        Object getExpressionStatementNode = codeGenerator.getNodeFactory().expressionStatementNode(codeGenerator.getNodeFactory().callExpressionNode(getterMember,getArrObject));
        codeGenerator.getNodeUtils().insertNodeToBody(getExpressionStatementNode,node);

        Object setExpressionStatementNode = codeGenerator.getNodeFactory().expressionStatementNode(codeGenerator.getNodeFactory().callExpressionNode(setterMember,setArrObject));
        codeGenerator.getNodeUtils().insertNodeToBody(setExpressionStatementNode,node);
        return HandleStatus.createNormalStatus();
    }
}
