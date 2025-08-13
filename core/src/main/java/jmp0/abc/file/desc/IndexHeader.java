package jmp0.abc.file.desc;

import com.google.common.primitives.UnsignedInteger;
import jmp0.abc.file.IPandaCanOutput;
import jmp0.abc.file.IResolvable;
import jmp0.abc.file.PandaFile;
import jmp0.abc.PandaParseException;
import jmp0.abc.file.PandaFileUtils;
import jmp0.abc.file.clazz.PandaClass;
import jmp0.abc.file.field.PandaField;
import jmp0.abc.file.method.PandaMethod;
import jmp0.abc.file.proto.PandaProto;
import lombok.Getter;
import lombok.ToString;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashSet;
import java.util.Objects;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */

//    struct RegionHeader {
//            uint32_t start;
//            uint32_t end;
//            uint32_t class_idx_size;
//            uint32_t class_idx_off;
//            uint32_t method_idx_size;
//            uint32_t method_idx_off;
//            uint32_t field_idx_size;
//            uint32_t field_idx_off;
//            uint32_t proto_idx_size;
//            uint32_t proto_idx_off;
//            };
@Getter
@ToString
public final class IndexHeader implements IResolvable , IPandaCanOutput {
    public static final int SIZE = 10*4;

    private final UnsignedInteger start;
    private final UnsignedInteger end;
    private final UnsignedInteger classIdxSize;
    private final UnsignedInteger classIdxOff;
    private final UnsignedInteger methodIdxSize;
    private final UnsignedInteger methodIdxOff;
    private final UnsignedInteger fieldIdxSize;
    private final UnsignedInteger fieldIdxOff;
    private final UnsignedInteger protoIdxSize;
    private final UnsignedInteger protoIdxOff;

    private final PandaClass[] pandaClasses;
    private final Offset[] pandaMethods;
    private final Offset[] pandaFields;
    private final PandaProto[] pandaProtos;
    private final HashSet<UnsignedInteger> moduleLiteralSet = new HashSet<>();

    public IndexHeader(PandaFile pandaFile, int index) throws PandaParseException {
        try {
            byte[] bs = PandaFileUtils.readSubBytes(pandaFile.getData(),index,SIZE);
            int nowIndex = 0;
            this.start = PandaFileUtils.bytes2UnsignedInteger(bs,nowIndex);
            nowIndex += 4;
            this.end = PandaFileUtils.bytes2UnsignedInteger(bs,nowIndex);
            nowIndex += 4;
            this.classIdxSize = PandaFileUtils.bytes2UnsignedInteger(bs,nowIndex);
            nowIndex += 4;
            this.classIdxOff = PandaFileUtils.bytes2UnsignedInteger(bs,nowIndex);
            nowIndex += 4;
            this.methodIdxSize = PandaFileUtils.bytes2UnsignedInteger(bs,nowIndex);
            nowIndex += 4;
            this.methodIdxOff = PandaFileUtils.bytes2UnsignedInteger(bs,nowIndex);
            nowIndex += 4;
            this.fieldIdxSize = PandaFileUtils.bytes2UnsignedInteger(bs,nowIndex);
            nowIndex += 4;
            this.fieldIdxOff = PandaFileUtils.bytes2UnsignedInteger(bs,nowIndex);
            nowIndex += 4;
            this.protoIdxSize = PandaFileUtils.bytes2UnsignedInteger(bs,nowIndex);
            nowIndex += 4;
            this.protoIdxOff = PandaFileUtils.bytes2UnsignedInteger(bs,nowIndex);

            this.pandaClasses = parseIndexHeaderClasses(pandaFile);
            this.pandaMethods = parseIndexHeaderMethods(pandaFile);
            this.pandaFields = parseIndexHeaderFields(pandaFile);
            this.pandaProtos = parseIndexHeaderProtos(pandaFile);
        }catch (Exception e){
            throw new PandaParseException(e);
        }

    }

    private PandaClass[] parseIndexHeaderClasses(PandaFile pandaFile){
        int offset = this.classIdxOff.intValue();
        int size = this.classIdxSize.intValue();
        PandaClass[] ret = new PandaClass[size];
        for (int i = 0; i < size; i++) {
            ret[i] = PandaClass.create(pandaFile,
                    PandaFileUtils.bytes2UnsignedInteger(pandaFile.getData(),offset + i * 4),this);
        }
        return ret;
    }

