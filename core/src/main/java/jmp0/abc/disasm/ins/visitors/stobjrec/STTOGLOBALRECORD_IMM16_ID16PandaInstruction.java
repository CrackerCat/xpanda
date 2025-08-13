package jmp0.abc.disasm.ins.visitors.stobjrec;

import jmp0.abc.disasm.types.PandaOPCode;
import jmp0.abc.file.method.PandaMethod;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class STTOGLOBALRECORD_IMM16_ID16PandaInstruction extends StoreObjectRecordPandaInstruction{
    public STTOGLOBALRECORD_IMM16_ID16PandaInstruction(int pc, PandaOPCode opCode, byte[] param, PandaMethod pandaMethod) {
        super(pc, opCode, param, pandaMethod);
    }
}
