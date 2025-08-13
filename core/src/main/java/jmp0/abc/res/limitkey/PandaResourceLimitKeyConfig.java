package jmp0.abc.res.limitkey;

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
public final class PandaResourceLimitKeyConfig extends PandaResourceOffset {
    private final byte[] keyTag = {'K','E','Y','S'};
    private final UnsignedInteger keyOffset;
    private final UnsignedInteger keyCount;
    private final PandaResourceLimitKeyParam[] keyParams;
    private UnsignedInteger SIZE = UnsignedInteger.ZERO;

    private PandaResourceLimitKeyConfig(PandaResourceFile pandaResourceFile, UnsignedInteger keyOffset) throws PandaParseException {
        super(pandaResourceFile, keyOffset);
        byte[] data = pandaResourceFile.getData();
        byte[] tag = PandaFileUtils.readSubBytes(data, keyOffset.intValue(), keyTag.length);
        SIZE = SIZE.plus(UnsignedInteger.valueOf(4));
        if (!Arrays.equals(keyTag,tag)) throw new PandaParseException("PandaResourceLimitKeyConfig keyTag must be 'KEYS'");
        this.keyOffset = PandaFileUtils.bytes2UnsignedInteger(data, keyOffset.intValue() + SIZE.intValue());
        SIZE = SIZE.plus(UnsignedInteger.valueOf(4));
        this.keyCount = PandaFileUtils.bytes2UnsignedInteger(data, keyOffset.intValue() + SIZE.intValue());
        SIZE = SIZE.plus(UnsignedInteger.valueOf(4));
        if (keyCount.intValue() == 0){
            keyParams = new PandaResourceLimitKeyParam[0];
        }else {
            keyParams = new PandaResourceLimitKeyParam[keyCount.intValue()];
            for (int i = 0; i < keyParams.length; i++) {
                keyParams[i] = PandaResourceLimitKeyParam.create(pandaResourceFile, keyOffset.plus(SIZE));
                SIZE = SIZE.plus(UnsignedInteger.valueOf(8));
            }
        }
    }

    public static PandaResourceLimitKeyConfig create(PandaResourceFile pandaResourceFile, UnsignedInteger offset) throws PandaParseException {
        PandaResourceOffset offset1 = pandaResourceFile.resolveOffset(offset);
        if (offset1 instanceof PandaResourceLimitKeyConfig) return (PandaResourceLimitKeyConfig) offset1;
        else{
            PandaResourceLimitKeyConfig limitKeyConfig = new PandaResourceLimitKeyConfig(pandaResourceFile,offset);
            pandaResourceFile.addOffset(offset,limitKeyConfig);
            return limitKeyConfig;
        }
    }
}
