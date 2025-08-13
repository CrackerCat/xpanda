package jmp0.abc.disasm.ins.call;

import jmp0.abc.disasm.ins.PandaInstruction;
import jmp0.abc.disasm.param.IPandaInstructionParam;
import jmp0.abc.disasm.types.PandaOPCode;
import jmp0.abc.file.method.PandaMethod;
import lombok.Getter;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
@Getter
public abstract class CallPandaInstruction extends PandaInstruction {
    protected final IPandaInstructionParam func;
    protected IPandaInstructionParam thisObj;
    protected IPandaInstructionParam[] callParams;
    protected boolean apply = false;

    public CallPandaInstruction(int pc, PandaOPCode opCode, byte[] param, PandaMethod method) {
        super(pc, opCode, param, method);
        this.func = null;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(super.baseToString());
        String thisObjStr;
        if (this.thisObj == null) thisObjStr = "null";
        else thisObjStr = this.thisObj.toString();
        builder.append("acc").append(".call(").append(thisObjStr);
        if (this.apply){
            builder.append(",...").append(callParams[0]);
        }else {
            if (this.callParams.length != 0 ) builder.append(',');
            for (int i = 0; i < callParams.length ; i++) {
                builder.append(this.callParams[i]);
                if (i != callParams.length-1) builder.append(',');
            }
        }
        builder.append(')');
        return builder.toString();
    }
}

