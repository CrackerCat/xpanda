package jmp0.abc.disasm.ins.visitors.ldext;

import jmp0.abc.disasm.ins.PandaInstruction;
import jmp0.abc.disasm.param.PandaInstructionIMM;
import jmp0.abc.disasm.types.PandaOPCode;
import jmp0.abc.file.clazz.PandaClassImportModule;
import jmp0.abc.file.clazz.PandaClassModulePath;
import jmp0.abc.file.method.PandaMethod;
import lombok.Getter;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
@Getter
public abstract class LoadExternalModuleVarPandaInstruction extends PandaInstruction {
    private final PandaClassImportModule module;
    public LoadExternalModuleVarPandaInstruction(int pc, PandaOPCode opCode, byte[] param, PandaMethod pandaMethod) {
        super(pc, opCode, param, pandaMethod);
        int index = ((PandaInstructionIMM)this.getParams()[0]).getImm().intValue();
        if (opCode == PandaOPCode.GETMODULENAMESPACE_IMM8 || opCode == PandaOPCode.WIDE_GETMODULENAMESPACE_PREF_IMM16){
            PandaClassModulePath path = pandaMethod.getParent().getPandaClassModuleData().getRequestedModules()[index];
            this.module = pandaMethod.getParent().getPandaClassModuleData().getPandaClassImportModuleByPath(path);
        }else {
            this.module = pandaMethod.getParent().getPandaClassModuleData().getImports()[index];
        }

    }

    @Override
    public String toString() {
        return super.baseToString() + module.getLocalName();
    }
}
