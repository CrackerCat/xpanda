package jmp0.abc.disasm.ins.visitors.ldobj;

import jmp0.abc.disasm.ins.PandaInstruction;
import jmp0.abc.disasm.param.IPandaInstructionParam;
import jmp0.abc.disasm.param.PandaInstructionACC;
import jmp0.abc.disasm.param.PandaInstructionVReg;
import jmp0.abc.disasm.types.PandaOPCode;
import jmp0.abc.file.method.PandaMethod;
import lombok.Getter;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
@Getter
public abstract class LoadObjectByPandaInstruction extends PandaInstruction {
    protected final IPandaInstructionParam obj;
    private boolean isGlobal = false;
    private boolean isDynamicImport = false;
    public LoadObjectByPandaInstruction(int pc, PandaOPCode opCode, byte[] param, PandaMethod pandaMethod) {
        super(pc, opCode, param, pandaMethod);
        if (opCode == PandaOPCode.DYNAMICIMPORT_NONE){
            this.isDynamicImport = true;
            this.obj = null;
            return;
        }
        this.obj = this.getParams()[1];
        if (opCode == PandaOPCode.LDGLOBALVAR_IMM16_ID16){
            this.isGlobal = true;
        }
    }

    @Override
    public String toString() {
        if (isDynamicImport) return baseToString() + "import(acc)";
        if (isGlobal) return baseToString() + "acc["+ obj.toString() +"]";
        if (obj instanceof PandaInstructionVReg) return baseToString() + obj + "[acc]";
        else return baseToString() + "acc["+ obj.toString() +"]";
    }
}
