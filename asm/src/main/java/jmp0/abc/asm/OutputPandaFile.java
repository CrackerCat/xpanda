package jmp0.abc.asm;

import com.google.common.primitives.UnsignedInteger;
import jmp0.abc.file.IPandaCanOutput;
import jmp0.abc.file.PandaFile;
import jmp0.abc.file.PandaFileUtils;
import jmp0.abc.file.anno.PandaAnnotation;
import jmp0.abc.file.clazz.PandaClass;
import jmp0.abc.file.desc.Header;
import jmp0.abc.file.desc.IndexHeader;
import jmp0.abc.file.desc.Offset;
import jmp0.abc.file.desc.PandaString;
import jmp0.abc.file.literal.PandaLiteralArray;
import jmp0.abc.file.method.PandaMethodCode;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class OutputPandaFile {
    private final PandaFile pandaFile;
    private final Header pandaHeader;
    private final IndexHeader[] indexHeaders;
    private final LinkedList<Offset> indexTable;
    private final LinkedList<PandaString> stringTable;
    private PandaString fileName;
    private PandaString entryName;
    private PandaString SlotNumber;

    public OutputPandaFile(PandaFile pandaFile){
        this.pandaFile = pandaFile;
        this.pandaFile.parseAssembly();
        this.pandaHeader = pandaFile.getHeader();
        this.indexHeaders = pandaFile.getIndexHeaders();
        this.indexTable = parseIndexTable();
        this.stringTable = parseStringTable();
//        parse();
    }

    private LinkedList<Offset> parseIndexTable(){
        LinkedList<Offset> table = new LinkedList<>();
        int begin = Header.SIZE;
        int end = pandaHeader.getIndexSectionOff().intValue();
        int size = (end - begin) / 4;
        for (int i = 0; i < size; i++) {
            UnsignedInteger offset = PandaFileUtils.bytes2UnsignedInteger(pandaFile.getData(),begin + i * 4);
            table.add(pandaFile.resolveOffset(offset));
        }
        return table;
    }

    private LinkedList<PandaString> parseStringTable(){
        LinkedList<PandaString> table = new LinkedList<>();
        int index = indexHeaders[0].getStart().intValue();
        while (true) {
            Offset offset = pandaFile.resolveOffset(UnsignedInteger.valueOf(index));
            if (offset instanceof PandaClass && !offset.isExternal()) {
                break;
            }
            else if (offset instanceof PandaString){
                table.add((PandaString) offset);
                index += ((PandaString) offset).getSize().intValue();
                //compat foreign classes
            } else if (offset == null){
                PandaString string = PandaString.create(pandaFile,UnsignedInteger.valueOf(index));
                table.add(string);
                index += string.getSize().intValue();
            }else break;
        }
        return table;
    }

    public static String bytes2String(byte[] a) {
        if (a == null)
            return "null";
        int iMax = a.length - 1;
        if (iMax == -1)
            return "[]";

        StringBuilder b = new StringBuilder();
        b.append('[');
        for (int i = 0; ; i++) {
            b.append(Long.toString(Integer.toUnsignedLong(a[i]) & 0xFF,16).toUpperCase());
            if (i == iMax)
                return b.append(']').toString();
            b.append(", ");
        }
    }

    public void compare(byte[] a,byte[] b,int pos){
        assert Arrays.compare(a,0,pos,b,0,pos) == 0;
    }
    public void parse() {
        ByteBuffer buffer = ByteBuffer.allocate(this.pandaFile.getData().length).order(ByteOrder.LITTLE_ENDIAN);
        //put header
        byte[] bs = this.pandaHeader.toByteArray();
        buffer.put(bs);
        //put index-table
        for (Offset offset : indexTable) {
            buffer.putInt(offset.getOffset().intValue());
        }
        //put indexHeader
        for (IndexHeader indexHeader : pandaFile.getIndexHeaders()) {
            bs = indexHeader.toByteArray();
            buffer.put(bs);
        }
        //put string-table
        for (PandaString string : stringTable) {
            buffer.put(string.toByteArray());
        }

        Offset offset = pandaFile.resolveOffset(UnsignedInteger.valueOf(buffer.position()));
        while (offset instanceof IPandaCanOutput){
            buffer.put(((IPandaCanOutput) offset).toByteArray());
            offset = pandaFile.resolveOffset(UnsignedInteger.valueOf(buffer.position()));
            this.compare(buffer.array(),pandaFile.getData(),buffer.position());
        }
        //put lnp header
        for (Offset lnpOffset : pandaFile.getLnpOffsets()) {
            buffer.putInt(lnpOffset.getOffset().intValue());
        }
        this.compare(buffer.array(),pandaFile.getData(),buffer.position());
    }

    public void test(){
        for (Offset value : pandaFile.getOffsets().values()) {
            if (value instanceof IPandaCanOutput){
                ((IPandaCanOutput) value).toByteArray();
            }else {
                System.out.println(value.getClass().getName());
            }
        }
    }
}
