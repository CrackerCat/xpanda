package jmp0.abc.res;

import com.google.common.primitives.UnsignedInteger;
import lombok.Getter;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
@Getter
public abstract class PandaResourceOffset {
    protected final UnsignedInteger offset;
    private final PandaResourceFile pandaResourceFile;

    public PandaResourceOffset(PandaResourceFile pandaResourceFile, UnsignedInteger offset){
        this.offset = offset;
        this.pandaResourceFile = pandaResourceFile;
    }
}
