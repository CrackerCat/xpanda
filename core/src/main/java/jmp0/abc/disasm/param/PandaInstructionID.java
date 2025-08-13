package jmp0.abc.disasm.param;

import jmp0.abc.PandaParseException;
import jmp0.abc.disasm.types.PandaOPCode;
import jmp0.abc.disasm.types.PandaOPCodeFlag;
import jmp0.abc.file.desc.IndexHeader;
import jmp0.abc.file.desc.Offset;
import jmp0.abc.file.desc.PandaString;
import jmp0.abc.file.field.PandaField;
import jmp0.abc.file.literal.PandaLiteralArray;
import jmp0.abc.file.method.PandaMethod;
import lombok.Getter;
import lombok.SneakyThrows;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
@Getter
public final class PandaInstructionID implements IPandaInstructionParam{
    @Getter
    public enum TYPE{
        TYPE(PandaOPCodeFlag.TYPE_ID),
        METHOD(PandaOPCodeFlag.METHOD_ID),
        STRING(PandaOPCodeFlag.STRING_ID),
        LITERALARRAY(PandaOPCodeFlag.LITERALARRAY_ID),
        FIELD(PandaOPCodeFlag.FIELD_ID);

        private final PandaOPCodeFlag flag;

        TYPE(PandaOPCodeFlag pandaOPCodeFlag) {
            this.flag = pandaOPCodeFlag;
        }
    }
    private TYPE type;
    private Offset obj;
    @SneakyThrows
    public PandaInstructionID(PandaOPCode code,int paramIndex, int id, PandaMethod pandaMethod){
        if (code == PandaOPCode.DEFINECLASSWITHBUFFER_IMM8_ID16_ID16_IMM16_V8 || code == PandaOPCode.DEFINECLASSWITHBUFFER_IMM16_ID16_ID16_IMM16_V8
                || code == PandaOPCode.CALLRUNTIME_DEFINESENDABLECLASS_PREF_IMM16_ID16_ID16_IMM16_V8){
            if (paramIndex == 1){
                this.type = TYPE.METHOD;
            }else if (paramIndex == 2){
                this.type = TYPE.LITERALARRAY;
            }else throw new PandaParseException("paramIndex not matched");
        }else {
            for (TYPE value : TYPE.values()) {
                if ((value.flag.getValue() & code.getFlags()) == value.flag.getValue()){
                    this.type = value;
                    break;
                }
            }
        }
        if (this.type == null) throw new PandaParseException("can not find id from the types enum");
        IndexHeader indexHeader = pandaMethod.getIndexHeader();
        switch (this.type){
            case TYPE:{
                //fixme
                break;
            }
            case FIELD:{
                Offset offset = indexHeader.getPandaFields()[id];
                this.obj = PandaField.create(pandaMethod.getPandaFile(),offset.getOffset());
                break;
            }
            case METHOD:{
                Offset offset = indexHeader.getPandaMethods()[id];
                this.obj = PandaMethod.create(pandaMethod.getPandaFile(),offset.getOffset());
                break;
            }
            case STRING:{
                Offset offset = indexHeader.getPandaMethods()[id];
                this.obj = PandaString.create(pandaMethod.getPandaFile(),offset.getOffset());
                break;
            }
            case LITERALARRAY:{
                Offset offset = indexHeader.getPandaMethods()[id];
                this.obj = PandaLiteralArray.create(pandaMethod.getPandaFile(),offset.getOffset());
                break;
            }
        }
    }

    @Override
    public String toString() {
        switch (this.type){
            case TYPE:{
                return "";
            }
            case FIELD:
            case METHOD:
            case STRING: {
                return this.obj.toString();
            }
            case LITERALARRAY:{
                return "array:"+ this.obj.toString();
            }
        }
        return "";
    }
}
