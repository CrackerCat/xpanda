package jmp0.abc.decompiler.ins;

import jmp0.abc.decompiler.PandaDecompilerMethodHandler;
import jmp0.abc.disasm.block.PandaIRBasicBlock;
import jmp0.abc.disasm.ins.creaters.obj.CreateObjectPandaInstruction;
import jmp0.abc.decompiler.PandaDecompileException;
import jmp0.abc.decompiler.codegen.CodeGenerator;
import jmp0.abc.disasm.lexical.PandaLexical;
import jmp0.abc.file.desc.Offset;
import jmp0.abc.file.desc.PandaString;
import jmp0.abc.file.literal.PandaByteEightLiteral;
import jmp0.abc.file.literal.PandaByteFourLiteral;
import jmp0.abc.file.literal.PandaByteOneLiteral;
import jmp0.abc.file.method.PandaMethod;
import lombok.SneakyThrows;

import java.util.Map;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class CreateObjectPandaInstructionHandle implements IInstructionHandle<CreateObjectPandaInstruction>{
    @SneakyThrows
    private Object generateProperties(PandaDecompilerMethodHandler pandaDecompilerMethodHandler, Map<String,Offset> map,PandaLexical pandaLexical){
        CodeGenerator codeGenerator = pandaDecompilerMethodHandler.getCodeGenerator();
        Object arrayObject = codeGenerator.arrayObject();
        for (String key : map.keySet()) {
            Offset value = map.get(key);
            if (value instanceof PandaString){
                Object propertyNode = codeGenerator.getNodeFactory().objectPropertyNode(
                        codeGenerator.getNodeFactory().stringLiteralNode(key),
                        codeGenerator.getNodeFactory().stringLiteralNode(((PandaString) value).getContent())
                );
                codeGenerator.addToArray(arrayObject,propertyNode);
            }else if(value instanceof PandaByteFourLiteral){
                Number number = ((PandaByteFourLiteral) value).getValue();
                Object propertyNode = codeGenerator.getNodeFactory().objectPropertyNode(
                        codeGenerator.getNodeFactory().stringLiteralNode(key),
                        codeGenerator.getNodeFactory().numberLiteralNode(number)
                );
                codeGenerator.addToArray(arrayObject,propertyNode);
            }else if (value instanceof PandaByteOneLiteral){
                boolean real = value.toString().equals("true");
                Object propertyNode = codeGenerator.getNodeFactory().objectPropertyNode(
                        codeGenerator.getNodeFactory().stringLiteralNode(key),
                        codeGenerator.getNodeFactory().booleanLiteralNode(real)
                );
                codeGenerator.addToArray(arrayObject,propertyNode);
            } else if (value instanceof PandaMethod){
                Object propertyNode = codeGenerator.getNodeFactory().objectPropertyNode(
                        codeGenerator.getNodeFactory().stringLiteralNode(key),
                        pandaDecompilerMethodHandler.decompileFunction((PandaMethod) value,pandaLexical)
                );
                codeGenerator.addToArray(arrayObject,propertyNode);
            }else if(value instanceof PandaByteEightLiteral){
                Number number = ((PandaByteEightLiteral) value).getValue();
                Object propertyNode = codeGenerator.getNodeFactory().objectPropertyNode(
                        codeGenerator.getNodeFactory().stringLiteralNode(key),
                        codeGenerator.getNodeFactory().numberLiteralNode(number)
                );
                codeGenerator.addToArray(arrayObject,propertyNode);
            }else throw new PandaDecompileException("instanceof value not recognized!");

        }
        return arrayObject;
    }
    @Override
    public HandleStatus handle(CreateObjectPandaInstruction instruction, PandaIRBasicBlock block, PandaDecompilerMethodHandler methodHandler, PandaLexical pandaLexical, Object node) {
        CodeGenerator codeGenerator = methodHandler.getCodeGenerator();
        if (instruction.isExcludeObject()){
            Object arrayObject = codeGenerator.arrayObject();
            Object excludeFormObj  = codeGenerator.getNodeFactory().identifierNode(instruction.getExcludeFormObj().toString());
            codeGenerator.addToArray(arrayObject,excludeFormObj);
            Object excludeObj  = codeGenerator.getNodeFactory().identifierNode(instruction.getExcludeObj().toString());
            codeGenerator.addToArray(arrayObject,excludeObj);
            Object excludeIndexNode  = codeGenerator.getNodeFactory().numberLiteralNode(instruction.getExcludeIndex().getImm());
            codeGenerator.addToArray(arrayObject,excludeIndexNode);
            Object callExpressionNode = codeGenerator.getNodeFactory().callExpressionNode(codeGenerator.getNodeFactory().identifierNode("panda_create_exclude_object_polyfill"),arrayObject);
            Object assignmentExpressionNode = codeGenerator.getNodeFactory().assignmentExpressionNode("=",codeGenerator.getNodeFactory().identifierNode("acc"),callExpressionNode);
            Object expressionStatementNode = codeGenerator.getNodeFactory().expressionStatementNode(assignmentExpressionNode);
            codeGenerator.getNodeUtils().insertNodeToBody(expressionStatementNode,node);
            return HandleStatus.createNormalStatus();
        }
        Object objNode = codeGenerator.getNodeFactory().objectExpressionNode(generateProperties(methodHandler,instruction.getObjMap(),pandaLexical));
        Object assignmentExpressionNode = codeGenerator.getNodeFactory().assignmentExpressionNode("=",codeGenerator.getNodeFactory().identifierNode("acc"),objNode);
        Object expressionStatementNode = codeGenerator.getNodeFactory().expressionStatementNode(assignmentExpressionNode);
        codeGenerator.getNodeUtils().insertNodeToBody(expressionStatementNode,node);
        return HandleStatus.createNormalStatus();
    }
}
