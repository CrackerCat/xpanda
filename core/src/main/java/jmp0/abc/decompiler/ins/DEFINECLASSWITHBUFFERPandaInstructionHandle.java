package jmp0.abc.decompiler.ins;

import jmp0.abc.decompiler.PandaDecompileException;
import jmp0.abc.decompiler.PandaDecompilerMethodHandler;
import jmp0.abc.disasm.block.PandaIRBasicBlock;
import jmp0.abc.disasm.ins.definition.DEFINECLASSWITHBUFFERPandaInstruction;
import jmp0.abc.decompiler.codegen.CodeGenerator;
import jmp0.abc.disasm.lexical.PandaLexical;
import jmp0.abc.file.literal.PandaLiteral;
import jmp0.abc.file.method.PandaMethod;
import lombok.SneakyThrows;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class DEFINECLASSWITHBUFFERPandaInstructionHandle implements IInstructionHandle<DEFINECLASSWITHBUFFERPandaInstruction>{

    @SneakyThrows
    @Override
    public HandleStatus handle(DEFINECLASSWITHBUFFERPandaInstruction instruction, PandaIRBasicBlock block, PandaDecompilerMethodHandler methodHandler, PandaLexical pandaLexical, Object node) {
        CodeGenerator codeGenerator = methodHandler.getCodeGenerator();
        PandaMethod constructorMethod = instruction.getConstructorMethod();
        String className = constructorMethod.getName().getContent();
        if (className.startsWith("#")){
            className = "panda_jmp0_reserved_anonymous_"+ methodHandler.getNextID();
        }else {
            className = "panda_jmp0_reserved_class_"+ methodHandler.getNextID();
        }
        Object id = codeGenerator.getNodeFactory().identifierNode(className);
        Object superClass = codeGenerator.getNodeFactory().identifierNode(instruction.getProto().toString());
        Object functionExp = methodHandler.decompileFunction(constructorMethod,pandaLexical);
        Object constructorMethodNode = codeGenerator.getNodeFactory().classMethodNode(codeGenerator.getNodeFactory().identifierNode("constructor"),functionExp,"constructor",false,false);
        Object arrayObject = codeGenerator.arrayObject();
        if (instruction.isSendClass()) for (DEFINECLASSWITHBUFFERPandaInstruction.SendClassVariableDescription sendClassVariableDescription : instruction.getSendClassVariableDescriptions()) {
            String name = sendClassVariableDescription.getVariableName();
            PandaLiteral literal = sendClassVariableDescription.getVariableValue();
            Object valueObject = null;
            switch (literal.getTag()){
                case DOUBLE:
                case FLOAT:
                case INTEGER :{
                    valueObject = codeGenerator.getNodeFactory().numberLiteralNode(literal.getValue());
                    break;
                }
                default:{
                    throw new PandaDecompileException(String.format("%s not support!",literal.getTag().name()));
                }
            }
            Object propertyNode = codeGenerator.getNodeFactory().classPropertyNode(codeGenerator.getNodeFactory().identifierNode(name),valueObject);
            codeGenerator.addToArray(arrayObject,propertyNode);
        }
        codeGenerator.addToArray(arrayObject,constructorMethodNode);
        for (DEFINECLASSWITHBUFFERPandaInstruction.MethodDescription methodDescription : instruction.getMethodDescriptions()) {
            Object methodNode = methodHandler.decompileFunction(methodDescription.getMethod(),pandaLexical);
            Object memberMethodNode = codeGenerator.getNodeFactory().classMethodNode(codeGenerator.getNodeFactory().identifierNode(methodDescription.getProtoMethodName()),methodNode,"method",false,false);
            codeGenerator.addToArray(arrayObject,memberMethodNode);
        }
        for (DEFINECLASSWITHBUFFERPandaInstruction.MethodDescription methodDescription : instruction.getStaticMethodDescriptions()) {
            Object methodNode = methodHandler.decompileFunction(methodDescription.getMethod(),pandaLexical);
            Object memberMethodNode = codeGenerator.getNodeFactory().classMethodNode(codeGenerator.getNodeFactory().identifierNode(methodDescription.getProtoMethodName()),methodNode,"method",false,true);
            codeGenerator.addToArray(arrayObject,memberMethodNode);
        }
        Object body = codeGenerator.getNodeFactory().classBodyNode(arrayObject);
        Object clazz = codeGenerator.getNodeFactory().classExpressionNode(id,superClass,body);
        Object assignmentExpressionNode = codeGenerator.getNodeFactory().assignmentExpressionNode("=",codeGenerator.getNodeFactory().identifierNode("acc"),clazz);
        Object expressionStatementNode = codeGenerator.getNodeFactory().expressionStatementNode(assignmentExpressionNode);
        codeGenerator.getNodeUtils().insertNodeToBody(expressionStatementNode,node);
        return HandleStatus.createNormalStatus();
    }
}
