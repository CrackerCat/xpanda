package jmp0.abc.file.desc;

import com.google.common.primitives.UnsignedInteger;
import jmp0.abc.codec.Leb128;
import jmp0.abc.codec.MUTF8;
import jmp0.abc.file.IPandaCanOutput;
import jmp0.abc.file.PandaFile;
import lombok.Getter;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
@Getter
public class PandaString extends Offset implements IPandaCanOutput {
    private final UnsignedInteger utf16Size;
    private UnsignedInteger size;
    private final boolean isAscii;
    protected final String content;
    protected PandaString(PandaFile pandaFile, UnsignedInteger offset) {
        super(pandaFile, offset);
        if (!isValid()){
            this.utf16Size = UnsignedInteger.ZERO;
            this.size = UnsignedInteger.ZERO;
            this.content = "";
            this.isAscii = true;
            return;
        }
        Leb128 leb128 = pandaFile.getPandaFileLeb128();
        leb128.decode2UnsignedInteger(offset.intValue());
        this.utf16Size = leb128.getUnsignedInteger();
        this.isAscii = (this.utf16Size.intValue() & 1) == 1;
        int byteSize = utf16Size.intValue() >> 1;
        this.content = MUTF8.decode(this.getPandaFile().getData(), offset.plus(leb128.getSize()).intValue(), byteSize);
        this.size = leb128.getSize().plus(UnsignedInteger.valueOf(byteSize + 1));
    }
    public PandaString(PandaFile pandaFile){
        super(pandaFile, UnsignedInteger.ZERO);
        this.utf16Size = UnsignedInteger.ZERO;
        this.size = UnsignedInteger.ZERO;
        this.content = "";
        this.isAscii = true;
    }

    public static PandaString create(PandaFile pandaFile, UnsignedInteger offset){
        Offset offset1 = pandaFile.resolveOffset(offset);
        if (offset1 instanceof PandaString) return (PandaString) offset1;
        else{
            PandaString pandaString = new PandaString(pandaFile,offset);
            pandaFile.addOffset(offset,pandaString);
            return pandaString;
        }
    }

    public String toString(){
        return "\""+content+"\"";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof String){
            return this.content.equals(obj);
        }else if(obj instanceof PandaString){
            return this.content.equals(((PandaString) obj).content);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.content.hashCode();
    }

    @Override
    public byte[] toByteArray() {
        byte[] leb128Len = Leb128.writeUnsignedLeb128(this.utf16Size.intValue());
        byte[] content = MUTF8.encode(this.content);
        ByteBuffer buffer = ByteBuffer.allocate(leb128Len.length + content.length + 1).order(ByteOrder.LITTLE_ENDIAN);
        buffer.put(leb128Len)
                .put(content);
        return buffer.array();
    }
}
