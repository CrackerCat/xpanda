package jmp0.abc.decompiler.ins;

import jmp0.abc.decompiler.PandaDecompilerMethodHandler;
import jmp0.abc.disasm.block.PandaIRBasicBlock;
import jmp0.abc.disasm.ins.iterator.GetIteratorPandaInstruction;
import jmp0.abc.disasm.lexical.PandaLexical;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class GetIteratorPandaInstructionHandle implements IInstructionHandle<GetIteratorPandaInstruction>{
    @Override
    public HandleStatus handle(GetIteratorPandaInstruction instruction, PandaIRBasicBlock block, PandaDecompilerMethodHandler methodHandler, PandaLexical pandaLexical, Object node) {
        Object dest = null;
        if (instruction.isProp()){
            dest = methodHandler.getCodeGenerator().getNodeFactory().parse("acc = Object.keys(acc)[Symbol.iterator]()");
        }else{
            dest = methodHandler.getCodeGenerator().getNodeFactory().parse("acc = acc[Symbol.iterator]()");
        }
        methodHandler.getCodeGenerator().getNodeUtils().insertToProgram(dest,node);
        return HandleStatus.createNormalStatus();
    }
}
