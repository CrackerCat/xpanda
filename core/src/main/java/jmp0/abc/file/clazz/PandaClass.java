package jmp0.abc.file.clazz;

import com.google.common.primitives.UnsignedInteger;
import jmp0.abc.PandaParseException;
import jmp0.abc.disasm.block.PandaIRCFG;
import jmp0.abc.file.IPandaCanOutput;
import jmp0.abc.file.IResolvable;
import jmp0.abc.file.PandaFile;
import jmp0.abc.codec.Leb128;
import jmp0.abc.file.PandaFileUtils;
import jmp0.abc.file.desc.IndexHeader;
import jmp0.abc.file.desc.Offset;
import jmp0.abc.file.desc.PandaString;
import jmp0.abc.file.field.PandaField;
import jmp0.abc.file.method.PandaMethod;
import lombok.Getter;
import lombok.SneakyThrows;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
@Getter
public final class PandaClass extends Offset implements IResolvable, IPandaCanOutput {
    private final boolean classExternal;
    private final PandaString name;
    private final Offset superClassOff;
    private final UnsignedInteger accessFlags;
    private final UnsignedInteger numFields;
    private final UnsignedInteger numMethods;
    private final IndexHeader indexHeader;
    private UnsignedInteger index = UnsignedInteger.ZERO;
    private UnsignedInteger numIfaces = UnsignedInteger.ZERO;
    private Offset ifacesOffsets = Offset.nullOffset();
    private byte sourceLang = 0;
    private PandaField[] pandaFields;
    private HashMap<String,PandaMethod> pandaMethods = new LinkedHashMap<>();
    private PandaClassModuleData pandaClassModuleData;
    //bugfix multi index header same class name.
    private boolean resolved = false;

    private PandaClass(PandaFile pandaFile, UnsignedInteger offset,IndexHeader indexHeader){
        super(pandaFile, offset);
        this.indexHeader = indexHeader;
        this.classExternal = isExternal();
        if (!this.isValid()){
            this.name = PandaString.create(pandaFile,this.offset);
            index = this.name.getSize();
            this.superClassOff = Offset.nullOffset();
            this.accessFlags = UnsignedInteger.ZERO;
            this.numFields = UnsignedInteger.ZERO;
            this.numMethods = UnsignedInteger.ZERO;
            return;
        }else if (classExternal){
            this.name = PandaString.create(pandaFile,this.offset);
            index = this.name.getSize();
            this.superClassOff = Offset.nullOffset();
            this.accessFlags = UnsignedInteger.ZERO;
            this.numFields = UnsignedInteger.ZERO;
            this.numMethods = UnsignedInteger.ZERO;
            return;
        }
        Leb128 leb128 = pandaFile.getPandaFileLeb128();
        this.name = PandaString.create(pandaFile,this.offset);
        index = this.name.getSize();
        this.superClassOff = new Offset(pandaFile,PandaFileUtils.bytes2UnsignedInteger(pandaFile.getData(),this.offset.plus(index).intValue()));
        index = index.plus(UnsignedInteger.valueOf(4));
        leb128.decode2UnsignedInteger(this.offset.plus(index).intValue());
        this.accessFlags = leb128.getUnsignedInteger();
        index = index.plus(leb128.getSize());
        leb128.decode2UnsignedInteger(this.offset.plus(index).intValue());
        this.numFields = leb128.getUnsignedInteger();
        index = index.plus(leb128.getSize());
        leb128.decode2UnsignedInteger(this.offset.plus(index).intValue());
        this.numMethods = leb128.getUnsignedInteger();
        index = index.plus(leb128.getSize());
    }
    @SneakyThrows
    private void resolveClasses(){
        // FIXME: class tag parse
        Leb128 leb128 = getPandaFile().getPandaFileLeb128();
        byte tag = getPandaFile().getData()[this.offset.plus(index).intValue()];
        while (tag != PandaClassTag.NOTHING.getValue()){
            index = index.plus(UnsignedInteger.ONE);
            // PandaClassTag.INTERFACES
            if (tag == PandaClassTag.INTERFACES.getValue()){
                leb128.decode2UnsignedInteger(this.offset.plus(index).intValue());
                this.numIfaces = leb128.getUnsignedInteger();
                index = index.plus(leb128.getSize());
                this.ifacesOffsets = new Offset(getPandaFile(),index);
                UnsignedInteger size = this.numIfaces.times(UnsignedInteger.valueOf(2));
                index = index.plus(size);
            } else if (tag == PandaClassTag.SOURCE_LANG.getValue()) {
                this.sourceLang = getPandaFile().getData()[this.offset.plus(index).intValue()];
                index = index.plus(UnsignedInteger.ONE);
            } else if (tag == PandaClassTag.RUNTIME_ANNOTATION.getValue()) {
                throw new PandaParseException("PandaClassTag.RUNTIME_ANNOTATION");
            } else if (tag == PandaClassTag.ANNOTATION.getValue()) {
                throw new PandaParseException("PandaClassTag.ANNOTATION");
            } else if (tag == PandaClassTag.RUNTIME_TYPE_ANNOTATION.getValue()) {
                throw new PandaParseException("PandaClassTag.RUNTIME_TYPE_ANNOTATION");
            }else if (tag == PandaClassTag.TYPE_ANNOTATION.getValue()) {
                throw new PandaParseException("PandaClassTag.TYPE_ANNOTATION");
            }else if (tag == PandaClassTag.SOURCE_FILE.getValue()){
                throw new PandaParseException("PandaClassTag.SOURCE_FILE");
            }
            tag = getPandaFile().getData()[this.offset.plus(index).intValue()];
        }
        index = index.plus(UnsignedInteger.ONE);
    }
    private void resolveFields(){
        if (this.numFields.compareTo(UnsignedInteger.ZERO) == 0) return;
        UnsignedInteger nums = this.numFields;
        pandaFields = new PandaField[nums.intValue()];
        int i = 0;
        while (nums.compareTo(UnsignedInteger.ZERO) != 0){
            PandaField pandaField = PandaField.create(this.getPandaFile(),this.offset.plus(index));
            pandaField.resolve();
            pandaFields[i++] = pandaField;
            index = index.plus(pandaField.getSize());
            nums = nums.minus(UnsignedInteger.ONE);
        }
        for (PandaField pandaField : this.pandaFields) {
            if (pandaField.getName().equals("moduleRecordIdx")){
                this.pandaClassModuleData = PandaClassModuleData.create(getPandaFile(),pandaField.getPandaFieldValue().getOffset(),this);
                break;
            }
        }
    }

