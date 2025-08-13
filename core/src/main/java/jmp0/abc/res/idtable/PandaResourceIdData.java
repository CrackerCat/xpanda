package jmp0.abc.res.idtable;

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
public final class PandaResourceIdData extends PandaResourceOffset {
    private final UnsignedInteger id;
    private final UnsignedInteger dataOffset;
    private final PandaResourceIdSet parent;

    private PandaResourceIdData(PandaResourceFile pandaResourceFile, UnsignedInteger offset,PandaResourceIdSet idSet) {
        super(pandaResourceFile, offset);
        this.parent = idSet;
        this.id = PandaFileUtils.bytes2UnsignedInteger(pandaResourceFile.getData(),offset.intValue());
        this.dataOffset = PandaFileUtils.bytes2UnsignedInteger(pandaResourceFile.getData(),offset.intValue() + 4);
    }

    public static PandaResourceIdData create(PandaResourceFile pandaResourceFile, UnsignedInteger offset,PandaResourceIdSet idSet) throws PandaParseException {
        PandaResourceOffset offset1 = pandaResourceFile.resolveOffset(offset);
        if (offset1 instanceof PandaResourceIdData) return (PandaResourceIdData) offset1;
        else{
            PandaResourceIdData resourceIdData = new PandaResourceIdData(pandaResourceFile,offset,idSet);
            pandaResourceFile.addOffset(offset,resourceIdData);
            return resourceIdData;
        }
    }


}
