package jmp0.abc.file.debug;

import com.google.common.primitives.UnsignedInteger;
import jmp0.abc.PandaParseException;
import jmp0.abc.codec.Leb128;
import jmp0.abc.file.IPandaCanOutput;
import jmp0.abc.file.PandaFile;
import jmp0.abc.file.desc.Offset;
import jmp0.abc.file.desc.PandaString;
import lombok.Getter;
import lombok.SneakyThrows;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.LinkedList;
import java.util.List;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
@Getter
final public class PandaLineNumberProgram extends Offset implements IPandaCanOutput {
    private PandaString fileName;
    private PandaString sourceCode;
    private UnsignedInteger column;
    private List<Byte> opCodeList = new LinkedList<>();

    @SneakyThrows
    public PandaLineNumberProgram(PandaFile pandaFile, UnsignedInteger offset,byte[] constPool) {
        super(pandaFile, offset);
        UnsignedInteger index = UnsignedInteger.ZERO;
        UnsignedInteger constPoolIndex = UnsignedInteger.ZERO;
        Leb128 constPoolLeb128 = new Leb128(constPool);
        Leb128 programLeb128 = new Leb128(pandaFile.getData());
        //handle opcode
        while (true){
            byte value = pandaFile.getData()[offset.plus(index).intValue()];
            opCodeList.add(value);
            PandaLineNumberProgramOpcode opcode = PandaLineNumberProgramOpcode.getType(value);
            if (opcode == null){
                //handle Special opcode
                int code = value & 0xff;
                int adjustOpcode = code - PandaLineNumberProgramOpcode.OPCODE_BASE;
                int pcOffset = adjustOpcode / PandaLineNumberProgramOpcode.LINE_RANGE;
                int lineOffset = adjustOpcode % PandaLineNumberProgramOpcode.LINE_RANGE + PandaLineNumberProgramOpcode.LINE_BASE;
                index = index.plus(UnsignedInteger.ONE);
                continue;
            }
            index = index.plus(UnsignedInteger.ONE);
            switch (opcode){
                case END_SEQUENCE : {
                    if (constPoolIndex.intValue() != constPool.length){
                        throw new PandaParseException("debug info parse error!");
                    }
                    return;
                }
                case ADVANCE_PC : {
                    constPoolLeb128.decode2UnsignedInteger(constPoolIndex.intValue());
                    int pcDiff = constPoolLeb128.getSignedResult();
                    constPoolIndex = constPoolIndex.plus(constPoolLeb128.getSize());
                    break;
                }
                case ADVANCE_LINE : {
                    constPoolLeb128.decode2Integer(constPoolIndex.intValue());
                    UnsignedInteger pc_diff = constPoolLeb128.getUnsignedInteger();
                    constPoolIndex = constPoolIndex.plus(constPoolLeb128.getSize());
                    break;
                }
                case START_LOCAL : {
                    System.out.println("START_LOCAL not implemented yet!");
                    break;
                }
                case START_LOCAL_EXTENDED : {
                    programLeb128.decode2Integer(offset.plus(index).intValue());
                    int registerNumber = programLeb128.getSignedResult();
                    index = index.plus(programLeb128.getSize());

                    constPoolLeb128.decode2UnsignedInteger(constPoolIndex.intValue());
                    UnsignedInteger nameIndex = constPoolLeb128.getUnsignedInteger();
                    PandaString name = PandaString.create(pandaFile,nameIndex);
                    constPoolIndex = constPoolIndex.plus(constPoolLeb128.getSize());

                    constPoolLeb128.decode2UnsignedInteger(constPoolIndex.intValue());
                    UnsignedInteger typeIndex = constPoolLeb128.getUnsignedInteger();
                    PandaString type = PandaString.create(pandaFile,typeIndex);
                    constPoolIndex = constPoolIndex.plus(constPoolLeb128.getSize());

                    constPoolLeb128.decode2UnsignedInteger(constPoolIndex.intValue());
                    UnsignedInteger typeSignatureIndex = constPoolLeb128.getUnsignedInteger();
                    PandaString typeSignature = PandaString.create(pandaFile,typeSignatureIndex);
                    constPoolIndex = constPoolIndex.plus(constPoolLeb128.getSize());
                    break;
                }
                case END_LOCAL : {
                    programLeb128.decode2Integer(offset.plus(index).intValue());
                    int registerNumber = programLeb128.getSignedResult();
                    index = index.plus(programLeb128.getSize());
                    break;
                }
                case RESTART_LOCAL : {
                    System.out.println("RESTART_LOCAL not implemented yet!");
                    break;
                }
                case SET_PROLOGUE_END : {
                    System.out.println("SET_PROLOGUE_END not implemented yet!");
                    break;
                }
                case SET_EPILOGUE_BEGIN : {
                    System.out.println("SET_EPILOGUE_BEGIN not implemented yet!");
                    break;
                }
                case SET_FILE : {
                    constPoolLeb128.decode2UnsignedInteger(constPoolIndex.intValue());
                    UnsignedInteger fileNameOffset = constPoolLeb128.getUnsignedInteger();
                    fileName = PandaString.create(pandaFile,fileNameOffset);
                    constPoolIndex = constPoolIndex.plus(constPoolLeb128.getSize());
                    break;
                }
                case SET_SOURCE_CODE : {
                    constPoolLeb128.decode2UnsignedInteger(constPoolIndex.intValue());
                    UnsignedInteger sourceCodeIndex = constPoolLeb128.getUnsignedInteger();
                    sourceCode = PandaString.create(pandaFile,sourceCodeIndex);
                    constPoolIndex = constPoolIndex.plus(constPoolLeb128.getSize());
                    break;
                }
                case SET_COLUMN : {
                    constPoolLeb128.decode2UnsignedInteger(constPoolIndex.intValue());
                    column = constPoolLeb128.getUnsignedInteger();
                    constPoolIndex = constPoolIndex.plus(constPoolLeb128.getSize());
                    break;
                }
            }
        }

    }

    public static PandaLineNumberProgram create(PandaFile pandaFile, UnsignedInteger offset, byte[] constPool){
        Offset offset1 = pandaFile.resolveOffset(offset);
        if (offset1 instanceof PandaLineNumberProgram) return (PandaLineNumberProgram) offset1;
        else{
            PandaLineNumberProgram pandaLineNumberProgram = new PandaLineNumberProgram(pandaFile,offset,constPool);
            pandaFile.addOffset(offset,pandaLineNumberProgram);
            return pandaLineNumberProgram;
        }
    }

    @Override
    public byte[] toByteArray() {
        ByteBuffer buffer = ByteBuffer.allocate(opCodeList.size()).order(ByteOrder.LITTLE_ENDIAN);
        for (Byte b : opCodeList) {
            buffer.put(b);
        }
        return buffer.array();
    }
}
