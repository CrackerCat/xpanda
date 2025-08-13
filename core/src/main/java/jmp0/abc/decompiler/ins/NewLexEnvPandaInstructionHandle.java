package jmp0.abc.decompiler.ins;

import jmp0.abc.decompiler.PandaDecompilerMethodHandler;
import jmp0.abc.disasm.block.PandaIRBasicBlock;
import jmp0.abc.disasm.ins.creaters.NewLexEnvPandaInstruction;
import jmp0.abc.decompiler.codegen.CodeGenerator;
import jmp0.abc.disasm.lexical.PandaLexical;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class NewLexEnvPandaInstructionHandle implements IInstructionHandle<NewLexEnvPandaInstruction>{
    @Override
    public HandleStatus handle(NewLexEnvPandaInstruction instruction, PandaIRBasicBlock block, PandaDecompilerMethodHandler methodHandler, PandaLexical pandaLexical, Object node) {
        CodeGenerator codeGenerator = methodHandler.getCodeGenerator();
        int nowLevel = pandaLexical.getLevel(block.getParent(),instruction.getPC());
        String name = "local";
        if (instruction.isSendAble()){
            name = "sendable";
        }
        name += "_" + nowLevel + "_";
        int local_nums = instruction.getImm().intValue();
        Object arrayObject = codeGenerator.arrayObject();
        for (int i = 0; i < local_nums; i++) {
            Object variableDeclaratorNode = codeGenerator.getNodeFactory().variableDeclaratorNode(codeGenerator.getNodeFactory().identifierNode(name+i));
            codeGenerator.addToArray(arrayObject,variableDeclaratorNode);
        }
        Object variableDeclarationNode = codeGenerator.getNodeFactory().variableDeclarationNode(arrayObject,"let");
        codeGenerator.getNodeUtils().insertNodeToBody(codeGenerator.getNodeFactory().expressionStatementNode(codeGenerator.getNodeFactory().identifierNode("panda_jmp0_reserved_lex_begin")),node);
        codeGenerator.getNodeUtils().insertNodeToBody(variableDeclarationNode,node);
        //set node extern lex
        return HandleStatus.createNormalStatus();
    }
}
