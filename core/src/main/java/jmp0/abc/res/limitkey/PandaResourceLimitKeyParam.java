package jmp0.abc.res.limitkey;

import com.google.common.primitives.UnsignedInteger;
import jmp0.abc.PandaParseException;
import jmp0.abc.file.PandaFileUtils;
import jmp0.abc.res.PandaResourceFile;
import jmp0.abc.res.PandaResourceOffset;
import jmp0.abc.res.types.PandaResourceKeyType;
import lombok.Getter;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
@Getter
public final class PandaResourceLimitKeyParam extends PandaResourceOffset {
    private final PandaResourceKeyType keyType;
    private final UnsignedInteger value;


    private PandaResourceLimitKeyParam(PandaResourceFile pandaResourceFile, UnsignedInteger offset) {
        super(pandaResourceFile, offset);
        this.keyType = PandaResourceKeyType.resolve(PandaFileUtils.bytes2UnsignedInteger(pandaResourceFile.getData(),offset.intValue()).intValue());
        this.value = PandaFileUtils.bytes2UnsignedInteger(pandaResourceFile.getData(),offset.intValue() + 4);
    }

    public static PandaResourceLimitKeyParam create(PandaResourceFile pandaResourceFile, UnsignedInteger offset) throws PandaParseException {
        PandaResourceOffset offset1 = pandaResourceFile.resolveOffset(offset);
        if (offset1 instanceof PandaResourceLimitKeyParam) return (PandaResourceLimitKeyParam) offset1;
        else{
            PandaResourceLimitKeyParam resourceLimitKey = new PandaResourceLimitKeyParam(pandaResourceFile,offset);
            pandaResourceFile.addOffset(offset,resourceLimitKey);
            return resourceLimitKey;
        }
    }
}
