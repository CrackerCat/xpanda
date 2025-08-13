package jmp0.abc.file.field;

import com.google.common.primitives.UnsignedInteger;
import jmp0.abc.PandaParseException;
import jmp0.abc.codec.Leb128;
import jmp0.abc.file.IPandaCanOutput;
import jmp0.abc.file.IResolvable;
import jmp0.abc.file.PandaFile;
import jmp0.abc.file.PandaFileUtils;
import jmp0.abc.file.clazz.PandaClass;
import jmp0.abc.file.desc.IndexHeader;
import jmp0.abc.file.desc.Offset;
import jmp0.abc.file.desc.PandaString;
import jmp0.abc.file.type.PandaRawType;
import jmp0.abc.file.type.PandaType;
import lombok.Getter;
import lombok.SneakyThrows;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.LinkedList;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
@Getter
final public class PandaField extends Offset implements IResolvable, IPandaCanOutput {
    private final short classIdx;
    private final short typeIdx;
    private PandaClass pandaClass;
    private PandaType pandaType;
    private final PandaString name;
    private final boolean fieldExternal;
    private final UnsignedInteger size;
    private final UnsignedInteger accessFlag;
    private int intValue = 0;
    private Offset pandaRawValue;
    private PandaFieldValue pandaFieldValue = null;
    private LinkedList<Byte> tagList = new LinkedList<>();
    @SneakyThrows
    private PandaField(PandaFile pandaFile, UnsignedInteger offset) {
        super(pandaFile, offset);
        UnsignedInteger index = UnsignedInteger.ZERO;
        this.classIdx = PandaFileUtils.bytes2Uint16(pandaFile.getData(),offset.intValue());
        index = index.plus(UnsignedInteger.valueOf(2));
        this.typeIdx = PandaFileUtils.bytes2Uint16(pandaFile.getData(),offset.plus(index).intValue());
        index = index.plus(UnsignedInteger.valueOf(2));
        this.name = PandaString.create(pandaFile,PandaFileUtils.readOffset(pandaFile,offset.plus(index).intValue()).getOffset());
        Leb128 leb128 = pandaFile.getPandaFileLeb128();
        this.fieldExternal = isExternal();
        if (this.fieldExternal){
            size = index;
            this.accessFlag = UnsignedInteger.ZERO;
            return;
        }
        index = index.plus(UnsignedInteger.valueOf(4));
        leb128.decode2UnsignedInteger(offset.plus(index).intValue());
        this.accessFlag = leb128.getUnsignedInteger();
        index = index.plus(leb128.getSize());
        byte tag = pandaFile.getData()[this.offset.plus(index).intValue()];
        this.tagList.add(tag);
        while (tag != PandaFieldTag.NOTHING.getValue()){
            index = index.plus(UnsignedInteger.ONE);
            if (tag == PandaFieldTag.INT_VALUE.getValue()){
                leb128.decode2Integer(offset.plus(index).intValue());
                this.intValue = leb128.getSignedResult();
                index = index.plus(leb128.getSize());
            } else if (tag == PandaFieldTag.VALUE.getValue()) {
                this.pandaRawValue = new Offset(getPandaFile(),PandaFileUtils.bytes2UnsignedInteger(getPandaFile().getData(),offset.plus(index).intValue()));
                index = index.plus(UnsignedInteger.valueOf(4));
            } else if (tag == PandaFieldTag.RUNTIME_ANNOTATION.getValue()) {
                throw new PandaParseException("PandaFieldTag.RUNTIME_ANNOTATION");
            } else if (tag == PandaFieldTag.ANNOTATION.getValue()) {
                throw new PandaParseException("PandaFieldTag.ANNOTATION");
            } else if (tag == PandaFieldTag.RUNTIME_TYPE_ANNOTATION.getValue()) {
                throw new PandaParseException("PandaFieldTag.RUNTIME_TYPE_ANNOTATION");
            }else if (tag == PandaFieldTag.TYPE_ANNOTATION.getValue()) {
                throw new PandaParseException("PandaFieldTag.TYPE_ANNOTATION");
            }
            tag = pandaFile.getData()[this.offset.plus(index).intValue()];
            this.tagList.add(tag);
        }
        this.size = index.plus(UnsignedInteger.ONE);
    }

    @Override
    public void resolve() {
        IndexHeader indexHeader = this.getPandaFile().resolveIndexHeaderByOffset(this);
        if (indexHeader != null){
            if (this.classIdx < indexHeader.getClassIdxSize().intValue()) this.pandaClass = indexHeader.getPandaClasses()[this.classIdx];
            if (this.typeIdx < indexHeader.getClassIdxSize().intValue()) this.pandaType = new PandaType(indexHeader.getPandaClasses()[this.typeIdx],true);
        }
        if (this.pandaType != null && this.pandaRawValue != null){
            this.pandaFieldValue = PandaFieldValue.create(getPandaFile(),pandaRawValue.getOffset(),pandaType.getRawType());
            if (this.pandaType.isPrimitive() && this.pandaType.getRawType() == PandaRawType.U32){
                if (this.name.equals("moduleRecordIdx")){
                    this.getParent().getParent().addModuleLiteral(this.pandaRawValue.getOffset());
                }
            }
            this.pandaRawValue = null;
        }
    }

    public static PandaField create(PandaFile pandaFile, UnsignedInteger offset){
        Offset offset1 = pandaFile.resolveOffset(offset);
        if (offset1 instanceof PandaField) return (PandaField) offset1;
        else{
            PandaField pandaField = new PandaField(pandaFile,offset);
            pandaFile.addOffset(offset,pandaField);
            return pandaField;
        }
    }

    public PandaClass getParent(){
        return this.pandaClass;
    }

    @Override
    public String toString() {
        return "field:"+getName();
    }

    @Override
    public byte[] toByteArray() {
        ByteBuffer buffer = ByteBuffer.allocate(size.intValue()).order(ByteOrder.LITTLE_ENDIAN);
        buffer.putShort(classIdx)
                .putShort(typeIdx)
                .putInt(name.getOffset().intValue())
                .put(Leb128.writeUnsignedLeb128(accessFlag.intValue()));
        for (Byte b : tagList) {
            if (b == PandaFieldTag.INT_VALUE.getValue()){
                buffer.put(b);
                buffer.put(Leb128.writeSignedLeb128(this.intValue));
            }else if (b == PandaFieldTag.VALUE.getValue()){
                buffer.put(b);
                if (pandaRawValue == null){
                    buffer.putInt(pandaFieldValue.getOffset().intValue());
                }else buffer.putInt(pandaRawValue.getOffset().intValue());
            } else if (b == PandaFieldTag.NOTHING.getValue()) {
                buffer.put(b);
            }
        }
        return buffer.array();
    }
}
