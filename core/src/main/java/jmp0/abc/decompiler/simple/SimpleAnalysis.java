package jmp0.abc.decompiler.simple;

import jmp0.abc.decompiler.IAnalysis;
import jmp0.abc.decompiler.PandaDecompilerMethodHandler;
import jmp0.abc.decompiler.codegen.CodeGenerator;
import jmp0.abc.decompiler.structure.statement.LinearPandaStatement;
import jmp0.abc.disasm.block.PandaIRBasicBlock;
import jmp0.abc.disasm.block.PandaIRCFG;
import jmp0.abc.disasm.ins.PandaInstruction;
import jmp0.abc.disasm.ins.jump.JumpDirectPandaInstruction;
import jmp0.abc.disasm.ins.jump.JumpEqualPandaInstruction;
import jmp0.abc.disasm.ins.jump.JumpNotEqualPandaInstruction;
import jmp0.abc.disasm.ins.jump.JumpPandaInstruction;
import jmp0.abc.disasm.lexical.PandaLexical;
import jmp0.abc.disasm.types.PandaOPCode;
import jmp0.abc.file.method.PandaMethodCatchBlock;
import jmp0.abc.file.method.PandaMethodTryBlock;
import jmp0.abc.util.PandaLogger;

import java.util.HashSet;
import java.util.LinkedList;

