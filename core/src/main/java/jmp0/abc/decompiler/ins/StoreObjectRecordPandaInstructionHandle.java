package jmp0.abc.decompiler.ins;

import jmp0.abc.decompiler.PandaDecompilerMethodHandler;
import jmp0.abc.decompiler.codegen.CodeGenerator;
import jmp0.abc.disasm.block.PandaIRBasicBlock;
import jmp0.abc.disasm.ins.visitors.stobjrec.StoreObjectRecordPandaInstruction;
import jmp0.abc.disasm.lexical.PandaLexical;
import jmp0.abc.file.desc.PandaString;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class StoreObjectRecordPandaInstructionHandle implements IInstructionHandle<StoreObjectRecordPandaInstruction>{
    @Override
    public HandleStatus handle(StoreObjectRecordPandaInstruction instruction, PandaIRBasicBlock block, PandaDecompilerMethodHandler methodHandler, PandaLexical pandaLexical, Object node) {
        CodeGenerator codeGenerator = methodHandler.getCodeGenerator();
        String kind = instruction.getType().name().toLowerCase();
        String name = ((PandaString)instruction.getName().getObj()).getContent();
        Object declarator = codeGenerator.getNodeFactory().variableDeclaratorNode(codeGenerator.getNodeFactory().identifierNode(name),codeGenerator.getNodeFactory().identifierNode("acc"));
        Object array = codeGenerator.arrayObject();
        codeGenerator.addToArray(array,declarator);
        codeGenerator.getNodeUtils().insertNodeToBody(codeGenerator.getNodeFactory().variableDeclarationNode(array,kind),node);
        return HandleStatus.createNormalStatus();
    }
}
