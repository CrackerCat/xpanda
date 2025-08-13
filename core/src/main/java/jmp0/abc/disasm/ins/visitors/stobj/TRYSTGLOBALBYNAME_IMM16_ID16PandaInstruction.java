package jmp0.abc.disasm.ins.visitors.stobj;

import jmp0.abc.disasm.types.PandaOPCode;
import jmp0.abc.file.method.PandaMethod;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public class TRYSTGLOBALBYNAME_IMM16_ID16PandaInstruction extends StoreObjectPandaInstruction{
    public TRYSTGLOBALBYNAME_IMM16_ID16PandaInstruction(int pc, PandaOPCode opCode, byte[] param, PandaMethod pandaMethod) {
        super(pc, opCode, param, pandaMethod);
    }
}
