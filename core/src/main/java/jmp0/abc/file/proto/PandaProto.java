package jmp0.abc.file.proto;

import com.google.common.primitives.UnsignedInteger;
import jmp0.abc.PandaParseException;
import jmp0.abc.file.IPandaCanOutput;
import jmp0.abc.file.PandaFile;
import jmp0.abc.file.PandaFileUtils;
import jmp0.abc.file.desc.Offset;
import jmp0.abc.file.type.PandaRawType;
import jmp0.abc.file.type.PandaType;
import lombok.Getter;
import lombok.SneakyThrows;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
@Getter
final public class PandaProto extends Offset implements IPandaCanOutput {
    private final PandaRawType[] pandaRawTypes;
    private UnsignedInteger numRef;
    private final static int SHORTY_ELEM_SIZE = 2;
    private final static int SHORTY_ELEM_WIDTH = 4;
    private final static int SHORTY_ELEM_MASK = 0xf;
    private final static int SHORTY_ELEM_PER16 = 4;
    private final int size;
    private final LinkedList<Short> vList = new LinkedList<>();
    @SneakyThrows
    public PandaProto(PandaFile pandaFile, UnsignedInteger offset) {
        super(pandaFile, offset);
        LinkedList<PandaRawType> pandaRawTypeLinkedList = new LinkedList<>();
        UnsignedInteger index = UnsignedInteger.ZERO;
        short v = PandaFileUtils.bytes2Uint16(pandaFile.getData(),offset.plus(index).intValue());
        vList.add(v);
        index = index.plus(UnsignedInteger.valueOf(2));
        numRef = UnsignedInteger.ZERO;
        UnsignedInteger elem_num_ = UnsignedInteger.ZERO;
        while (v != 0){
            int shift = (elem_num_.intValue() % SHORTY_ELEM_PER16) * SHORTY_ELEM_WIDTH;
            byte elem = (byte) ((v >> shift) & SHORTY_ELEM_MASK);
            if (elem == 0) {
                break;
            }
            elem_num_ = elem_num_.plus(UnsignedInteger.ONE);
            PandaRawType pandaRawType = PandaRawType.getType(elem);
            if (pandaRawType == null){
                throw new PandaParseException("pandaRawType can not be resolved.");
            }
            if (!pandaRawType.isPrimitive()){
                numRef = numRef.plus(UnsignedInteger.ONE);
            }
            pandaRawTypeLinkedList.add(pandaRawType);
            if ((elem_num_.intValue() % SHORTY_ELEM_PER16) == 0) {
                v = PandaFileUtils.bytes2Uint16(pandaFile.getData(),offset.plus(index).intValue());
                vList.add(v);
                index = index.plus(UnsignedInteger.valueOf(2));
            }
        }
        this.pandaRawTypes = pandaRawTypeLinkedList.toArray(PandaRawType[]::new);
        this.size = index.intValue() + numRef.intValue() * 2;
    }

    public static PandaProto create(PandaFile pandaFile, UnsignedInteger offset){
        Offset offset1 = pandaFile.resolveOffset(offset);
        if (offset1 != null) return (PandaProto) offset1;
        else{
            PandaProto pandaProto = new PandaProto(pandaFile,offset);
            pandaFile.addOffset(offset,pandaProto);
            return pandaProto;
        }
    }

    @Override
    public byte[] toByteArray() {
        ByteBuffer buffer = ByteBuffer.allocate(this.size).order(ByteOrder.LITTLE_ENDIAN);
        for (Short i : vList) {
            buffer.putShort(i);
        }
        return buffer.array();
    }
}
