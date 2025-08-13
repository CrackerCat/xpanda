package jmp0.abc.decompiler.ins;

import jmp0.abc.decompiler.PandaDecompilerMethodHandler;
import jmp0.abc.disasm.block.PandaIRBasicBlock;
import jmp0.abc.disasm.ins.unary.UnaryPandaInstruction;
import jmp0.abc.decompiler.codegen.CodeGenerator;
import jmp0.abc.disasm.lexical.PandaLexical;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class UnaryPandaInstructionHandle implements IInstructionHandle<UnaryPandaInstruction>{
    @Override
    public HandleStatus handle(UnaryPandaInstruction instruction, PandaIRBasicBlock block, PandaDecompilerMethodHandler methodHandler, PandaLexical pandaLexical, Object node) {
        CodeGenerator codeGenerator = methodHandler.getCodeGenerator();
        String op = instruction.getType().getOp();
        switch (op) {
            case "":
                //donothing
                break;
            case "!!": {
                Object unaryObject = codeGenerator.getNodeFactory().unaryExpressionNode("!", codeGenerator.getNodeFactory().identifierNode("acc"));
                unaryObject = codeGenerator.getNodeFactory().unaryExpressionNode("!", unaryObject);
                Object assignmentExpressionNode = codeGenerator.getNodeFactory().assignmentExpressionNode("=", codeGenerator.getNodeFactory().identifierNode("acc"), unaryObject);
                Object expressionStatementNode = codeGenerator.getNodeFactory().expressionStatementNode(assignmentExpressionNode);
                codeGenerator.getNodeUtils().insertNodeToBody(expressionStatementNode, node);
                break;
            }
            case "--":
            case "++": {
                //desugar
                Object binaryExpressionNode = codeGenerator.getNodeFactory().binaryExpressionNode(op.substring(1), codeGenerator.getNodeFactory().identifierNode("acc"),codeGenerator.getNodeFactory().numberLiteralNode(1));
                Object assignmentExpressionNode = codeGenerator.getNodeFactory().assignmentExpressionNode('=',codeGenerator.getNodeFactory().identifierNode("acc"),binaryExpressionNode);
                Object expressionStatementNode = codeGenerator.getNodeFactory().expressionStatementNode(assignmentExpressionNode);
                codeGenerator.getNodeUtils().insertNodeToBody(expressionStatementNode, node);
                break;
            }
            default: {
                Object unaryObject = codeGenerator.getNodeFactory().unaryExpressionNode(op, codeGenerator.getNodeFactory().identifierNode("acc"));
                Object assignmentExpressionNode = codeGenerator.getNodeFactory().assignmentExpressionNode("=", codeGenerator.getNodeFactory().identifierNode("acc"), unaryObject);
                Object expressionStatementNode = codeGenerator.getNodeFactory().expressionStatementNode(assignmentExpressionNode);
                codeGenerator.getNodeUtils().insertNodeToBody(expressionStatementNode, node);
                break;
            }
        }
        return HandleStatus.createNormalStatus();
    }
}
