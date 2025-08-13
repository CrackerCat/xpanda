package jmp0.abc.file.anno;

import com.google.common.primitives.UnsignedInteger;
import jmp0.abc.codec.Leb128;
import jmp0.abc.codec.MUTF8;
import jmp0.abc.file.IPandaCanOutput;
import jmp0.abc.file.PandaFile;
import jmp0.abc.file.PandaFileUtils;
import jmp0.abc.file.desc.Offset;
import jmp0.abc.file.desc.PandaString;
import lombok.Getter;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
@Getter
public class PandaAnnotationElement extends Offset implements IPandaCanOutput {
    public static final int SIZE = 8;
    private final PandaString name;
    private final Offset pandaValue;
    public PandaAnnotationElement(PandaFile pandaFile, UnsignedInteger offset) {
        super(pandaFile, offset);
        UnsignedInteger index = UnsignedInteger.ZERO;
        Offset nameOffset = PandaFileUtils.readOffset(getPandaFile(),offset.plus(index).intValue());
        index = index.plus(UnsignedInteger.valueOf(4));
        this.pandaValue = new Offset(getPandaFile(),offset.plus(index));
        if (!nameOffset.isValid()){
            this.name = new PandaString(pandaFile);
            return;
        }
        this.name = PandaString.create(pandaFile,nameOffset.getOffset());
    }

    @Override
    public byte[] toByteArray() {
        ByteBuffer buffer = ByteBuffer.allocate(SIZE).order(ByteOrder.LITTLE_ENDIAN);
        buffer.putInt(name.getOffset().intValue());
        byte[] bs = PandaFileUtils.readSubBytes(getPandaFile().getData(),this.pandaValue.getOffset().intValue(),4);
        buffer.put(bs);
        return buffer.array();
    }
}
