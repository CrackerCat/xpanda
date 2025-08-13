package jmp0.abc.file.literal;

import com.google.common.primitives.UnsignedInteger;
import jmp0.abc.file.PandaFile;
import jmp0.abc.file.desc.Offset;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class PandaByteOneLiteral extends PandaLiteral{
    private PandaByteOneLiteral(PandaFile pandaFile, UnsignedInteger offset,LiteralTag tag) {
        super(pandaFile, offset,1,tag);
    }

    public static PandaByteOneLiteral create(PandaFile pandaFile, UnsignedInteger offset,LiteralTag tag){
        Offset offset1 = pandaFile.resolveOffset(offset);
        if (offset1 instanceof PandaByteOneLiteral) return (PandaByteOneLiteral) offset1;
        else{
            PandaByteOneLiteral pandaByteOneLiteral = new PandaByteOneLiteral(pandaFile,offset,tag);
            pandaFile.addOffset(offset,pandaByteOneLiteral);
            return pandaByteOneLiteral;
        }
    }

    @Override
    public String toString() {
        switch (this.tag){
            case NULLVALU:
                return "null";
            case BOOL:
                return String.valueOf(valueByteArr[0] == 1);
            default:
                return super.toString();
        }
    }
}