import static jmp0.abc.decompiler.structure.RegionGraphBuilder.checkIsAsyncException;
import static jmp0.abc.decompiler.structure.RegionGraphBuilder.checkIsGeneratorException;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class SimpleAnalysis implements IAnalysis {
    private final PandaLogger logger = new PandaLogger(SimpleAnalysis.class);
    private final PandaIRCFG irCFG;
    private boolean isAsyncFunction = false;
    private boolean isGeneratorFunction = false;
    private final HashSet<PandaIRBasicBlock> excpetionHandleBlocks = new HashSet<>();
    private final LinkedList<LinearPandaStatement> statements = new LinkedList<>();
    public SimpleAnalysis(PandaIRCFG pandaIRCFG){
        this.irCFG = pandaIRCFG;
        this.checkIsAsync();
        this.checkAndSpecialFunction();
        pandaIRCFG.getPandaIRBasicBlocks().forEach(basicBlock -> statements.add(new LinearPandaStatement(basicBlock)));
    }

    private void checkAndSpecialFunction(){
        if (irCFG.getPandaMethod().hasException()){
            for (PandaMethodTryBlock pandaMethodTryBlock : irCFG.getPandaMethod().getMethodCode().getPandaMethodTryBlocks()) {
                PandaMethodCatchBlock pandaMethodCatchBlock = pandaMethodTryBlock.getPandaMethodCatchBlocks()[0];
                PandaIRBasicBlock tryEntryBlock = irCFG.getBasicBlockByPC(pandaMethodTryBlock.getStartPC().intValue());
                int tryCodeLength = pandaMethodTryBlock.getLength().intValue();
                PandaIRBasicBlock handleEntryBlock = irCFG.getBasicBlockByPC(pandaMethodCatchBlock.getHandlerPC().intValue());
                int handleCodeLength =  pandaMethodCatchBlock.getCodeSize().intValue();
                if (tryCodeLength == 0 || handleCodeLength == 0){
                    logger.logD("checkAndSpecialFunction ignore exception",String.format("%s %s, because of tryCodeLength or handleCodeLength is null.",tryEntryBlock.getName(),handleEntryBlock.getName()));
                    continue;
                }
                excpetionHandleBlocks.add(handleEntryBlock);
                if (checkIsAsyncException(handleEntryBlock)){
                    logger.logD("checkAndSpecialFunction ignore exception",String.format("%s %s",tryEntryBlock.getName(),handleEntryBlock.getName()));
                    isAsyncFunction = true;
                    continue;
                }
                if (checkIsGeneratorException(tryEntryBlock)){
                    logger.logD("checkAndSpecialFunction ignore exception",String.format("%s %s",tryEntryBlock.getName(),handleEntryBlock.getName()));
                    isGeneratorFunction = true;
                }
            }
        }
    }

    private void checkIsAsync(){
        for (PandaInstruction pandaInstruction : irCFG.getEntryBlock().getPandaInstructions()) {
            if (pandaInstruction.getOpCode() == PandaOPCode.ASYNCFUNCTIONENTER_NONE) {
                isAsyncFunction = true;
                break;
            }
        }
    }

    @Override
    public void analysis(PandaDecompilerMethodHandler methodHandler, PandaLexical pandaLexical, Object astNode) {
        CodeGenerator codeGenerator = methodHandler.getCodeGenerator();
        codeGenerator.getNodeFactory().addLeadingComment(astNode,"The function was created by SimpleAnalysis,because StructureAnalysis failed.");
        if (isAsyncFunction){
            codeGenerator.getNodeUtils().setIsAsyncBlock(astNode);
        }
        if (isGeneratorFunction){
            codeGenerator.getNodeUtils().setIsGeneratorBlock(astNode);
        }
        for (LinearPandaStatement statement : statements) {
            Object blockStatementNode = codeGenerator.getNodeFactory().blockStatementNode();
            if (statement.getBlock().getCatchBlockList() != null) {
                for (PandaIRBasicBlock pandaIRBasicBlock : statement.getBlock().getCatchBlockList()) {
                    Object arrayObject = codeGenerator.arrayObject();
                    codeGenerator.addToArray(arrayObject,codeGenerator.getNodeFactory().identifierNode(pandaIRBasicBlock.getName()));
                    Object callExpressionNode = codeGenerator.getNodeFactory().callExpressionNode(codeGenerator.getNodeFactory().identifierNode("decompiler_exception_handled_by"),arrayObject);
                    codeGenerator.getNodeUtils().insertNodeToBody(codeGenerator.getNodeFactory().expressionStatementNode(callExpressionNode), blockStatementNode);
                }
            }
            if (excpetionHandleBlocks.contains(statement.getBlock())){
                Object assignmentExpressionNode = codeGenerator.getNodeFactory().assignmentExpressionNode("=",codeGenerator.getNodeFactory().identifierNode("acc"),codeGenerator.getNodeFactory().identifierNode("decompiler_exception_detail"));
                codeGenerator.getNodeUtils().insertNodeToBody(codeGenerator.getNodeFactory().expressionStatementNode(assignmentExpressionNode), blockStatementNode);
            }
            statement.decompile(methodHandler,pandaLexical , blockStatementNode);
            PandaInstruction instruction = statement.getBlock().getTerminator();
            if (instruction instanceof JumpDirectPandaInstruction){
                Object arrayObject = codeGenerator.arrayObject();
                codeGenerator.addToArray(arrayObject,codeGenerator.getNodeFactory().identifierNode(((JumpDirectPandaInstruction) instruction).getDestLabelName()));
                Object callExpressionNode = codeGenerator.getNodeFactory().callExpressionNode(codeGenerator.getNodeFactory().identifierNode("decompiler_goto"),arrayObject);
                codeGenerator.getNodeUtils().insertNodeToBody(codeGenerator.getNodeFactory().expressionStatementNode(callExpressionNode), blockStatementNode);
            } else if (instruction instanceof JumpEqualPandaInstruction || instruction instanceof JumpNotEqualPandaInstruction) {
                Object arrayObject = codeGenerator.arrayObject();
                Object labelObject = codeGenerator.getNodeFactory().identifierNode(((JumpPandaInstruction) instruction).getDestLabelName());
                codeGenerator.addToArray(arrayObject,labelObject);
                Object callExpressionNode = codeGenerator.getNodeFactory().callExpressionNode(codeGenerator.getNodeFactory().identifierNode("decompiler_goto"),arrayObject);
                Object ifTestObject;
                if (instruction instanceof JumpNotEqualPandaInstruction){
                    ifTestObject = codeGenerator.getNodeFactory().identifierNode("acc");
                }else {
                    ifTestObject = codeGenerator.getNodeFactory().unaryExpressionNode("!",codeGenerator.getNodeFactory().identifierNode("acc"));
                }
                Object ifStatementNode = codeGenerator.getNodeFactory().ifStatementNode(ifTestObject,codeGenerator.getNodeFactory().expressionStatementNode(callExpressionNode),null);
                codeGenerator.getNodeUtils().insertNodeToBody(ifStatementNode, blockStatementNode);
            }
            Object labeledStatementNode = codeGenerator.getNodeFactory().labeledStatementNode(statement.getBlock().getName(),blockStatementNode);
            codeGenerator.getNodeUtils().insertNodeToBody(labeledStatementNode, astNode);
        }
    }
}
