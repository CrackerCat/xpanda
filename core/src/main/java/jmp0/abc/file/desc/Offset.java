package jmp0.abc.file.desc;

import com.google.common.primitives.UnsignedInteger;
import jmp0.abc.file.IPandaCanOutput;
import jmp0.abc.file.PandaFile;
import lombok.Getter;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */

@Getter
public class Offset{
    protected final UnsignedInteger offset;
    private final PandaFile pandaFile;

    public Offset(PandaFile pandaFile, UnsignedInteger offset){
        this.offset = offset;
        this.pandaFile = pandaFile;
    }

    public static Offset nullOffset(){
        return new Offset(null,null);
    }

    public boolean isValid(){
        return this.offset.compareTo(pandaFile.getHeader().getFileSize()) <= 0
                && this.offset.compareTo(UnsignedInteger.valueOf(Header.SIZE)) > 0;
    }

    public boolean isExternal(){
        UnsignedInteger begin = this.pandaFile.getHeader().getForeignOff();
        UnsignedInteger end = begin.plus(this.pandaFile.getHeader().getForeignSize());
        return this.offset.compareTo(begin) >= 0 && this.offset.compareTo(end) < 0;
    }
}
