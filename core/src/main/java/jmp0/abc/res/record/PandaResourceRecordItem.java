package jmp0.abc.res.record;

import com.google.common.primitives.UnsignedInteger;
import jmp0.abc.PandaParseException;
import jmp0.abc.file.PandaFileUtils;
import jmp0.abc.res.PandaResourceFile;
import jmp0.abc.res.PandaResourceOffset;
import lombok.Getter;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
@Getter
public final class PandaResourceRecordItem extends PandaResourceOffset {
    private final UnsignedInteger size;
    private final UnsignedInteger resType;
    private final UnsignedInteger id;
    public UnsignedInteger SIZE = UnsignedInteger.valueOf(12);

    private PandaResourceRecordItem(PandaResourceFile pandaResourceFile, UnsignedInteger offset) {
        super(pandaResourceFile, offset);
        this.size = PandaFileUtils.bytes2UnsignedInteger(pandaResourceFile.getData(),offset.intValue());
        this.resType = PandaFileUtils.bytes2UnsignedInteger(pandaResourceFile.getData(),offset.intValue() + 4);
        this.id = PandaFileUtils.bytes2UnsignedInteger(pandaResourceFile.getData(),offset.intValue() + 8);
    }

    public static PandaResourceRecordItem create(PandaResourceFile pandaResourceFile, UnsignedInteger offset) throws PandaParseException {
        PandaResourceOffset offset1 = pandaResourceFile.resolveOffset(offset);
        if (offset1 instanceof PandaResourceRecordItem) return (PandaResourceRecordItem) offset1;
        else{
            PandaResourceRecordItem recordItem = new PandaResourceRecordItem(pandaResourceFile,offset);
            pandaResourceFile.addOffset(offset,recordItem);
            return recordItem;
        }
    }
}
