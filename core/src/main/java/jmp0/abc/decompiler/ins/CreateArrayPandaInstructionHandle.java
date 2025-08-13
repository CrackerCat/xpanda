package jmp0.abc.decompiler.ins;

import jmp0.abc.decompiler.PandaDecompilerMethodHandler;
import jmp0.abc.disasm.block.PandaIRBasicBlock;
import jmp0.abc.disasm.ins.creaters.array.CreateArrayPandaInstruction;
import jmp0.abc.decompiler.codegen.CodeGenerator;
import jmp0.abc.disasm.lexical.PandaLexical;
import jmp0.abc.file.desc.Offset;
import jmp0.abc.file.desc.PandaString;
import jmp0.abc.file.literal.PandaLiteral;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class CreateArrayPandaInstructionHandle implements IInstructionHandle<CreateArrayPandaInstruction>{
    @Override
    public HandleStatus handle(CreateArrayPandaInstruction instruction, PandaIRBasicBlock block, PandaDecompilerMethodHandler methodHandler, PandaLexical pandaLexical, Object node) {
        CodeGenerator codeGenerator = methodHandler.getCodeGenerator();
        Object arrayObject = codeGenerator.arrayObject();
        if (instruction.getInitArr() != null){
            for (Offset offset : instruction.getInitArr()) {
                if (offset instanceof PandaLiteral){
                    switch (((PandaLiteral) offset).getTag()){
                        case INTEGER:
                        case FLOAT:
                        case DOUBLE:{
                            codeGenerator.addToArray(arrayObject,codeGenerator.getNodeFactory().numberLiteralNode(((PandaLiteral) offset).getValue()));
                            break;
                        }
                        case STRING:{
                            codeGenerator.addToArray(arrayObject,codeGenerator.getNodeFactory().stringLiteralNode(offset.toString()));
                            break;
                        }
                        default:{
                            codeGenerator.addToArray(arrayObject,codeGenerator.getNodeFactory().identifierNode(offset.toString()));
                        }
                    }

                }else if (offset instanceof PandaString){
                    codeGenerator.addToArray(arrayObject,codeGenerator.getNodeFactory().stringLiteralNode(((PandaString) offset).getContent()));
                }else codeGenerator.addToArray(arrayObject,codeGenerator.getNodeFactory().identifierNode(offset.toString()));

            }
        }
        Object arrayExpressionNode = codeGenerator.getNodeFactory().arrayExpressionNode(arrayObject);
        Object assignmentExpressionNode = codeGenerator.getNodeFactory().assignmentExpressionNode("=",codeGenerator.getNodeFactory().identifierNode("acc"),arrayExpressionNode);
        Object expressionStatementNode = codeGenerator.getNodeFactory().expressionStatementNode(assignmentExpressionNode);
        codeGenerator.getNodeUtils().insertNodeToBody(expressionStatementNode,node);
        return HandleStatus.createNormalStatus();
    }
}
