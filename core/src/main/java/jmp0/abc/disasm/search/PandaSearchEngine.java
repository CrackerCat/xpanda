package jmp0.abc.disasm.search;

import jmp0.abc.file.PandaFile;
import jmp0.abc.file.clazz.PandaClass;
import jmp0.abc.file.clazz.PandaClassImportModule;
import jmp0.abc.file.clazz.PandaClassModuleData;
import jmp0.abc.file.clazz.PandaClassModulePath;
import jmp0.abc.file.desc.IndexHeader;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class PandaSearchEngine {
    private final PandaFile pandaFile;
    private final HashMap<PandaClass,String> importPaths = new HashMap<>();

    public PandaSearchEngine(PandaFile pandaFile){
        this.pandaFile = pandaFile;
        buildImportIndex();
    }

    private void buildImportIndex(){
        for (IndexHeader indexHeader : pandaFile.getIndexHeaders()) {
            for (PandaClass pandaClass : indexHeader.getPandaClasses()) {
                PandaClassModuleData data = pandaClass.getPandaClassModuleData();
                if (data == null) continue;
                StringBuilder builder = new StringBuilder();
                for (PandaClassImportModule module : data.getImports() ) {
                    String path = module.getPath();
                    if (path == null) continue;
                    builder.append(path.toLowerCase());
                }
                importPaths.put(pandaClass,builder.toString());
            }
        }
    }

    public PandaClass[] searchImports(String content){
        String contentLower = content.toLowerCase();
        LinkedList<PandaClass> result = new LinkedList<>();
        importPaths.forEach((k,v)->{
            if (v.contains(contentLower)){
                result.add(k);
            }
        });
        return result.toArray(new PandaClass[0]);
    }

}
