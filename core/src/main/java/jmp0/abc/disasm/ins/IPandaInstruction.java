package jmp0.abc.disasm.ins;

import jmp0.abc.disasm.param.IPandaInstructionParam;
import jmp0.abc.disasm.types.PandaOPCode;
import jmp0.abc.disasm.types.PandaOPCodeFormat;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public interface IPandaInstruction {
    int getPC();

    PandaOPCode getOpCode();
    PandaOPCodeFormat getFormat();
    IPandaInstructionParam[] getParams();
}
