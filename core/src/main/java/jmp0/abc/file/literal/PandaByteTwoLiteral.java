package jmp0.abc.file.literal;

import com.google.common.primitives.UnsignedInteger;
import jmp0.abc.file.PandaFile;
import jmp0.abc.file.desc.Offset;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */

public final class PandaByteTwoLiteral extends PandaLiteral{
    private PandaByteTwoLiteral(PandaFile pandaFile, UnsignedInteger offset,LiteralTag tag) {
        super(pandaFile, offset,2,tag);
    }

    public static PandaByteTwoLiteral create(PandaFile pandaFile, UnsignedInteger offset,LiteralTag tag){
        Offset offset1 = pandaFile.resolveOffset(offset);
        if (offset1 instanceof PandaByteTwoLiteral) return (PandaByteTwoLiteral) offset1;
        else{
            PandaByteTwoLiteral pandaByteTwoLiteral = new PandaByteTwoLiteral(pandaFile,offset,tag);
            pandaFile.addOffset(offset,pandaByteTwoLiteral);
            return pandaByteTwoLiteral;
        }
    }

    @Override
    public String toString() {
        switch (tag){
            case METHODAFFILIATE:
                return String.valueOf(getValue());
            default:
                return super.toString();
        }
    }
}
