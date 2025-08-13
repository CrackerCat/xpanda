package jmp0.abc.file.desc;

import com.google.common.primitives.UnsignedInteger;
import jmp0.abc.PandaParseException;
import jmp0.abc.file.IPandaCanOutput;
import jmp0.abc.file.PandaFileUtils;
import lombok.Getter;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */

//    struct Header {
//            std::array<uint8_t, MAGIC_SIZE> magic;
//        uint32_t checksum;
//        std::array<uint8_t, VERSION_SIZE> version;
//        uint32_t file_size;
//        uint32_t foreign_off;
//        uint32_t foreign_size;
//        uint32_t num_classes;
//        uint32_t class_idx_off;
//        uint32_t num_lnps;
//        uint32_t lnp_idx_off;
//        uint32_t num_literalarrays;
//        uint32_t literalarray_idx_off;
//        uint32_t num_indexes;
//        uint32_t index_section_off;
//        };
@Getter
public final class Header implements IPandaCanOutput {
    private static final int MAGIC_SIZE = 8;
    private static final int VERSION_SIZE = 4;
    private static final byte[] MAGIC_PANDA_BYTES = new byte[]{'P', 'A', 'N', 'D', 'A', '\0', '\0', '\0'};
    public static final int SIZE = 12*4 + 8 + 4;

    private final byte[] magic;
    private final UnsignedInteger checksum;
    private final byte[] version;
    private final UnsignedInteger fileSize;
    private final UnsignedInteger foreignOff;
    private final UnsignedInteger foreignSize;
    private final UnsignedInteger numClasses;
    private final UnsignedInteger classIdxOff;
    private final UnsignedInteger numLnps;
    private final UnsignedInteger lnpIdxOff;
    private final UnsignedInteger numLiteralArrays;
    private final UnsignedInteger literalArrayIdxOff;
    private final UnsignedInteger numIndexes;
    private final UnsignedInteger indexSectionOff;

    public Header(byte[] in, int index) throws PandaParseException {
        try {
            byte[] bs = PandaFileUtils.readSubBytes(in,index,SIZE);
            int nowIndex = 0;
            this.magic = PandaFileUtils.readSubBytes(bs,nowIndex,MAGIC_SIZE);
            if (0 != Arrays.compare(this.magic, MAGIC_PANDA_BYTES)){
                throw new PandaParseException("magic not matched!");
            }
            nowIndex += MAGIC_SIZE;
            this.checksum = PandaFileUtils.bytes2UnsignedInteger(bs,nowIndex);
            nowIndex += 4;
            this.version = PandaFileUtils.readSubBytes(bs,nowIndex,4);
            nowIndex += 4;
            this.fileSize = PandaFileUtils.bytes2UnsignedInteger(bs,nowIndex);
            nowIndex += 4;
            this.foreignOff = PandaFileUtils.bytes2UnsignedInteger(bs,nowIndex);
            nowIndex += 4;
            this.foreignSize = PandaFileUtils.bytes2UnsignedInteger(bs,nowIndex);
            nowIndex += 4;
            this.numClasses = PandaFileUtils.bytes2UnsignedInteger(bs,nowIndex);
            nowIndex += 4;
            this.classIdxOff = PandaFileUtils.bytes2UnsignedInteger(bs,nowIndex);
            nowIndex += 4;
            this.numLnps = PandaFileUtils.bytes2UnsignedInteger(bs,nowIndex);
            nowIndex += 4;
            this.lnpIdxOff = PandaFileUtils.bytes2UnsignedInteger(bs,nowIndex);
            nowIndex += 4;
            this.numLiteralArrays = PandaFileUtils.bytes2UnsignedInteger(bs,nowIndex);
            nowIndex += 4;
            this.literalArrayIdxOff = PandaFileUtils.bytes2UnsignedInteger(bs,nowIndex);
            nowIndex += 4;
            this.numIndexes = PandaFileUtils.bytes2UnsignedInteger(bs,nowIndex);
            nowIndex += 4;
            this.indexSectionOff = PandaFileUtils.bytes2UnsignedInteger(bs,nowIndex);
        }catch (Exception e){ throw new PandaParseException(e.getMessage()); }
    }

    @Override
    public byte[] toByteArray() {
        ByteBuffer buffer = ByteBuffer.allocate(SIZE).order(ByteOrder.LITTLE_ENDIAN);
        buffer.put(magic)
                .putInt(checksum.intValue())
                .put(version)
                .putInt(fileSize.intValue())
                .putInt(foreignOff.intValue())
                .putInt(foreignSize.intValue())
                .putInt(numClasses.intValue())
                .putInt(classIdxOff.intValue())
                .putInt(numLnps.intValue())
                .putInt(lnpIdxOff.intValue())
                .putInt(numLiteralArrays.intValue())
                .putInt(literalArrayIdxOff.intValue())
                .putInt(numIndexes.intValue())
                .putInt(indexSectionOff.intValue());
        return buffer.array();
    }
}
