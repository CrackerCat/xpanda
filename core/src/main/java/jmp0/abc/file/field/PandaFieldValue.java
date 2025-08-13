package jmp0.abc.file.field;

import com.google.common.primitives.UnsignedInteger;
import com.google.common.primitives.UnsignedLong;
import jmp0.abc.PandaParseException;
import jmp0.abc.file.PandaFile;
import jmp0.abc.file.PandaFileUtils;
import jmp0.abc.file.desc.Offset;
import jmp0.abc.file.type.PandaRawType;
import lombok.Getter;
import lombok.SneakyThrows;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
@Getter
public final class PandaFieldValue extends Offset {
    private final PandaRawType pandaRawType;
    private final Number value;
    @SneakyThrows
    private PandaFieldValue(PandaFile pandaFile, UnsignedInteger offset, PandaRawType rawType) {
        super(pandaFile, offset);
        this.pandaRawType = rawType;
        ByteBuffer buffer = ByteBuffer.wrap(pandaFile.getData()).order(ByteOrder.LITTLE_ENDIAN);
        switch (rawType){
            case I64:{
                Offset dest = PandaFileUtils.readOffset(pandaFile,0);
                value = buffer.getLong(dest.getOffset().intValue());
                break;
            }
            case U64:{
                Offset dest = PandaFileUtils.readOffset(pandaFile,0);
                value = UnsignedLong.valueOf(buffer.getLong(dest.getOffset().intValue()));
                break;
            }
            case F64:{
                Offset dest = PandaFileUtils.readOffset(pandaFile,0);
                value = buffer.getDouble(dest.getOffset().intValue());
                break;
            }
            case U32:{
                value = UnsignedInteger.valueOf(buffer.getInt(offset.intValue()));
                break;
            }
            case I32:{
                value = buffer.getInt(offset.intValue());
                break;
            }
            case F32:{
                value = buffer.getFloat(offset.intValue());
                break;
            }
            default:{
                throw new PandaParseException(rawType.name() + " not supported!");
            }
        }
    }

    public static PandaFieldValue create(PandaFile pandaFile, UnsignedInteger offset, PandaRawType rawType){
        Offset offset1 = pandaFile.resolveOffset(offset);
        if (offset1 instanceof PandaFieldValue) return (PandaFieldValue) offset1;
        else{
            PandaFieldValue pandaFieldValue = new PandaFieldValue(pandaFile,offset,rawType);
            pandaFile.addOffset(offset,pandaFieldValue);
            return pandaFieldValue;
        }
    }

    @Override
    public String toString() {
        return String.valueOf(this.value);
    }
}
