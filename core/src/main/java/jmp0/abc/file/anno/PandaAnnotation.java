package jmp0.abc.file.anno;

import com.google.common.primitives.UnsignedInteger;
import jmp0.abc.file.IPandaCanOutput;
import jmp0.abc.file.IResolvable;
import jmp0.abc.file.PandaFile;
import jmp0.abc.file.PandaFileUtils;
import jmp0.abc.file.clazz.PandaClass;
import jmp0.abc.file.desc.IndexHeader;
import jmp0.abc.file.desc.Offset;
import lombok.Getter;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
@Getter
public final class PandaAnnotation extends Offset implements IResolvable, IPandaCanOutput {
    private final short classIdx;
    private PandaClass pandaClass = null;
    private final short count;
    private final PandaAnnotationElement[] pandaAnnotationElements;
    private final char[] element_types;
    private final int SIZE;

    private PandaAnnotation(PandaFile pandaFile, UnsignedInteger offset) {
        super(pandaFile, offset);
        UnsignedInteger index = UnsignedInteger.ZERO;
        this.classIdx = PandaFileUtils.bytes2Uint16(pandaFile.getData(),offset.plus(index).intValue());
        index = index.plus(UnsignedInteger.valueOf(2));
        this.count = PandaFileUtils.bytes2Uint16(pandaFile.getData(),offset.plus(index).intValue());
        index = index.plus(UnsignedInteger.valueOf(2));
        this.pandaAnnotationElements = new PandaAnnotationElement[this.count];
        for (int i = 0; i < count; i++) {
            this.pandaAnnotationElements[i] = new PandaAnnotationElement(getPandaFile(),offset.plus(index));
            index = index.plus(UnsignedInteger.valueOf(PandaAnnotationElement.SIZE));
        }
        this.element_types = new char[this.count];
        for (int i = 0; i < count; i++) {
            this.element_types[i] = (char) pandaFile.getData()[offset.plus(index).intValue()];
            index = index.plus(UnsignedInteger.ONE);
        }
        SIZE = index.intValue();
    }

    public static PandaAnnotation create(PandaFile pandaFile, UnsignedInteger offset){
        Offset offset1 = pandaFile.resolveOffset(offset);
        if (offset1 instanceof PandaAnnotation) return (PandaAnnotation) offset1;
        else{
            PandaAnnotation pandaAnnotation = new PandaAnnotation(pandaFile,offset);
            pandaFile.addOffset(offset,pandaAnnotation);
            return pandaAnnotation;
        }
    }

    @Override
    public void resolve() {
        IndexHeader indexHeader = this.getPandaFile().resolveIndexHeaderByOffset(this);
        if (indexHeader != null){
            if (this.classIdx < indexHeader.getClassIdxSize().intValue()) this.pandaClass = indexHeader.getPandaClasses()[this.classIdx];
        }
    }

    @Override
    public byte[] toByteArray() {
        ByteBuffer buffer = ByteBuffer.allocate(SIZE).order(ByteOrder.LITTLE_ENDIAN);
        buffer.putShort(classIdx);
        buffer.putShort(count);
        for (PandaAnnotationElement pandaAnnotationElement : pandaAnnotationElements) {
            buffer.put(pandaAnnotationElement.toByteArray());
        }
        for (char elementType : element_types) {
            buffer.put((byte) elementType);
        }
        return buffer.array();
    }
}
