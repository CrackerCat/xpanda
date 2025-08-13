package jmp0.abc.disasm.ins.unary;

import jmp0.abc.disasm.ins.PandaInstruction;
import jmp0.abc.disasm.types.PandaOPCode;
import jmp0.abc.file.method.PandaMethod;
import lombok.Getter;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
@Getter
public abstract class UnaryPandaInstruction extends PandaInstruction {
    @Getter
    public enum TYPE {
        TYPEOF("typeof"),
        TONUMBER(""),
        TONUMERIC(""),
        NEG("-"),
        NOT("~"),
        INC("++"),
        DEC("--"),
        ISTRUE("!!"),
        ISFALSE("!");
        private final String op;
        TYPE(String op){
            this.op = op;
        }

    }

    private final TYPE type;
    public UnaryPandaInstruction(int pc, PandaOPCode opCode, byte[] param, PandaMethod pandaMethod,TYPE type) {
        super(pc, opCode, param, pandaMethod);
        this.type = type;
    }

    @Override
    public String toString() {
        if (type == TYPE.TYPEOF) return baseToString() + type.getOp() + " acc";
        return baseToString() + type.getOp() + "acc";
    }
}
