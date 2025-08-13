package jmp0.abc.disasm.ins.binary;

import jmp0.abc.disasm.ins.PandaInstruction;
import jmp0.abc.disasm.param.PandaInstructionVReg;
import jmp0.abc.disasm.types.PandaOPCode;
import jmp0.abc.file.method.PandaMethod;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
@Getter
public abstract class BinaryOperationPandaInstruction extends PandaInstruction {
    private final TYPE type;
    private final PandaInstructionVReg left;
    @Getter
    public enum TYPE{
        ADD("+"), SUB("-"), MUL("*"), DIV("/"),
        MOD("%"),
        LESS("<"), LESS_EQ("<="),
        GREATER(">"), GREATER_EQ(">="),
        EQ("=="), NOT_EQ("!="),
        INSTANCE_OF("instanceof"),IS_IN("in"),
        STRICT_EQ("==="),STRICT_NOT_EQ("!=="),
        SHL("<<"), SHR(">>>"), ASHR(">>"),
        AND("&"), OR("|"), XOR("^"), EXP("**");
        private final String opName;
        TYPE(String opName){
            this.opName = opName;
        }
    }

    static private final Set<TYPE> compareTypeSet = new HashSet<>(){};
    static {
        compareTypeSet.add(TYPE.LESS);
        compareTypeSet.add(TYPE.LESS_EQ);
        compareTypeSet.add(TYPE.GREATER);
        compareTypeSet.add(TYPE.GREATER_EQ);
        compareTypeSet.add(TYPE.EQ);
        compareTypeSet.add(TYPE.NOT_EQ);
        compareTypeSet.add(TYPE.STRICT_EQ);
        compareTypeSet.add(TYPE.STRICT_NOT_EQ);
        compareTypeSet.add(TYPE.INSTANCE_OF);
        compareTypeSet.add(TYPE.IS_IN);
    }

    public BinaryOperationPandaInstruction(int pc, PandaOPCode opCode, byte[] param, PandaMethod pandaMethod,BinaryOperationPandaInstruction.TYPE type) {
        super(pc, opCode, param, pandaMethod);
        this.left = (PandaInstructionVReg)this.getParams()[1];
        this.type = type;
    }

    public boolean isCompareInstruction(){
        return compareTypeSet.contains(this.type);
    }

    @Override
    public String toString() {
        return baseToString() + left.toString() + " " + type.getOpName() + " acc";
    }
}
