package jmp0.abc.file.clazz;

import com.google.common.primitives.UnsignedInteger;
import jmp0.abc.file.IPandaCanOutput;
import jmp0.abc.file.PandaFile;
import jmp0.abc.file.PandaFileUtils;
import jmp0.abc.file.desc.Offset;
import jmp0.abc.file.desc.PandaString;
import lombok.Getter;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.LinkedList;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */

public final class PandaClassModuleData extends Offset implements IPandaCanOutput {
    private final PandaClass pandaClass;
    private UnsignedInteger index = UnsignedInteger.valueOf(4);
    @Getter private PandaClassModulePath[] requestedModules = null;
    @Getter private final PandaClassImportModule[] imports;
    @Getter private final PandaClassExportModule[] exports;
    private PandaClassModuleData(PandaFile pandaFile, UnsignedInteger offset, PandaClass pandaClass) {
        super(pandaFile, offset);
        this.pandaClass = pandaClass;
        this.imports = parseImport(pandaFile);
        this.exports = parseExport(pandaFile);
    }

    private PandaClassImportModule[] parseImport(PandaFile pandaFile){
        UnsignedInteger size = PandaFileUtils.bytes2UnsignedInteger(pandaFile.getData(),offset.plus(index).intValue());
        index = index.plus(UnsignedInteger.valueOf(4));
        LinkedList<PandaClassModulePath> modules = new LinkedList<>();
        LinkedList<PandaClassImportModule> importLinkedList = new LinkedList<>();
        for (int i = 0; i < size.intValue(); i++) {
            modules.add(PandaClassModulePath.create(pandaFile,
                    PandaFileUtils.bytes2UnsignedInteger(getPandaFile().getData(),offset.plus(index).intValue())));
            index = index.plus(UnsignedInteger.valueOf(4));
        }
        this.requestedModules = modules.toArray(new PandaClassModulePath[]{});
        UnsignedInteger importEntrySize = PandaFileUtils.bytes2UnsignedInteger(pandaFile.getData(),offset.plus(index).intValue());
        index = index.plus(UnsignedInteger.valueOf(4));
        for (int i = 0; i < importEntrySize.intValue(); i++) {
            PandaString localName = PandaString.create(pandaFile,
                    PandaFileUtils.bytes2UnsignedInteger(getPandaFile().getData(),offset.plus(index).intValue()));
            index = index.plus(UnsignedInteger.valueOf(4));
            PandaString importName = PandaString.create(pandaFile,
                    PandaFileUtils.bytes2UnsignedInteger(getPandaFile().getData(),offset.plus(index).intValue()));
            index = index.plus(UnsignedInteger.valueOf(4));
            int off = PandaFileUtils.bytes2Uint16(getPandaFile().getData(),offset.plus(index).intValue());
            index = index.plus(UnsignedInteger.valueOf(2));
            importLinkedList.add(new PandaClassImportModule(modules.get(off).toString(),localName.getContent(),importName.getContent()));
        }
        UnsignedInteger namespaceImportNum = PandaFileUtils.bytes2UnsignedInteger(getPandaFile().getData(),offset.plus(index).intValue());
        index = index.plus(UnsignedInteger.valueOf(4));
        UnsignedInteger totalSize = importEntrySize.plus(namespaceImportNum);
        for (int i = importEntrySize.intValue(); i < totalSize.intValue(); i++) {
            PandaString localName = PandaString.create(pandaFile,
                    PandaFileUtils.bytes2UnsignedInteger(getPandaFile().getData(),offset.plus(index).intValue()));
            index = index.plus(UnsignedInteger.valueOf(4));
            int off = PandaFileUtils.bytes2Uint16(getPandaFile().getData(),offset.plus(index).intValue());
            index = index.plus(UnsignedInteger.valueOf(2));
            importLinkedList.add(new PandaClassImportModule(modules.get(off).toString(),localName.getContent(),null));
        }
        return importLinkedList.toArray(new PandaClassImportModule[]{});
    }

