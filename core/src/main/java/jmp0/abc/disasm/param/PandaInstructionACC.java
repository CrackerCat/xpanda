package jmp0.abc.disasm.param;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class PandaInstructionACC implements IPandaInstructionParam{
    public enum TYPE{
        OBJECT,
        STRING,
        BIGINT,
        NAN,
        HOLE,
        INFINITY,
        UNDEFINED,
        NULL,
        TRUE,
        FALSE,
        SYMBOL,
        THIS,
        GLOBAL,
        ARGUMENTS
    }
    private TYPE type = TYPE.OBJECT;
    private IPandaInstructionParam obj = null;
    public PandaInstructionACC(){}

    @Override
    public String toString() {
        if (this.type == TYPE.GLOBAL) return "globalThis";
        else if (obj == null) return "acc";
        else if (type == TYPE.OBJECT) return obj.toString();
        return type.name().toLowerCase();
    }
}