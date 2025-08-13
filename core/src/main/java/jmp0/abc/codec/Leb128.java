package jmp0.abc.codec;

import com.google.common.primitives.UnsignedInteger;

import java.nio.ByteBuffer;
import java.util.LinkedList;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */

public final class Leb128 {
    private UnsignedInteger unsignedInteger;
    private UnsignedInteger size;
    private int signedResult;
    private final byte[] bs;
    public Leb128(byte[] bs){
        this.bs = bs;
    }

    public UnsignedInteger getUnsignedInteger() {
        synchronized (this) {
            return unsignedInteger;
        }
    }

    public UnsignedInteger getSize() {
        synchronized (this) {
            return size;
        }
    }

    public int getSignedResult() {
        synchronized (this) {
            return signedResult;
        }
    }

    public void decode2UnsignedInteger(int offset){
        synchronized (this){
            int result = 0;
            int cur;
            int count = 0;
            int idx = 0;
            do {
                cur = bs[offset + idx++] & 0xff;
                result |= (cur & 0x7f) << (count * 7);
                count++;
            } while (((cur & 0x80) == 0x80) && count < 5);
            if (result == -1){
                this.unsignedInteger = UnsignedInteger.MAX_VALUE;
                this.size = UnsignedInteger.valueOf(5);
                return;
            }
            this.unsignedInteger = UnsignedInteger.valueOf(result);
            this.size = UnsignedInteger.valueOf(count);
        }
    }

    public void decode2Integer(int offset){
        synchronized (this){
            int result = 0;
            int cur;
            int count = 0;
            int signBits = -1;
            int idx = 0;
            do {
                cur = bs[offset + idx++] & 0xff;
                result |= (cur & 0x7f) << (count * 7);
                signBits <<= 7;
                count++;
            } while (((cur & 0x80) == 0x80) && count < 5);

            // Sign extend if appropriate
            if (((signBits >> 1) & result) != 0 ) {
                result |= signBits;
            }

            this.signedResult = result;
            this.size = UnsignedInteger.valueOf(count);
        }
    }

    public static byte[] writeUnsignedLeb128(int value) {
        LinkedList<Byte> bsList = new LinkedList<>();
        for (int remaining = value >>> 7; remaining != 0; remaining >>>= 7) {
            bsList.add((byte) ((value & 127) | 0x80));
            value = remaining;
        }
        bsList.add((byte) (value & 127));
        ByteBuffer buffer = ByteBuffer.allocate(bsList.size());
        for (Byte b : bsList) {
            buffer.put(b);
        }
        return buffer.array();
    }

    public static byte[] writeSignedLeb128(int value ) {
        LinkedList<Byte> bsList = new LinkedList<>();
        int remaining = value >> 7;
        boolean hasMore = true;
        int end = ( ( value & Integer.MIN_VALUE ) == 0 ) ? 0 : -1;

        while ( hasMore ) {
            hasMore = ( remaining != end ) || ( ( remaining & 1 ) != ( ( value >> 6 ) & 1 ) );

            bsList.add( ( byte ) ( ( value & 0x7f ) | ( hasMore ? 0x80 : 0 ) ) );
            value = remaining;
            remaining >>= 7;
        }
        ByteBuffer buffer = ByteBuffer.allocate(bsList.size());
        for (Byte b : bsList) {
            buffer.put(b);
        }
        return buffer.array();
    }


}
