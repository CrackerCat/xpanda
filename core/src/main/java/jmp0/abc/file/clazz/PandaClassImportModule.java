package jmp0.abc.file.clazz;

import lombok.Getter;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
@Getter
public final class PandaClassImportModule {
    public enum TYPE{
        NAMESPACE,
        DEFAULT,
        SPECIFIER,
        SAME_SPECIFIER
    }
    private final String path;
    private final String localName;
    private final String importName;
    private final TYPE type;

    public PandaClassImportModule(String path, String localName, String importName){
        this.path = path;
        this.localName = localName;
        this.importName = importName;
        if (importName == null){
            this.type = TYPE.NAMESPACE;
            return;
        }else if (importName.equals("default")){
            this.type = TYPE.DEFAULT;
            return;
        } else if (importName.equals(localName)){
            this.type = TYPE.SAME_SPECIFIER;
            return;
        }
        this.type = TYPE.SPECIFIER;
    }

    @Override
    public String toString() {
        switch (type){
            case NAMESPACE:
                return String.format("import * as %s from \"%s\"",localName,path);
            case DEFAULT:
                return String.format("import %s from \"%s\"",localName,path);
            case SAME_SPECIFIER:
                return String.format("import {%s} from \"%s\"",importName,path);
            case SPECIFIER:
                return String.format("import {%s as %s} from \"%s\"",importName,localName,path);
        }
        return "";
    }
}