    private PandaClassExportModule[] parseExport(PandaFile pandaFile){
        UnsignedInteger localExportNum = PandaFileUtils.bytes2UnsignedInteger(getPandaFile().getData(),offset.plus(index).intValue());
        index = index.plus(UnsignedInteger.valueOf(4));
        LinkedList<PandaClassExportModule> exportModuleLinkedList = new LinkedList<>();
        for (int i = 0; i < localExportNum.intValue(); i++) {
            PandaString localName = PandaString.create(pandaFile,
                    PandaFileUtils.bytes2UnsignedInteger(getPandaFile().getData(),offset.plus(index).intValue()));
            index = index.plus(UnsignedInteger.valueOf(4));
            PandaString exportName = PandaString.create(pandaFile,
                    PandaFileUtils.bytes2UnsignedInteger(getPandaFile().getData(),offset.plus(index).intValue()));
            index = index.plus(UnsignedInteger.valueOf(4));
            exportModuleLinkedList.add(new PandaClassExportModule(null,localName.getContent(),exportName.getContent(), PandaClassExportModule.TYPE.LOCAL));
        }
        UnsignedInteger indirectExportNum = PandaFileUtils.bytes2UnsignedInteger(getPandaFile().getData(),offset.plus(index).intValue());
        index = index.plus(UnsignedInteger.valueOf(4));
        for (int i = 0; i < indirectExportNum.intValue(); i++) {
            PandaString localName = PandaString.create(pandaFile,
                    PandaFileUtils.bytes2UnsignedInteger(getPandaFile().getData(),offset.plus(index).intValue()));
            index = index.plus(UnsignedInteger.valueOf(4));
            PandaString exportName = PandaString.create(pandaFile,
                    PandaFileUtils.bytes2UnsignedInteger(getPandaFile().getData(),offset.plus(index).intValue()));
            index = index.plus(UnsignedInteger.valueOf(4));
            int off = PandaFileUtils.bytes2Uint16(getPandaFile().getData(),offset.plus(index).intValue());
            index = index.plus(UnsignedInteger.valueOf(2));
            exportModuleLinkedList.add(new PandaClassExportModule(this.requestedModules[off].toString(),localName.getContent(),exportName.getContent(),PandaClassExportModule.TYPE.INDIRECT));
        }
        UnsignedInteger starExportNum = PandaFileUtils.bytes2UnsignedInteger(getPandaFile().getData(),offset.plus(index).intValue());
        index = index.plus(UnsignedInteger.valueOf(4));
        for (int i = 0; i < starExportNum.intValue(); i++) {
            int off = PandaFileUtils.bytes2Uint16(getPandaFile().getData(),offset.plus(index).intValue());
            index = index.plus(UnsignedInteger.valueOf(2));
            exportModuleLinkedList.add(new PandaClassExportModule(this.requestedModules[off].toString(),null,null,PandaClassExportModule.TYPE.ALL));
        }
        return exportModuleLinkedList.toArray(new PandaClassExportModule[]{});
    }

    public static PandaClassModuleData create(PandaFile pandaFile, UnsignedInteger offset, PandaClass pandaClass) {
        Offset offset1 = pandaFile.resolveOffset(offset);
        if (offset1 instanceof PandaClassModuleData) return (PandaClassModuleData) offset1;
        else {
            PandaClassModuleData pandaClassModuleData = new PandaClassModuleData(pandaFile, offset,pandaClass);
            pandaFile.addOffset(offset, pandaClassModuleData);
            return pandaClassModuleData;
        }
    }

    public PandaClass getParent(){
        return this.pandaClass;
    }

    public PandaClassImportModule getPandaClassImportModuleByPath(PandaClassModulePath path){
        for (PandaClassImportModule anImport : this.imports) {
            if (anImport.getPath().equals(path.toString())) return anImport;
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (PandaClassImportModule anImport : imports) {
            builder.append(anImport).append('\n');
        }
        for (PandaClassExportModule anImport : exports) {
            builder.append(anImport).append('\n');
        }
        return builder.toString();
    }

    @Override
    public byte[] toByteArray() {
        ByteBuffer buffer = ByteBuffer.allocate(index.intValue()).order(ByteOrder.LITTLE_ENDIAN);
        //todo not implemented yet!
        return buffer.array();
    }
}
