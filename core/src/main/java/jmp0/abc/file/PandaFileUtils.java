package jmp0.abc.file;

import com.google.common.primitives.Bytes;
import com.google.common.primitives.UnsignedBytes;
import com.google.common.primitives.UnsignedInteger;
import jmp0.abc.file.desc.Offset;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class PandaFileUtils {
    public static byte[] readSubBytes(byte[] bytes,int index,int len){
        byte[] out = new byte[len];
        System.arraycopy(bytes,index,out,0,len);
        return out;
    }
    public static UnsignedInteger bytes2UnsignedInteger(byte[] bytes,int index){
        int value = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getInt(index);
        if (value == -1) return UnsignedInteger.MAX_VALUE;
        else return UnsignedInteger.valueOf(((long) value & 0xFFFFFFFL));
    }

    public static short bytes2Uint16(byte[] bytes, int index){
        return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getShort(index);
    }

    public static Offset readOffset(PandaFile pandaFile,int index){
        UnsignedInteger offset = PandaFileUtils.bytes2UnsignedInteger(pandaFile.getData(),index);
        return new Offset(pandaFile,offset);
    }
}
