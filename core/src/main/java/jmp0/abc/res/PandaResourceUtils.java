package jmp0.abc.res;

import com.google.common.primitives.UnsignedInteger;
import jmp0.abc.res.limitkey.PandaResourceLimitKeyParam;
import jmp0.abc.res.types.PandaResourceDeviceType;
import jmp0.abc.res.types.PandaResourceKeyType;
import jmp0.abc.res.types.PandaResourceResolutionType;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class PandaResourceUtils {

    public static String bytes2String(byte[] bytes) {
        int i = bytes.length -1;
        for (; i >= 0 ; i--) {
            if (bytes[i] != 0) break;
        }
        return new String(bytes, 0, i + 1, StandardCharsets.UTF_8);
    }

    public static String[] bytes2StringArray(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);
        LinkedList<String> strings = new LinkedList<>();
        int i = 0;
        while (i < bytes.length - 1) {
            int size = buffer.getShort(i);
            i += 2;
            strings.add(new String(bytes, i, size, StandardCharsets.UTF_8));
            i += size + 1;
        }
        return strings.toArray(new String[0]);
    }

    private static String getLocaleLimitKey(PandaResourceLimitKeyParam param){
        UnsignedInteger value = param.getValue();
        byte[] result = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(value.intValue()).array();
        return new StringBuffer(bytes2String(result)).reverse().toString();
    }

    private static String getKeyParamValue(PandaResourceLimitKeyParam param){
        String val = "";
        switch (param.getKeyType()){
            case ORIENTATION:{
                val = param.getValue().compareTo(UnsignedInteger.ZERO) == 0 ? "vertical" : "horizontal";
                break;
            }
            case NIGHTMODE:{
                val = param.getValue().compareTo(UnsignedInteger.ZERO) == 0 ? "dark" : "light";
                break;
            }
            case DEVICETYPE:{
                PandaResourceDeviceType deviceType = PandaResourceDeviceType.resolve(param.getValue().intValue());
                val = deviceType.getName();
                break;
            }
            case RESOLUTION:{
                PandaResourceResolutionType resolutionType = PandaResourceResolutionType.resolve(param.getValue().intValue());
                val = resolutionType.getName();
                break;
            }
            case LANGUAGE:
            case REGION: {
                val = getLocaleLimitKey(param);
                break;
            }
            default:{
                UnsignedInteger value = param.getValue();
                byte[] result = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(value.intValue()).array();
                val = bytes2String(result);
                break;
            }
        }
        return val;
    }

    public static String parseKeyParam(PandaResourceLimitKeyParam[] params){
        if (params.length == 0){
            return "base";
        }
        String result = "";
        for (PandaResourceLimitKeyParam param : params) {
            String limitKey = getKeyParamValue(param);
            if (limitKey.isEmpty()){
                continue;
            }
            if (param.getKeyType() == PandaResourceKeyType.MCC){
                limitKey = "mcc" + limitKey;
            }
            if (param.getKeyType() == PandaResourceKeyType.MNC){
                limitKey = "mnc" + limitKey;
            }
            if (param.getKeyType() == PandaResourceKeyType.REGION || param.getKeyType() == PandaResourceKeyType.MNC){
                result = result + "_" + limitKey;
            }else {
                result = result + "-" + limitKey;
            }
        }
        if (!result.isEmpty()) {
            result = result.substring(1);
        }

        return result;
    }
}
