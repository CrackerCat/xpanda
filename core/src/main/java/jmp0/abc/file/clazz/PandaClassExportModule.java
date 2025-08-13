package jmp0.abc.file.clazz;

import jmp0.abc.PandaParseException;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
@Getter
public final class PandaClassExportModule {
    public enum TYPE{
        LOCAL,
        INDIRECT,
        ALL
    }
    private final String path;
    @Setter private String localName;
    private final String exportName;
    private final TYPE type;

    public PandaClassExportModule(String path, String localName, String exportName,TYPE type){
        this.path = path;
        this.localName = localName;
        if (localName != null && localName.equals("*default*")){
            this.localName = "panda_jmp0_reserved_export_default";
        }
        this.exportName = exportName;
        this.type = type;
    }

    private boolean isStarExport(){
        return (this.path != null) && (this.localName == null) && (this.exportName == null);
    }

    private boolean isIndirectExport(){
        return (this.path != null) && (this.localName != null) && (this.exportName != null);
    }

    private boolean isLocalExport(){
        return (this.path == null) && (this.localName != null) && (this.exportName != null);
    }

    @SneakyThrows
    @Override
    public String toString() {
        if (isLocalExport()){
            if (this.exportName.equals("default")){
                return "export default "+ this.localName;
            } else if (this.exportName.equals(this.localName)) {
                return String.format("export {%s}",this.exportName);
            }
            return String.format("export {%s as %s}",this.localName,this.exportName);
        }else if (isIndirectExport()){
            if (this.exportName.equals(this.localName)) {
                return String.format("export {%s} from \"%s\"",this.exportName,this.path);
            }
            return String.format("export {%s as %s} from \"%s\"",this.localName,this.exportName,this.path);
        }else if (isStarExport()){
            return String.format("export * from \"%s\"",this.path);
        }
        throw new PandaParseException("PandaClassExportModule unknown export type!");
    }
}