    private Offset[] parseIndexHeaderMethods(PandaFile pandaFile){
        int offset = this.methodIdxOff.intValue();
        int size = this.methodIdxSize.intValue();
        Offset[] ret = new Offset[size];
        for (int i = 0; i < size; i++) {
            ret[i] = new Offset(pandaFile,
                    PandaFileUtils.bytes2UnsignedInteger(pandaFile.getData(),offset + i * 4));
        }
        return ret;
    }

    /**
     bugfix 20240726 Do you have nothing to do?????
     ## IndexHeader
     1. We delete the corresponding [field_idx_off] offsets-array from file format from version 12.0.1.0.
     2. We invalid the [field_idx_size] & [field_idx_off] by setting [0xffffffff] in IndexHeader from version 12.0.1.0.
     3. We delete the corresponding [proto_idx_off] offsets-array from file format from version 12.0.1.0.
     4. We invalid the [proto_idx_size] & [proto_idx_off] by setting [0xffffffff] in IndexHeader from version 12.0.1.0.
     */
    private Offset[] parseIndexHeaderFields(PandaFile pandaFile){
        if (Objects.equals(this.fieldIdxOff, UnsignedInteger.MAX_VALUE))
            return null;
        int offset = this.fieldIdxOff.intValue();
        int size = this.fieldIdxSize.intValue();
        Offset[] ret = new Offset[size];
        for (int i = 0; i < size; i++) {
            ret[i] = new Offset(pandaFile,
                    PandaFileUtils.bytes2UnsignedInteger(pandaFile.getData(),offset + i * 4));
        }
        return ret;
    }

    private PandaProto[] parseIndexHeaderProtos(PandaFile pandaFile){
        if (Objects.equals(this.protoIdxOff, UnsignedInteger.MAX_VALUE))
            return null;
        int offset = this.protoIdxOff.intValue();
        int size = this.protoIdxSize.intValue();
        PandaProto[] ret = new PandaProto[size];
        for (int i = 0; i < size; i++) {
            ret[i] = PandaProto.create(pandaFile,
                    PandaFileUtils.bytes2UnsignedInteger(pandaFile.getData(),offset + i * 4));
        }
        return ret;
    }

    public void addModuleLiteral(UnsignedInteger offset){
        this.moduleLiteralSet.add(offset);
    }

    public boolean moduleLiteralContain(UnsignedInteger offset){
       return this.moduleLiteralSet.contains(offset);
    }

    @Override
    public void resolve() {
        for (PandaClass pandaClass : pandaClasses) {
            pandaClass.resolve();
        }
    }

    @Override
    public byte[] toByteArray() {
        int realSize = SIZE;
        realSize += pandaClasses.length * 4;
        realSize += pandaMethods.length * 4;
        if (pandaFields != null){
            realSize += pandaFields.length * 4;
        }
        if (pandaProtos != null){
            realSize += pandaProtos.length * 4;
        }

        ByteBuffer buffer = ByteBuffer.allocate(realSize).order(ByteOrder.LITTLE_ENDIAN);
        //put header
        buffer.putInt(start.intValue())
                .putInt(end.intValue())
                .putInt(classIdxSize.intValue())
                .putInt(classIdxOff.intValue())
                .putInt(methodIdxSize.intValue())
                .putInt(methodIdxOff.intValue())
                .putInt(fieldIdxSize.intValue())
                .putInt(fieldIdxOff.intValue())
                .putInt(protoIdxSize.intValue())
                .putInt(protoIdxOff.intValue());
        //put class index
        for (PandaClass pandaClass : pandaClasses) {
            buffer.putInt(pandaClass.getOffset().intValue());
        }
        //put method index
        for (Offset method : pandaMethods) {
            buffer.putInt(method.getOffset().intValue());
        }
        if (pandaFields != null)
            for (Offset field : pandaFields) {
                buffer.putInt(field.getOffset().intValue());
            }
        if (pandaProtos != null)
            for (Offset proto : pandaProtos) {
                buffer.putInt(proto.getOffset().intValue());
            }
        return buffer.array();
    }
}
