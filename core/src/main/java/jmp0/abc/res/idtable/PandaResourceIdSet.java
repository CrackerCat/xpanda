package jmp0.abc.res.idtable;

import com.google.common.primitives.UnsignedInteger;
import jmp0.abc.PandaParseException;
import jmp0.abc.file.PandaFileUtils;
import jmp0.abc.res.PandaResourceFile;
import jmp0.abc.res.PandaResourceOffset;
import lombok.Getter;

import java.util.Arrays;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
@Getter
public final class PandaResourceIdSet extends PandaResourceOffset {
    private final byte[] idTag = {'I','D','S','S'};
    private final UnsignedInteger count;
    private final PandaResourceIdData[] idData;
    private UnsignedInteger SIZE = UnsignedInteger.ZERO;

    private PandaResourceIdSet(PandaResourceFile pandaResourceFile, UnsignedInteger offset) throws PandaParseException {
        super(pandaResourceFile, offset);
        byte[] data = pandaResourceFile.getData();
        byte[] tag = PandaFileUtils.readSubBytes(data,offset.intValue(), idTag.length);
        SIZE = SIZE.plus(UnsignedInteger.valueOf(4));
        if (!Arrays.equals(idTag,tag)) throw new PandaParseException("PandaResourceLimitKeyConfig idTag must be 'IDSS'");
        this.count = PandaFileUtils.bytes2UnsignedInteger(data,offset.plus(SIZE).intValue());
        SIZE = SIZE.plus(UnsignedInteger.valueOf(4));
        if (this.count.intValue() == 0){
            idData = new PandaResourceIdData[0];
        }else {
            idData = new PandaResourceIdData[this.count.intValue()];
            for (int i = 0; i < this.count.intValue(); i++) {
                idData[i] = PandaResourceIdData.create(pandaResourceFile,offset.plus(SIZE),this);
                SIZE = SIZE.plus(UnsignedInteger.valueOf(8));
            }
        }
    }

    public static PandaResourceIdSet create(PandaResourceFile pandaResourceFile, UnsignedInteger offset) throws PandaParseException {
        PandaResourceOffset offset1 = pandaResourceFile.resolveOffset(offset);
        if (offset1 instanceof PandaResourceIdSet) return (PandaResourceIdSet) offset1;
        else{
            PandaResourceIdSet resourceIdSet = new PandaResourceIdSet(pandaResourceFile,offset);
            pandaResourceFile.addOffset(offset,resourceIdSet);
            return resourceIdSet;
        }
    }
}
