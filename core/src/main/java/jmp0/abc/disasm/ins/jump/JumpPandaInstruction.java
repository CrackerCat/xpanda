package jmp0.abc.disasm.ins.jump;

import jmp0.abc.disasm.ins.PandaInstruction;
import jmp0.abc.disasm.types.PandaOPCode;
import jmp0.abc.file.method.PandaMethod;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public abstract class JumpPandaInstruction extends PandaInstruction {
    public JumpPandaInstruction(int pc, PandaOPCode opCode, byte[] param, PandaMethod pandaMethod) {
        super(pc, opCode, param,pandaMethod);
    }

    public abstract int getDestinationPC();

    public boolean isJumpForward(){
        return getDestinationPC() > getPC();
    }

    public String getDestLabelName(){
        return "label_" + getDestinationPC();
    }

    @Override
    public String toString() {
        return super.toString() +" [" + getDestLabelName() + "]";
    }
}
