package jmp0.abc.decompiler.structure;

import jmp0.abc.decompiler.PandaDecompilerMethodHandler;
import jmp0.abc.disasm.lexical.PandaLexical;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public interface IPandaDecompileAble {
    void decompile(PandaDecompilerMethodHandler methodHandler, PandaLexical pandaLexical, Object astNode);
}
