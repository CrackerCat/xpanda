package jmp0.abc.file.literal;

import com.google.common.primitives.UnsignedInteger;
import jmp0.abc.file.PandaFile;
import jmp0.abc.file.desc.Offset;
import jmp0.abc.file.field.PandaField;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class PandaByteFourLiteral extends PandaLiteral{
    private PandaByteFourLiteral(PandaFile pandaFile, UnsignedInteger offset,LiteralTag tag) {
        super(pandaFile, offset,4,tag);
    }

    public static PandaByteFourLiteral create(PandaFile pandaFile, UnsignedInteger offset,LiteralTag tag){
        Offset offset1 = pandaFile.resolveOffset(offset);
        if (offset1 instanceof PandaByteFourLiteral) return (PandaByteFourLiteral) offset1;
        else{
            PandaByteFourLiteral pandaByteFourLiteral = new PandaByteFourLiteral(pandaFile,offset,tag);
            pandaFile.addOffset(offset,pandaByteFourLiteral);
            return pandaByteFourLiteral;
        }
    }

    @Override
    public String toString() {
        switch (this.tag){
            case INTEGER:
            case FLOAT:
                return String.valueOf(getValue());
            default:
                return super.toString();
        }
    }
}
