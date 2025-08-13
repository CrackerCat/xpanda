package jmp0.abc.file.type;

import com.google.common.primitives.UnsignedInteger;
import jmp0.abc.file.clazz.PandaClass;
import jmp0.abc.file.desc.Header;
import lombok.Getter;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */

@Getter
public final class PandaType {
    private final PandaClass pandaClass;
    private final boolean primitive;
    private PandaRawType rawType;
    public PandaType(PandaClass pandaClass,boolean isField){
        this.pandaClass = pandaClass;
        this.primitive = pandaClass.getOffset().compareTo(UnsignedInteger.valueOf(Header.SIZE)) <= 0;
        if (this.primitive){
            if (isField){
                this.rawType = PandaRawType.getType(pandaClass.getOffset().byteValue()).toFieldType();
            }else {
                this.rawType = PandaRawType.getType(pandaClass.getOffset().byteValue());
            }

        }
    }

}
