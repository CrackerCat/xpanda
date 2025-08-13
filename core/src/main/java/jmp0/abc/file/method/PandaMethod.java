package jmp0.abc.file.method;

import com.google.common.primitives.UnsignedInteger;
import jmp0.abc.PandaParseException;
import jmp0.abc.codec.Leb128;
import jmp0.abc.disasm.PandaDisAssembler;
import jmp0.abc.disasm.block.PandaIRCFG;
import jmp0.abc.file.IPandaCanOutput;
import jmp0.abc.file.IResolvable;
import jmp0.abc.file.PandaFile;
import jmp0.abc.file.PandaFileUtils;
import jmp0.abc.file.anno.PandaAnnotation;
import jmp0.abc.file.clazz.PandaClass;
import jmp0.abc.file.debug.PandaMethodDebugInfo;
import jmp0.abc.file.desc.IndexHeader;
import jmp0.abc.file.desc.Offset;
import jmp0.abc.file.desc.PandaString;
import jmp0.abc.file.proto.PandaProto;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.LinkedList;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
@Getter
final public class PandaMethod extends Offset implements IResolvable, IPandaCanOutput {
    private final short classIdx;
    private final short protoIdx;
    private PandaClass pandaClass;
    private PandaProto pandaProto;
    private final IndexHeader indexHeader;
    private final PandaString name;
    private final UnsignedInteger size;
    private final UnsignedInteger accessFlag;
    private final boolean methodExternal;
    private PandaMethodCode methodCode = null;
    private PandaMethodDebugInfo pandaMethodDebugInfo = null;
    private byte sourceLang = 0;
    private final LinkedList<PandaAnnotation> pandaAnnotations = new LinkedList<>();
    //use to get now lex environment...
    private LinkedList<Integer> lexEnvList = new LinkedList<>();

    @SneakyThrows
    private PandaMethod(PandaFile pandaFile, UnsignedInteger offset) {
        super(pandaFile, offset);
        this.indexHeader = this.getPandaFile().resolveIndexHeaderByOffset(this);
        UnsignedInteger index = UnsignedInteger.ZERO;
        this.classIdx = PandaFileUtils.bytes2Uint16(pandaFile.getData(),offset.intValue());
        index = index.plus(UnsignedInteger.valueOf(2));
        this.protoIdx = PandaFileUtils.bytes2Uint16(pandaFile.getData(),offset.plus(index).intValue());
        index = index.plus(UnsignedInteger.valueOf(2));
        this.name = PandaString.create(pandaFile,PandaFileUtils.readOffset(pandaFile,offset.plus(index).intValue()).getOffset());
        Leb128 leb128 = pandaFile.getPandaFileLeb128();
        index = index.plus(UnsignedInteger.valueOf(4));
        leb128.decode2UnsignedInteger(offset.plus(index).intValue());
        this.accessFlag = leb128.getUnsignedInteger();
        this.methodExternal = isExternal();
        if (this.isMethodExternal()){
            index = index.plus(leb128.getSize());
            leb128.decode2UnsignedInteger(offset.plus(index).intValue());
            UnsignedInteger unsignedInteger = leb128.getUnsignedInteger();
            this.size = index;
            return;
        }
        index = index.plus(leb128.getSize());
        byte tag = pandaFile.getData()[this.offset.plus(index).intValue()];
        while (tag != PandaMethodTag.NOTHING.getValue()){
            index = index.plus(UnsignedInteger.ONE);
            if (tag == PandaMethodTag.CODE.getValue()){
                UnsignedInteger codeOffset = PandaFileUtils.bytes2UnsignedInteger(getPandaFile().getData(),this.offset.plus(index).intValue());
                this.methodCode = PandaMethodCode.create(pandaFile,codeOffset,this);
                index = index.plus(UnsignedInteger.valueOf(4));
            }else if (tag == PandaMethodTag.SOURCE_LANG.getValue()){
                this.sourceLang = getPandaFile().getData()[this.offset.plus(index).intValue()];
                index = index.plus(UnsignedInteger.ONE);
            }else if (tag == PandaMethodTag.RUNTIME_ANNOTATION.getValue()){
                throw new PandaParseException("PandaMethodTag.RUNTIME_ANNOTATION");
            }else if (tag == PandaMethodTag.RUNTIME_PARAM_ANNOTATION.getValue()){
                throw new PandaParseException("PandaMethodTag.RUNTIME_PARAM_ANNOTATION");
            }else if (tag == PandaMethodTag.DEBUG_INFO.getValue()){
                this.pandaMethodDebugInfo = PandaMethodDebugInfo.create(pandaFile,PandaFileUtils.bytes2UnsignedInteger(getPandaFile().getData(),this.offset.plus(index).intValue()),this);
                index = index.plus(UnsignedInteger.valueOf(4));
            }else if (tag == PandaMethodTag.ANNOTATION.getValue()){
                pandaAnnotations.add(PandaAnnotation.create(getPandaFile(),PandaFileUtils.bytes2UnsignedInteger(getPandaFile().getData(),this.offset.plus(index).intValue())));
                index = index.plus(UnsignedInteger.valueOf(4));
            }else if (tag == PandaMethodTag.PARAM_ANNOTATION.getValue()){
                throw new PandaParseException("PandaMethodTag.PARAM_ANNOTATION");
            }else if (tag == PandaMethodTag.TYPE_ANNOTATION.getValue()){
                throw new PandaParseException("PandaMethodTag.TYPE_ANNOTATION");
            }else if (tag == PandaMethodTag.RUNTIME_TYPE_ANNOTATION.getValue()){
                throw new PandaParseException("PandaMethodTag.RUNTIME_TYPE_ANNOTATION");
            }
            tag = pandaFile.getData()[this.offset.plus(index).intValue()];
        }
        this.size = index.plus(UnsignedInteger.ONE);

    }

