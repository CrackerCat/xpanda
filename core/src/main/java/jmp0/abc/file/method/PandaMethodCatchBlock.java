package jmp0.abc.file.method;

import com.google.common.primitives.UnsignedInteger;
import jmp0.abc.codec.Leb128;
import jmp0.abc.file.IPandaCanOutput;
import jmp0.abc.file.PandaFile;
import jmp0.abc.file.desc.Offset;
import lombok.Getter;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */

@Getter
public final class PandaMethodCatchBlock extends Offset implements IPandaCanOutput {
    private final UnsignedInteger typeIdx;
    private final UnsignedInteger handlerPC;
    private final UnsignedInteger codeSize;
    private final UnsignedInteger blockSize;

    private PandaMethodCatchBlock(PandaFile pandaFile, UnsignedInteger offset) {
        super(pandaFile, offset);
        UnsignedInteger index = UnsignedInteger.ZERO;
        Leb128 leb128 = pandaFile.getPandaFileLeb128();
        leb128.decode2UnsignedInteger(offset.plus(index).intValue());
        this.typeIdx = leb128.getUnsignedInteger();
        index = index.plus(leb128.getSize());
        leb128.decode2UnsignedInteger(offset.plus(index).intValue());
        this.handlerPC = leb128.getUnsignedInteger();
        index = index.plus(leb128.getSize());
        leb128.decode2UnsignedInteger(offset.plus(index).intValue());
        this.codeSize = leb128.getUnsignedInteger();
        index = index.plus(leb128.getSize());
        blockSize = index;
    }

    public static PandaMethodCatchBlock create(PandaFile pandaFile, UnsignedInteger offset){
        Offset offset1 = pandaFile.resolveOffset(offset);
        if (offset1 instanceof PandaMethodCatchBlock) return (PandaMethodCatchBlock) offset1;
        else{
            PandaMethodCatchBlock pandaMethodCatchBlock = new PandaMethodCatchBlock(pandaFile,offset);
            pandaFile.addOffset(offset,pandaMethodCatchBlock);
            return pandaMethodCatchBlock;
        }
    }

    @Override
    public byte[] toByteArray() {
        ByteBuffer buffer = ByteBuffer.allocate(blockSize.intValue()).order(ByteOrder.LITTLE_ENDIAN);
        buffer.put(Leb128.writeUnsignedLeb128(typeIdx.intValue()))
                .put(Leb128.writeUnsignedLeb128(handlerPC.intValue()))
                .put(Leb128.writeUnsignedLeb128(codeSize.intValue()));
        return buffer.array();
    }
}
