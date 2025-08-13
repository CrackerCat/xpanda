package jmp0.abc.file.debug;

import com.google.common.primitives.UnsignedInteger;
import jmp0.abc.codec.Leb128;
import jmp0.abc.file.IPandaCanOutput;
import jmp0.abc.file.IResolvable;
import jmp0.abc.file.PandaFile;
import jmp0.abc.file.PandaFileUtils;
import jmp0.abc.file.desc.Offset;
import jmp0.abc.file.method.PandaMethod;
import lombok.Getter;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.LinkedList;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
@Getter
public final class PandaMethodDebugInfo extends Offset implements IResolvable, IPandaCanOutput {
    private final PandaMethod pandaMethod;
    private final UnsignedInteger lineStart;
    private final UnsignedInteger numParameters;
    private final LinkedList<Offset> parameters = new LinkedList<>();
    private UnsignedInteger constantPoolSize;
    private byte[] constantPoolData;
    private UnsignedInteger lnpIndex;
    private PandaLineNumberProgram pandaLineNumberProgram;
    private final int SIZE;

    private PandaMethodDebugInfo(PandaFile pandaFile, UnsignedInteger offset,PandaMethod method) {
        super(pandaFile, offset);
        this.pandaMethod = method;
        UnsignedInteger index = UnsignedInteger.ZERO;
        Leb128 leb128 = pandaFile.getPandaFileLeb128();
        leb128.decode2UnsignedInteger(offset.plus(index).intValue());
        this.lineStart = leb128.getUnsignedInteger();
        index = index.plus(leb128.getSize());

        leb128.decode2UnsignedInteger(offset.plus(index).intValue());
        this.numParameters = leb128.getUnsignedInteger();
        index = index.plus(leb128.getSize());

        for (int i = 0; i < numParameters.intValue(); i++) {
            leb128.decode2UnsignedInteger(offset.plus(index).intValue());
            parameters.add(new Offset(getPandaFile(),leb128.getUnsignedInteger()));
            UnsignedInteger size = leb128.getSize();
            index = index.plus(size);
        }

        leb128.decode2UnsignedInteger(offset.plus(index).intValue());
        this.constantPoolSize = leb128.getUnsignedInteger();
        index = index.plus(leb128.getSize());

        constantPoolData = PandaFileUtils.readSubBytes(getPandaFile().getData(),offset.plus(index).intValue(),constantPoolSize.intValue());
        index = index.plus(constantPoolSize);

        leb128.decode2UnsignedInteger(offset.plus(index).intValue());
        lnpIndex = leb128.getUnsignedInteger();
        index = index.plus(leb128.getSize());

        this.SIZE = index.intValue();
    }

    public static PandaMethodDebugInfo create(PandaFile pandaFile, UnsignedInteger offset,PandaMethod method){
        Offset offset1 = pandaFile.resolveOffset(offset);
        if (offset1 instanceof PandaMethodDebugInfo) return (PandaMethodDebugInfo) offset1;
        else{
            PandaMethodDebugInfo pandaMethodDebugInfo = new PandaMethodDebugInfo(pandaFile,offset,method);
            pandaFile.addOffset(offset,pandaMethodDebugInfo);
            return pandaMethodDebugInfo;
        }
    }

    public PandaMethod getParent(){
        return this.pandaMethod;
    }

    @Override
    public void resolve() {
        if (lnpIndex != null){
            Offset line = getPandaFile().getLnpOffsets()[lnpIndex.intValue()];
            this.pandaLineNumberProgram = PandaLineNumberProgram.create(getPandaFile(),line.getOffset(),constantPoolData);
        }
    }

    @Override
    public byte[] toByteArray() {
        ByteBuffer buffer = ByteBuffer.allocate(SIZE).order(ByteOrder.LITTLE_ENDIAN);
        buffer.put(Leb128.writeUnsignedLeb128(lineStart.intValue()))
                .put(Leb128.writeUnsignedLeb128(numParameters.intValue()));
        for (Offset parameter : parameters) {
            buffer.put(Leb128.writeUnsignedLeb128(parameter.getOffset().intValue()));
        }
        buffer.put(Leb128.writeUnsignedLeb128(constantPoolSize.intValue()))
                .put(constantPoolData)
                .put(Leb128.writeUnsignedLeb128(lnpIndex.intValue()));
        return buffer.array();
    }
}
