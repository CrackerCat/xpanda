package jmp0.abc.disasm.ins.creaters;

import jmp0.abc.disasm.ins.PandaInstruction;
import jmp0.abc.disasm.param.PandaInstructionIMM;
import jmp0.abc.disasm.param.PandaInstructionVReg;
import jmp0.abc.disasm.types.PandaOPCode;
import jmp0.abc.file.method.PandaMethod;
import lombok.Getter;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
@Getter
public abstract class NewObjectRangePandaInstruction extends PandaInstruction {
    protected final int argSize;
    protected final PandaInstructionVReg objectVReg;
    protected final PandaInstructionVReg[] paramsVReg;
    public NewObjectRangePandaInstruction(int pc, PandaOPCode opCode, byte[] param, PandaMethod pandaMethod) {
        super(pc, opCode, param, pandaMethod);
        this.argSize = ((PandaInstructionIMM)this.getParams()[1]).getImm().intValue();
        this.objectVReg = ((PandaInstructionVReg)this.getParams()[2]);
        this.paramsVReg = new PandaInstructionVReg[argSize-1];
        for (int i = 0; i < paramsVReg.length; i++) {
            this.paramsVReg[i] = new PandaInstructionVReg((short) (objectVReg.getIndex() + i + 1), PandaInstructionVReg.TYPE.VREG,pandaMethod);
        }
    }

    public String toString() {
        StringBuilder builder = new StringBuilder(super.baseToString());
        builder.append(this.objectVReg).append('(');
        for (int i = 0; i < paramsVReg.length ; i++) {
            builder.append(this.paramsVReg[i]);
            if (i != argSize-2) builder.append(',');
        }
        builder.append(')');
        return builder.toString();
    }
}
