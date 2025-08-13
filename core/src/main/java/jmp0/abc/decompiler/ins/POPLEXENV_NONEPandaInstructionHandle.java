package jmp0.abc.decompiler.ins;

import jmp0.abc.decompiler.PandaDecompilerMethodHandler;
import jmp0.abc.disasm.block.PandaIRBasicBlock;
import jmp0.abc.disasm.ins.load.POPLEXENV_NONEPandaInstruction;
import jmp0.abc.disasm.lexical.PandaLexical;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class POPLEXENV_NONEPandaInstructionHandle implements IInstructionHandle<POPLEXENV_NONEPandaInstruction>{
    @Override
    public HandleStatus handle(POPLEXENV_NONEPandaInstruction instruction, PandaIRBasicBlock block, PandaDecompilerMethodHandler methodHandler, PandaLexical pandaLexical, Object node) {
        methodHandler.getCodeGenerator().getNodeUtils().covert2SingleBlock(node);
        return HandleStatus.createNormalStatus();
    }
}
