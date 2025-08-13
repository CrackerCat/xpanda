package jmp0.abc.res;

import jmp0.abc.PandaParseException;
import jmp0.abc.res.limitkey.PandaResourceLimitKeyParam;
import jmp0.abc.res.types.PandaResourceResType;
import lombok.Getter;
import lombok.Setter;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
@Getter
public final class PandaResourceItem {
    private final String fileName;
    private final PandaResourceLimitKeyParam[] keyParams;
    private final PandaResourceResType resType;
    @Setter
    private String limitKey = "";
    @Setter
    private byte[] values;
    private Object obj;

    public PandaResourceItem(String fileName, PandaResourceLimitKeyParam[] keyParams,PandaResourceResType resType){
        this.fileName = fileName;
        this.keyParams = keyParams;
        this.resType = resType;
    }

    public void parseValues() throws PandaParseException {
        switch (resType){
            case STRING:
            case INTEGER:
            case BOOLEAN:
            case COLOR:
            case FLOAT:
            case MEDIA:
            case PROF:{
                obj = PandaResourceUtils.bytes2String(values);
                break;
            }
            case PLURAL:{
                obj = PandaResourceUtils.bytes2StringArray(values);
                break;
            }
            default: {
                throw new PandaParseException(resType + " not implemented now!");
            }
        }
    }

    @Override
    public String toString() {
        return "PandaResourceItem [fileName=" + fileName +  ", limitKey=" + limitKey + ", resType=" + resType + ", obj=" + obj + "]";
    }
}
