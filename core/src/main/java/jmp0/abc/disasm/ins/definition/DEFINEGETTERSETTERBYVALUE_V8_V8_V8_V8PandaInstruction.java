package jmp0.abc.disasm.ins.definition;

import jmp0.abc.disasm.ins.PandaInstruction;
import jmp0.abc.disasm.param.PandaInstructionVReg;
import jmp0.abc.disasm.types.PandaOPCode;
import jmp0.abc.file.method.PandaMethod;
import lombok.Getter;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
@Getter
public final class DEFINEGETTERSETTERBYVALUE_V8_V8_V8_V8PandaInstruction extends PandaInstruction {
    private final PandaInstructionVReg obj;
    private final PandaInstructionVReg prop;
    private final PandaInstructionVReg getter;
    private final PandaInstructionVReg setter;

    public DEFINEGETTERSETTERBYVALUE_V8_V8_V8_V8PandaInstruction(int pc, PandaOPCode opCode, byte[] param, PandaMethod pandaMethod) {
        super(pc, opCode, param, pandaMethod);
        this.obj = (PandaInstructionVReg) this.getParams()[0];
        this.prop = (PandaInstructionVReg) this.getParams()[1];
        this.getter = (PandaInstructionVReg) this.getParams()[2];
        this.setter = (PandaInstructionVReg) this.getParams()[3];
    }

    @Override
    public String toString() {
        return baseToString() + obj.toString() + "." + prop.toString() + ":" +
                "get method:" + getter.toString() + " " + "set method:" + setter.toString();
    }
}
