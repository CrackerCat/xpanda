package jmp0.abc.file.literal;

import com.google.common.primitives.UnsignedInteger;
import jmp0.abc.file.PandaFile;
import jmp0.abc.file.desc.Offset;
import jmp0.abc.file.field.PandaField;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class PandaByteEightLiteral extends PandaLiteral {
    private PandaByteEightLiteral(PandaFile pandaFile, UnsignedInteger offset,LiteralTag tag) {
        super(pandaFile, offset, 8,tag);
    }

    public static PandaByteEightLiteral create(PandaFile pandaFile, UnsignedInteger offset,LiteralTag tag){
        Offset offset1 = pandaFile.resolveOffset(offset);
        if (offset1 instanceof PandaByteEightLiteral) return (PandaByteEightLiteral) offset1;
        else{
            PandaByteEightLiteral pandaByteEightLiteral = new PandaByteEightLiteral(pandaFile,offset,tag);
            pandaFile.addOffset(offset,pandaByteEightLiteral);
            return pandaByteEightLiteral;
        }
    }

    @Override
    public String toString() {
        switch (tag){
            case DOUBLE:
                return String.valueOf(getValue());
            default:
                return super.toString();
        }
    }
}
