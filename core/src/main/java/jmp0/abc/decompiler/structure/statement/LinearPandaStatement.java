package jmp0.abc.decompiler.structure.statement;

import jmp0.abc.decompiler.PandaDecompilerMethodHandler;
import jmp0.abc.disasm.block.PandaIRBasicBlock;
import jmp0.abc.disasm.lexical.PandaLexical;
import lombok.Getter;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
@Getter
public final class LinearPandaStatement implements PandaStatement {
    private final PandaIRBasicBlock block;
    public LinearPandaStatement(PandaIRBasicBlock pandaIRBasicBlock){
        this.block = pandaIRBasicBlock;
    }
    @Override
    public void decompile(PandaDecompilerMethodHandler methodHandler, PandaLexical pandaLexical, Object astNode) {
        methodHandler.handleBlock(block,pandaLexical, astNode);
    }
}