    private void resolveMethods(){
        if (this.numMethods.compareTo(UnsignedInteger.ZERO) == 0) return;
        UnsignedInteger nums = this.numMethods;
        int i = 0;
        while (nums.compareTo(UnsignedInteger.ZERO) != 0){
            PandaMethod pandaMethod = PandaMethod.create(this.getPandaFile(),this.offset.plus(index));
            pandaMethod.resolve();
            pandaMethods.put(pandaMethod.getName().getContent(),pandaMethod);
            index = index.plus(pandaMethod.getSize());
            nums = nums.minus(UnsignedInteger.ONE);
        }

    }

    public static PandaClass create(PandaFile pandaFile, UnsignedInteger offset,IndexHeader indexHeader){
        Offset offset1 = pandaFile.resolveOffset(offset);
        if (offset1 instanceof PandaClass) return (PandaClass) offset1;
        else{
            PandaClass pandaClass = new PandaClass(pandaFile,offset,indexHeader);
            pandaFile.addOffset(offset,pandaClass);
            return pandaClass;
        }
    }

    public IndexHeader getParent(){
        return this.indexHeader;
    }


    @Override
    public void resolve() {
        if (isValid()){
            if (resolved)
                return;
            resolveClasses();
            resolveFields();
            resolveMethods();
            resolved = true;
        }

    }

    public PandaIRCFG[] disAssembleAllMethods() {
        if (this.pandaMethods == null) return new PandaIRCFG[]{};
        LinkedList<PandaIRCFG> pandaIRCFGS = new LinkedList<>();
        for (String s : pandaMethods.keySet()) {
            pandaIRCFGS.add(pandaMethods.get(s).disAssemble());
        }
        return pandaIRCFGS.toArray(new PandaIRCFG[]{});
    }

    @Override
    public byte[] toByteArray() {
        ByteBuffer buffer = ByteBuffer.allocate(index.intValue()).order(ByteOrder.LITTLE_ENDIAN);
        if (!isValid()){
            return buffer.array();
        }
        buffer.put(this.name.toByteArray());
        if (isExternal()){
            return buffer.array();
        }
        buffer.putInt(superClassOff.getOffset().intValue())
                .put(Leb128.writeUnsignedLeb128(accessFlags.intValue()))
                .put(Leb128.writeUnsignedLeb128(numFields.intValue()))
                .put(Leb128.writeUnsignedLeb128(numMethods.intValue()));
        //put SOURCE_LANG
        buffer.put(PandaClassTag.SOURCE_LANG.getValue());
        buffer.put(this.sourceLang);
        //put INTERFACES
        if (this.numIfaces.compareTo(UnsignedInteger.ZERO) != 0){
            buffer.put(PandaClassTag.INTERFACES.getValue())
                    .put(Leb128.writeUnsignedLeb128(numIfaces.intValue()))
                    .putInt(this.numIfaces.times(UnsignedInteger.valueOf(2)).intValue());
        }
        //put NOTHING
        buffer.put(PandaClassTag.NOTHING.getValue());
        if (pandaFields != null)
            for (PandaField pandaField : pandaFields) {
                buffer.put(pandaField.toByteArray());
            }
        //put methods description
        for (PandaMethod value : pandaMethods.values()) {
            buffer.put(value.toByteArray());
        }
        return buffer.array();
    }
}