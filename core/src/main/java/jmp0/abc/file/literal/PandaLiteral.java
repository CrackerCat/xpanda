package jmp0.abc.file.literal;

import com.google.common.primitives.UnsignedInteger;
import jmp0.abc.PandaParseException;
import jmp0.abc.file.IPandaCanOutput;
import jmp0.abc.file.PandaFile;
import jmp0.abc.file.PandaFileUtils;
import jmp0.abc.file.desc.Offset;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.ToString;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Base64;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
@Getter
public abstract class PandaLiteral extends Offset implements IPandaCanOutput {
    public int size = 0;
    protected final LiteralTag tag;
    protected final byte[] valueByteArr;
    protected PandaLiteral(PandaFile pandaFile, UnsignedInteger offset,int size,LiteralTag tag) {
        super(pandaFile, offset);
        this.size = size;
        this.tag = tag;
        this.valueByteArr = PandaFileUtils.readSubBytes(pandaFile.getData(),offset.intValue(),size);
    }

    @SneakyThrows
    public Number getValue(){
        ByteBuffer buffer = ByteBuffer.wrap(this.valueByteArr).order(ByteOrder.LITTLE_ENDIAN);
        switch (tag){
            case INTEGER:
                return buffer.getInt();
            case FLOAT:
                return buffer.getFloat();
            case DOUBLE:
                return buffer.getDouble();
            case METHODAFFILIATE:
                return buffer.getShort();
            default:
                throw new PandaParseException(String.format("LiteralTag %s not known.",tag.name()));
        }
    }

    @Override
    public String toString() {
        return tag.name().toLowerCase()  +":"+ Arrays.toString(valueByteArr);
    }

    @Override
    public byte[] toByteArray() {
        ByteBuffer buffer = ByteBuffer.allocate(valueByteArr.length).order(ByteOrder.LITTLE_ENDIAN);
        buffer.put(valueByteArr);
        return buffer.array();
    }
}
