package jmp0.abc.disasm.param;

import com.google.common.primitives.UnsignedInteger;
import jmp0.abc.PandaParseException;
import jmp0.abc.file.method.PandaMethod;
import lombok.Getter;
import lombok.SneakyThrows;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class PandaInstructionVReg implements IPandaInstructionParam{
    public enum TYPE{
        VREG,
        PARAM
    }
    @Getter private final short index;
    @Getter private final TYPE type;
    private final PandaMethod method;
    @SneakyThrows
    public PandaInstructionVReg(short index, PandaMethod pandaMethod){
        UnsignedInteger numParams = pandaMethod.getMethodCode().getNumArgs();
        UnsignedInteger numVRegs = pandaMethod.getMethodCode().getNumVregs();
        this.method = pandaMethod;
        if (index > numParams.plus(numVRegs).intValue())
            throw new PandaParseException("index over size!");
        if (index >= numVRegs.intValue()){
            this.index = (short) (index - numVRegs.intValue());
            this.type = TYPE.PARAM;
        }else {
            this.index = index;
            this.type = TYPE.VREG;
        }
    }
    public PandaInstructionVReg(short index,TYPE type,PandaMethod method){
        this.index = index;
        this.type = type;
        this.method = method;
    }

    @Override
    public String toString() {
        if (this.type == TYPE.PARAM){
            //func_name new.target this
            if (index < 3){
                return "panda_jmp0_reserved_param" + "_p" + index;
            }
            return "p" + (index - 3);
        }
        return "v" + index;
    }
}
