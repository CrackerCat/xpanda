package jmp0.abc.disasm.ins.visitors.stmod;

import jmp0.abc.disasm.ins.PandaInstruction;
import jmp0.abc.disasm.param.PandaInstructionIMM;
import jmp0.abc.disasm.types.PandaOPCode;
import jmp0.abc.file.clazz.PandaClassExportModule;
import jmp0.abc.file.method.PandaMethod;
import lombok.Getter;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
@Getter
public abstract class StoreModuleVarPandaInstruction extends PandaInstruction {
    private final PandaClassExportModule module;
    public StoreModuleVarPandaInstruction(int pc, PandaOPCode opCode, byte[] param, PandaMethod pandaMethod) {
        super(pc, opCode, param, pandaMethod);
        int index = ((PandaInstructionIMM)this.getParams()[0]).getImm().intValue();
        this.module = pandaMethod.getParent().getPandaClassModuleData().getExports()[index];
    }

    public String toString() {
        return super.baseToString() + module.getLocalName();
    }
}
