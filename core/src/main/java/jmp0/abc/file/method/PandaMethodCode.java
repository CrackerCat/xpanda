package jmp0.abc.file.method;

import com.google.common.primitives.UnsignedInteger;
import jmp0.abc.codec.Leb128;
import jmp0.abc.file.IPandaCanOutput;
import jmp0.abc.file.PandaFile;
import jmp0.abc.file.PandaFileUtils;
import jmp0.abc.file.desc.Offset;
import lombok.Getter;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
@Getter
public final class PandaMethodCode extends Offset implements IPandaCanOutput {
    private final PandaMethod pandaMethod;
    private final UnsignedInteger numVregs;
    private final UnsignedInteger numArgs;
    private final UnsignedInteger codeSize;
    private final UnsignedInteger triesSize;
    private final byte[] instructions;
    private PandaMethodTryBlock[] pandaMethodTryBlocks = null;
    private final int SIZE;

    private PandaMethodCode(PandaFile pandaFile, UnsignedInteger offset,PandaMethod method) {
        super(pandaFile, offset);
        this.pandaMethod = method;
        UnsignedInteger index = UnsignedInteger.ZERO;
        Leb128 leb128 = pandaFile.getPandaFileLeb128();
        leb128.decode2UnsignedInteger(offset.plus(index).intValue());
        this.numVregs = leb128.getUnsignedInteger();
        index = index.plus(leb128.getSize());
        leb128.decode2UnsignedInteger(offset.plus(index).intValue());
        this.numArgs = leb128.getUnsignedInteger();
        index = index.plus(leb128.getSize());
        leb128.decode2UnsignedInteger(offset.plus(index).intValue());
        this.codeSize = leb128.getUnsignedInteger();
        index = index.plus(leb128.getSize());
        leb128.decode2UnsignedInteger(offset.plus(index).intValue());
        this.triesSize = leb128.getUnsignedInteger();
        index = index.plus(leb128.getSize());
        this.instructions = PandaFileUtils.readSubBytes(pandaFile.getData(),offset.plus(index).intValue(),this.codeSize.intValue());
        index = index.plus(this.codeSize);
        if (this.triesSize.intValue() > 0){
            this.pandaMethodTryBlocks = new PandaMethodTryBlock[this.triesSize.intValue()];
            for (int i = 0; i < this.triesSize.intValue(); i++) {
                this.pandaMethodTryBlocks[i] = PandaMethodTryBlock.create(pandaFile,offset.plus(index));
                index = index.plus(this.pandaMethodTryBlocks[i].getBlockSize());
            }
        }
        this.SIZE = index.intValue();
    }

    public static PandaMethodCode create(PandaFile pandaFile, UnsignedInteger offset,PandaMethod method){
        Offset offset1 = pandaFile.resolveOffset(offset);
        if (offset1 instanceof PandaMethodCode) return (PandaMethodCode) offset1;
        else{
            PandaMethodCode pandaMethodCode = new PandaMethodCode(pandaFile,offset,method);
            pandaFile.addOffset(offset,pandaMethodCode);
            return pandaMethodCode;
        }
    }


    public PandaMethod getParent(){
        return this.pandaMethod;
    }

    @Override
    public byte[] toByteArray() {
        ByteBuffer buffer = ByteBuffer.allocate(SIZE).order(ByteOrder.LITTLE_ENDIAN);
        buffer.put(Leb128.writeUnsignedLeb128(numVregs.intValue()))
                .put(Leb128.writeUnsignedLeb128(numArgs.intValue()))
                .put(Leb128.writeUnsignedLeb128(codeSize.intValue()))
                .put(Leb128.writeUnsignedLeb128(triesSize.intValue()))
                .put(instructions);
        if (pandaMethodTryBlocks != null) for (PandaMethodTryBlock pandaMethodTryBlock : pandaMethodTryBlocks) {
            buffer.put(pandaMethodTryBlock.toByteArray());
        }
        return buffer.array();
    }
}
