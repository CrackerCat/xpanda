package jmp0.abc.file.literal;

import com.google.common.primitives.UnsignedInteger;
import jmp0.abc.PandaParseException;
import jmp0.abc.codec.Leb128;
import jmp0.abc.file.IPandaCanOutput;
import jmp0.abc.file.PandaFile;
import jmp0.abc.file.PandaFileUtils;
import jmp0.abc.file.desc.Offset;
import jmp0.abc.file.desc.PandaString;
import jmp0.abc.file.method.PandaMethod;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.ToString;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.LinkedList;

import static jmp0.abc.file.literal.LiteralTag.*;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
@ToString
@Getter
final public class PandaLiteralArray extends Offset implements IPandaCanOutput {
    private final UnsignedInteger numLiterals;
    private final Offset[] pandaLiterals;
    @SneakyThrows
    private PandaLiteralArray(PandaFile pandaFile, UnsignedInteger offset){
        super(pandaFile, offset);
        UnsignedInteger index = UnsignedInteger.ZERO;
        this.numLiterals = PandaFileUtils.bytes2UnsignedInteger(pandaFile.getData(),offset.plus(index).intValue());
        index = index.plus(UnsignedInteger.valueOf(4));
        int realSize = this.numLiterals.intValue() / 2;
        this.pandaLiterals = new Offset[realSize];
        for (int i = 0; i < realSize; i++) {
            byte value = pandaFile.getData()[offset.plus(index).intValue()];
            LiteralTag tag = getType(value);
            if (tag == null){
                throw new PandaParseException(String.format("LiteralTag get null tag_value:%d",value));
            }
            index = index.plus(UnsignedInteger.ONE);
            switch (tag) {
                case STRING:{
                    UnsignedInteger dest = PandaFileUtils.bytes2UnsignedInteger(pandaFile.getData(),offset.plus(index).intValue());
                    this.pandaLiterals[i] = PandaString.create(pandaFile,dest);
                    index = index.plus(UnsignedInteger.valueOf(4));
                    break;
                }
                case GETTER:
                case SETTER:
                case METHOD:
                case GENERATORMETHOD:
                case ASYNCGENERATORMETHOD:{
                    UnsignedInteger dest = PandaFileUtils.bytes2UnsignedInteger(pandaFile.getData(),offset.plus(index).intValue());
                    this.pandaLiterals[i] = PandaMethod.create(pandaFile,dest);
                    index = index.plus(UnsignedInteger.valueOf(4));
                    break;
                }
                case INTEGER:
                case FLOAT:
                case LITERALBUFFERINDEX: {
                    this.pandaLiterals[i] = PandaByteFourLiteral.create(pandaFile,offset.plus(index),tag);
                    index = index.plus(UnsignedInteger.valueOf(4));
                    break;
                }
                case LITERALARRAY: {
                    Offset la = PandaFileUtils.readOffset(pandaFile,offset.plus(index).intValue());
                    this.pandaLiterals[i] = PandaLiteralArray.create(pandaFile,la.getOffset());
                    index = index.plus(UnsignedInteger.valueOf(4));
                    break;
                }
                case ARRAY_U1:
                case ARRAY_U8:
                case ARRAY_I8:
                case ARRAY_U16:
                case ARRAY_I16:
                case ARRAY_U32:
                case ARRAY_I32:
                case ARRAY_U64:
                case ARRAY_I64:
                case ARRAY_F32:
                case ARRAY_F64:
                case ARRAY_STRING: {
                    this.pandaLiterals[i] = PandaByteFourLiteral.create(pandaFile,offset.plus(index),tag);
                    i = realSize;
                    break;
                }
                case DOUBLE: {
                    this.pandaLiterals[i] = PandaByteEightLiteral.create(pandaFile,offset.plus(index),tag);
                    index = index.plus(UnsignedInteger.valueOf(8));
                    break;
                }
                case TAGVALUE:
                case BOOL:
                case BUILTINTYPEINDEX:
                case ACCESSOR:
                case NULLVALU: {
                    this.pandaLiterals[i] = PandaByteOneLiteral.create(pandaFile,offset.plus(index),tag);
                    index = index.plus(UnsignedInteger.ONE);
                    break;
                }
                case METHODAFFILIATE: {
                    this.pandaLiterals[i] = PandaByteTwoLiteral.create(pandaFile,offset.plus(index),tag);
                    index = index.plus(UnsignedInteger.valueOf(2));
                    break;
                }
                default: {
                    throw new PandaParseException("LiteralTag unknown!");
                }
            }
        }
    }

    public static PandaLiteralArray create(PandaFile pandaFile, UnsignedInteger offset){
        Offset offset1 = pandaFile.resolveOffset(offset);
        if (offset1 instanceof PandaLiteralArray) return (PandaLiteralArray) offset1;
        else{
            PandaLiteralArray pandaLiteralArray = new PandaLiteralArray(pandaFile,offset);
            pandaFile.addOffset(offset,pandaLiteralArray);
            return pandaLiteralArray;
        }
    }

    @Override
    public byte[] toByteArray() {
        int size = 4;
        for (Offset pandaLiteral : pandaLiterals) {
            if (pandaLiteral instanceof PandaLiteral){
                size += ((PandaLiteral) pandaLiteral).getSize();
            }else if (pandaLiteral instanceof PandaString){
                size += 4;
            }else if (pandaLiteral instanceof PandaMethod){
                size += 4;
            }
            size += 1;
        }
        ByteBuffer buffer = ByteBuffer.allocate(size).order(ByteOrder.LITTLE_ENDIAN);
        buffer.putInt(numLiterals.intValue());
        for (Offset pandaLiteral : pandaLiterals) {
            if (pandaLiteral instanceof PandaLiteral){
                buffer.put(((PandaLiteral) pandaLiteral).getTag().getValue());
                buffer.put(((PandaLiteral) pandaLiteral).toByteArray());
            }else if (pandaLiteral instanceof PandaString){
                buffer.put(STRING.getValue());
                buffer.putInt(pandaLiteral.getOffset().intValue());
            }else if (pandaLiteral instanceof PandaMethod){
                buffer.put(METHOD.getValue());
                buffer.putInt(pandaLiteral.getOffset().intValue());
            }
            size += 1;
        }
        return buffer.array();
    }
}
