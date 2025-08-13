package jmp0.abc.decompiler.ins;

import jmp0.abc.decompiler.PandaDecompilerMethodHandler;
import jmp0.abc.disasm.block.PandaIRBasicBlock;
import jmp0.abc.disasm.ins.ret.ReturnPandaInstruction;
import jmp0.abc.decompiler.codegen.CodeGenerator;
import jmp0.abc.disasm.lexical.PandaLexical;
import jmp0.abc.file.PandaFile;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class ReturnPandaInstructionHandle implements IInstructionHandle<ReturnPandaInstruction>{
    @Override
    public HandleStatus handle(ReturnPandaInstruction instruction, PandaIRBasicBlock block, PandaDecompilerMethodHandler methodHandler, PandaLexical pandaLexical, Object node) {
        if (block.getParent().getName().getContent().equals(PandaFile.ENTRY_FUNCTION_MAIN)) return HandleStatus.createNormalStatus();;
        CodeGenerator codeGenerator = methodHandler.getCodeGenerator();
        Object returnStatementNode;
        if (instruction.isUndefined()){
            returnStatementNode = codeGenerator.getNodeFactory().returnStatementNode();
        }else {
            returnStatementNode = codeGenerator.getNodeFactory().returnStatementNode(codeGenerator.getNodeFactory().identifierNode("acc"));
        }
        codeGenerator.getNodeUtils().insertNodeToBody(returnStatementNode,node);
        return HandleStatus.createNormalStatus();
    }
}
