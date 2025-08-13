package jmp0.abc.disasm.ins.visitors;

import jmp0.abc.disasm.ins.PandaInstruction;
import jmp0.abc.disasm.param.PandaInstructionIMM;
import jmp0.abc.disasm.types.PandaOPCode;
import jmp0.abc.file.clazz.PandaClassExportModule;
import jmp0.abc.file.method.PandaMethod;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class LDLOCALMODULEVAR_IMM8PandaInstruction extends LoadModuleVarPandaInstruction {
    public LDLOCALMODULEVAR_IMM8PandaInstruction(int pc, PandaOPCode opCode, byte[] param, PandaMethod pandaMethod) {
        super(pc, opCode, param, pandaMethod);
    }
}
