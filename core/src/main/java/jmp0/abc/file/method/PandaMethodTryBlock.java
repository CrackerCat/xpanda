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
public final class PandaMethodTryBlock extends Offset implements IPandaCanOutput {
    private final UnsignedInteger startPC;
    private final UnsignedInteger length;
    private final UnsignedInteger numCatches;
    private final UnsignedInteger blockSize;
    private final PandaMethodCatchBlock[] pandaMethodCatchBlocks;
    private PandaMethodTryBlock(PandaFile pandaFile, UnsignedInteger offset) {
        super(pandaFile, offset);
        UnsignedInteger index = UnsignedInteger.ZERO;
        Leb128 leb128 = pandaFile.getPandaFileLeb128();
        leb128.decode2UnsignedInteger(offset.plus(index).intValue());
        this.startPC = leb128.getUnsignedInteger();
        index = index.plus(leb128.getSize());
        leb128.decode2UnsignedInteger(offset.plus(index).intValue());
        this.length = leb128.getUnsignedInteger();
        index = index.plus(leb128.getSize());
        leb128.decode2UnsignedInteger(offset.plus(index).intValue());
        this.numCatches = leb128.getUnsignedInteger();
        index = index.plus(leb128.getSize());
        this.pandaMethodCatchBlocks = new PandaMethodCatchBlock[this.numCatches.intValue()];
        for (int i = 0; i < this.numCatches.intValue(); i++) {
            this.pandaMethodCatchBlocks[i] = PandaMethodCatchBlock.create(pandaFile,offset.plus(index));
            index = index.plus(pandaMethodCatchBlocks[i].getBlockSize());
        }
        this.blockSize = index;
    }

    public static PandaMethodTryBlock create(PandaFile pandaFile, UnsignedInteger offset){
        Offset offset1 = pandaFile.resolveOffset(offset);
        if (offset1 instanceof PandaMethodTryBlock) return (PandaMethodTryBlock) offset1;
        else{
            PandaMethodTryBlock pandaMethodTryBlock = new PandaMethodTryBlock(pandaFile,offset);
            pandaFile.addOffset(offset,pandaMethodTryBlock);
            return pandaMethodTryBlock;
        }
    }

    @Override
    public byte[] toByteArray() {
        ByteBuffer buffer = ByteBuffer.allocate(blockSize.intValue()).order(ByteOrder.LITTLE_ENDIAN);
        buffer.put(Leb128.writeUnsignedLeb128(startPC.intValue()))
                .put(Leb128.writeUnsignedLeb128(length.intValue()));
        if (pandaMethodCatchBlocks != null) for (PandaMethodCatchBlock pandaMethodCatchBlock : pandaMethodCatchBlocks) {
            buffer.put(pandaMethodCatchBlock.toByteArray());
        }
        return buffer.array();
    }
}
