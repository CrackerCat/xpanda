package jmp0.abc.file;

import jmp0.abc.disasm.block.PandaIRCFG;
import jmp0.abc.file.clazz.PandaClass;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public interface IDisAssemblyCallBack {
    void CFGCallback(PandaClass pandaClass, PandaIRCFG[] pandaIRCFGS);
    void fakeAssemblyCallback(PandaClass pandaClass, String code);
}
