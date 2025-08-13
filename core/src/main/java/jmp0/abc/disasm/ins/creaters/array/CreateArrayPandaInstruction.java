package jmp0.abc.disasm.ins.creaters.array;

import jmp0.abc.disasm.ins.PandaInstruction;
import jmp0.abc.disasm.param.PandaInstructionID;
import jmp0.abc.disasm.types.PandaOPCode;
import jmp0.abc.file.desc.Offset;
import jmp0.abc.file.literal.PandaLiteralArray;
import jmp0.abc.file.method.PandaMethod;
import lombok.Getter;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
@Getter
public abstract class CreateArrayPandaInstruction extends PandaInstruction {
    private final Offset[] initArr;
    public CreateArrayPandaInstruction(int pc, PandaOPCode opCode, byte[] param, PandaMethod pandaMethod) {
        super(pc, opCode, param, pandaMethod);
        if (opCode == PandaOPCode.CREATEEMPTYARRAY_IMM8 || opCode == PandaOPCode.CREATEEMPTYARRAY_IMM16){
            this.initArr = null;
        }else {
            PandaInstructionID instructionID = (PandaInstructionID)this.getParams()[1];
            PandaLiteralArray literalArray = (PandaLiteralArray) instructionID.getObj();
            this.initArr = literalArray.getPandaLiterals();
        }

    }

    @Override
    public String toString() {
        if(initArr == null){
            return baseToString() + "[]";
        }
        StringBuilder builder = new StringBuilder(baseToString());
        builder.append('[');
        for (int i = 0; i < this.initArr.length; i++) {
            builder.append(this.initArr[i]);
            if (i != this.initArr.length-1) builder.append(',');
        }
        builder.append(']');
        return builder.toString();
    }
}