    public static PandaMethod create(PandaFile pandaFile, UnsignedInteger offset){
        Offset offset1 = pandaFile.resolveOffset(offset);
        if (offset1 instanceof PandaMethod) return (PandaMethod) offset1;
        else{
            PandaMethod pandaMethod = new PandaMethod(pandaFile,offset);
            pandaFile.addOffset(offset,pandaMethod);
            return pandaMethod;
        }
    }

    @Override
    public void resolve() {
        if (indexHeader != null){
            if (this.classIdx < indexHeader.getClassIdxSize().intValue()) this.pandaClass = indexHeader.getPandaClasses()[this.classIdx];
            if (this.protoIdx < indexHeader.getProtoIdxSize().intValue()) this.pandaProto = indexHeader.getPandaProtos()[this.protoIdx];
        }
        for (PandaAnnotation pandaAnnotation : this.pandaAnnotations) {
            pandaAnnotation.resolve();
        }
        if (this.pandaMethodDebugInfo != null) this.pandaMethodDebugInfo.resolve();
    }

    public PandaClass getParent(){
        return this.pandaClass;
    }

    public boolean hasException(){
        return this.methodCode.getPandaMethodTryBlocks() != null;
    }

    public PandaIRCFG disAssemble() {
        if (this.methodCode == null) return null;
        return PandaDisAssembler.disAssembly(this);
    }

    @Override
    public String toString() {
        return "method:"+getName();
    }

    @Override
    public byte[] toByteArray() {
        ByteBuffer buffer = ByteBuffer.allocate(getSize().intValue()).order(ByteOrder.LITTLE_ENDIAN);
        buffer.putShort(classIdx)
                .putShort(protoIdx)
                .putInt(name.getOffset().intValue())
                .put(Leb128.writeUnsignedLeb128(accessFlag.intValue()));
        //put methodCode offset
        if (methodCode != null){
            buffer.put(PandaMethodTag.CODE.getValue());
            buffer.putInt(methodCode.getOffset().intValue());
        }
        //put source lang
        buffer.put(PandaMethodTag.SOURCE_LANG.getValue());
        buffer.put(sourceLang);
        //put debugInfo
        if (pandaMethodDebugInfo != null){
            buffer.put(PandaMethodTag.DEBUG_INFO.getValue());
            buffer.putInt(pandaMethodDebugInfo.getOffset().intValue());
        }
        //put ANNOTATION
        if (!pandaAnnotations.isEmpty()){
            for (PandaAnnotation pandaAnnotation : pandaAnnotations) {
                buffer.put(PandaMethodTag.ANNOTATION.getValue());
                buffer.putInt(pandaAnnotation.getOffset().intValue());
            }
        }
        buffer.put(PandaMethodTag.NOTHING.getValue());
        return buffer.array();
    }
}
