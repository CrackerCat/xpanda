package jmp0.abc.res;

import com.google.common.primitives.UnsignedInteger;
import jmp0.abc.file.PandaFileUtils;
import lombok.Getter;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
@Getter
public final class PandaResourceIndexHeader {
    private static final int VERSION_MAX_LEN = 128;
    private final byte[] version;
    private final UnsignedInteger fileSize;
    private final UnsignedInteger limitKeyConfigSize;
    public final static int SIZE = 128 + 4 + 4;
    public PandaResourceIndexHeader(byte[] data){
        this.version = PandaFileUtils.readSubBytes(data,0,VERSION_MAX_LEN);
        this.fileSize = PandaFileUtils.bytes2UnsignedInteger(data,VERSION_MAX_LEN);
        this.limitKeyConfigSize = PandaFileUtils.bytes2UnsignedInteger(data,VERSION_MAX_LEN + 4);
    }
}
