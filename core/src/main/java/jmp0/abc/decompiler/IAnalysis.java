package jmp0.abc.decompiler;

import jmp0.abc.disasm.lexical.PandaLexical;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public interface IAnalysis {
    void analysis(PandaDecompilerMethodHandler methodHandler, PandaLexical pandaLexical, Object astNode);
}
