package jmp0.abc.file;

import com.google.common.primitives.UnsignedInteger;
import jmp0.abc.PandaParseException;
import jmp0.abc.codec.Leb128;
import jmp0.abc.disasm.PandaDisAssemblerFakeCodeHelper;
import jmp0.abc.disasm.block.PandaIRCFG;
import jmp0.abc.file.clazz.PandaClass;
import jmp0.abc.file.desc.Header;
import jmp0.abc.file.desc.IndexHeader;
import jmp0.abc.file.desc.PandaString;
import jmp0.abc.file.literal.PandaLiteralArray;
import jmp0.abc.file.desc.Offset;
import lombok.Getter;
import lombok.ToString;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.zip.Adler32;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
@Getter
@ToString
final public class PandaFile {
    public static final String ENTRY_FUNCTION_MAIN = "func_main_0";
    private final byte[] data;
    private final Leb128 pandaFileLeb128;
    private final Header header;
    private final IndexHeader[] indexHeaders;
    private final HashMap<UnsignedInteger,Offset> offsets = new HashMap<>();
    private PandaLiteralArray[] pandaLiteralArrays;
    private final Offset[] lnpOffsets;
    private final PandaClass[] foreignPandaClasses;

    public PandaFile(String path) throws IOException, PandaParseException {
        this(new FileInputStream(path));
    }

    public PandaFile(InputStream inputStream) throws PandaParseException, IOException {
        //parse Header
        this.data = inputStream.readAllBytes();
        this.pandaFileLeb128 = new Leb128(data);
        this.header = new Header(this.data,0);

        //check checksum
        int checksumOffset = 12;
        Adler32 adler32 = new Adler32();
        adler32.update(data,checksumOffset,data.length - checksumOffset);
        UnsignedInteger calcCheckSum = UnsignedInteger.valueOf(adler32.getValue() & 0xfffffff);
        if (this.header.getChecksum().compareTo(calcCheckSum) != 0) {
            throw new PandaParseException(String.format("Checksum check failed,the file may be encrypted. calcChecksum=%d fileChecksum=%d", calcCheckSum.intValue(),this.header.getChecksum().intValue()));
        }

        //parse lnpOffsets
        int lnpSize = this.header.getNumLnps().intValue();
        this.lnpOffsets = new Offset[lnpSize];
        UnsignedInteger lnpOffset = this.header.getLnpIdxOff();
        for (int i = 0; i < lnpSize; i++) {
            Offset offset = PandaFileUtils.readOffset(this,lnpOffset.plus(UnsignedInteger.valueOf(i * 4L)).intValue());
            this.lnpOffsets[i] = offset;
        }

        //parse RegionHeader
        int regionHeaderSize = this.header.getNumIndexes().intValue();
        indexHeaders = new IndexHeader[regionHeaderSize];
        for (int i = 0; i < regionHeaderSize; i++) {
            indexHeaders[i] = new IndexHeader(this,
                    this.header.getIndexSectionOff().intValue() + i * IndexHeader.SIZE );
        }
        // resolve indexHeaders
        for (IndexHeader indexHeader : indexHeaders) {
            indexHeader.resolve();
        }

        //parse foreignPandaClasses
        LinkedList<PandaClass> foreignPandaClasses = new LinkedList<>();
        UnsignedInteger foreignIndex = UnsignedInteger.ZERO;
        while (foreignIndex.compareTo(this.header.getForeignSize()) < 0) {
            PandaClass pandaClass = PandaClass.create(this,this.header.getForeignOff().plus(foreignIndex),this.getIndexHeaders()[0]);
            foreignIndex = foreignIndex.plus(pandaClass.getIndex());
            foreignPandaClasses.add(pandaClass);
        }
        this.foreignPandaClasses = foreignPandaClasses.toArray(new PandaClass[0]);

//        int literalArrayNum = this.header.getNumLiteralArrays().intValue();
        // The [literalarrays_size] & [literalarrays_idx_off] in header is set to an invalid value (0xffffffff) since version 13.0.0.0.
//        if (literalArrayNum != -1){
//            pandaLiteralArrays = new PandaLiteralArray[literalArrayNum];
//            for (int i = 0; i < literalArrayNum; i++) {
//                UnsignedInteger offset = PandaFileUtils.bytes2UnsignedInteger(this.data,this.header.getLiteralArrayIdxOff().intValue() + i*4);
//                boolean find = false;
//                for (IndexHeader indexHeader : this.indexHeaders) {
//                    if (indexHeader.moduleLiteralContain(offset)){
//                        find = true;
//                        break;
//                    }
//                }
//                //fixme not null...
//                if (find) pandaLiteralArrays[i] = null;
//                else pandaLiteralArrays[i] = PandaLiteralArray.create(this,offset);
//        }
//        }
    }

    public IndexHeader resolveIndexHeaderByOffset(Offset offset){
        if (!offset.isValid()) return null;
        for (IndexHeader indexHeader : indexHeaders) {
            if(offset.getOffset().compareTo(indexHeader.getStart()) >=0 &&
                    offset.getOffset().compareTo(indexHeader.getEnd())<0){
                return indexHeader;
            }
        }
        return null;
    }

    public Offset resolveLineNumberProgramIndex(UnsignedInteger index){
        if (index.compareTo(this.header.getNumLnps()) >= 0) return Offset.nullOffset();
        return PandaFileUtils.readOffset(this,this.header.getLnpIdxOff().plus(
                (index.times(UnsignedInteger.valueOf(4L)))).intValue());
    }

    public Offset resolveOffset(UnsignedInteger offset){
        return offsets.getOrDefault(offset, null);
    }

    public <T extends Offset> void addOffset(UnsignedInteger ptr, T offset){
        offsets.put(ptr, offset);
    }

    public void parseAssembly(){

        for (IndexHeader indexHeader : this.indexHeaders) {
            for (PandaClass pandaClass : indexHeader.getPandaClasses()) {
                pandaClass.disAssembleAllMethods();
            }
        }
    }

    public void disAssembly(IDisAssemblyCallBack callBack){
        for (IndexHeader indexHeader : this.indexHeaders) {
            for (PandaClass pandaClass : indexHeader.getPandaClasses()) {
                PandaIRCFG[] pandaIRCFGS = pandaClass.disAssembleAllMethods();
                if (pandaClass.isValid()){
                    callBack.CFGCallback(pandaClass,pandaIRCFGS);
                    callBack.fakeAssemblyCallback(pandaClass,PandaDisAssemblerFakeCodeHelper.genFakeCode(pandaClass,pandaIRCFGS));
                }
            }
        }
    }

    public String dumpAllString(){
        this.parseAssembly();
        StringBuilder sb = new StringBuilder();
        for (Offset value : this.offsets.values()) {
            if (value instanceof PandaString){
                sb.append(value.getOffset()).append("->").append(((PandaString) value).getContent()).append('\n');
            }
        }
        return sb.toString();
    }
}
