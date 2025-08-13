package jmp0.abc.file.clazz;

import com.google.common.primitives.UnsignedInteger;
import jmp0.abc.file.PandaFile;
import jmp0.abc.file.desc.Offset;
import jmp0.abc.file.desc.PandaString;
import lombok.Getter;
import lombok.SneakyThrows;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
@Getter
public final class PandaClassModulePath extends PandaString {
    enum TYPE{
        BUNDLE("@bundle:"),
        MODULE("@module:"),
        PACKAGE("@package:"),
        NATIVE_MODULE("@native:"),
        NAPI_OHOS("@ohos:"),
        NAPI_HMS("@hms:"),
        NAPI_APP("@app:"),
        NORMALIZED("@normalized:"),
        UNKNOWN("");
        private final String prefix;

        TYPE(String prefix) {
            this.prefix = prefix;
        }
    }
    private TYPE type;
    private String realPath;


    public PandaClassModulePath(PandaFile pandaFile, UnsignedInteger offset) {
        super(pandaFile,offset);
        getTypeWithRawPath();
    }

    @SneakyThrows
    private void getTypeWithRawPath(){
        for (TYPE value : TYPE.values()) {
            if (content.startsWith(value.prefix)){
                this.type = value;
                this.realPath = content.substring(value.prefix.length());
                return;
            }
        }
        this.type = TYPE.UNKNOWN;
        this.realPath = content;
    }

    public static PandaClassModulePath create(PandaFile pandaFile, UnsignedInteger offset){
        Offset offset1 = pandaFile.resolveOffset(offset);
        if (offset1 instanceof PandaClassModulePath) return (PandaClassModulePath) offset1;
        else{
            PandaClassModulePath pandaString = new PandaClassModulePath(pandaFile,offset);
            pandaFile.addOffset(offset,pandaString);
            return pandaString;
        }
    }

    @Override
    public String toString() {
        return content;
    }
}
