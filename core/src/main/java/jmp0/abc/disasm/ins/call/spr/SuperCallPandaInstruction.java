package jmp0.abc.disasm.ins.call.spr;

import jmp0.abc.PandaParseException;
import jmp0.abc.disasm.ins.PandaInstruction;
import jmp0.abc.disasm.param.PandaInstructionIMM;
import jmp0.abc.disasm.param.PandaInstructionVReg;
import jmp0.abc.disasm.types.PandaOPCode;
import jmp0.abc.file.method.PandaMethod;
import lombok.Getter;
import lombok.SneakyThrows;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
@Getter
public abstract class SuperCallPandaInstruction extends PandaInstruction {
    private final PandaInstructionVReg[] callParams;
    @SneakyThrows
    public SuperCallPandaInstruction(int pc, PandaOPCode opCode, byte[] param, PandaMethod pandaMethod) {
        super(pc, opCode, param, pandaMethod);
        Number argNum;
        if (opCode == PandaOPCode.SUPERCALLTHISRANGE_IMM8_IMM8_V8 || opCode == PandaOPCode.SUPERCALLARROWRANGE_IMM8_IMM8_V8){
            argNum = ((PandaInstructionIMM)(this.getParams()[1])).getImm();
            this.callParams = getVRegs(argNum.intValue(),(PandaInstructionVReg)(this.getParams()[2]),pandaMethod);
            return;
        }else if (opCode == PandaOPCode.WIDE_SUPERCALLTHISRANGE_PREF_IMM16_V8 || opCode == PandaOPCode.WIDE_SUPERCALLARROWRANGE_PREF_IMM16_V8){
            argNum = ((PandaInstructionIMM)(this.getParams()[0])).getImm();
            this.callParams = getVRegs(argNum.intValue(),(PandaInstructionVReg)(this.getParams()[1]),pandaMethod);
            return;
        }
        throw new PandaParseException(opCode + " not recognized");
    }

    private PandaInstructionVReg[] getVRegs(int size,PandaInstructionVReg vReg,PandaMethod method){
        PandaInstructionVReg[] rets = new PandaInstructionVReg[size];
        for (int i = 0; i < size; i++) {
            rets[i] = new PandaInstructionVReg((short) ((vReg).getIndex()+i), PandaInstructionVReg.TYPE.VREG,method);
        }
        return rets;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(baseToString());
        builder.append("super(");
        for (int i = 0; i < callParams.length; i++) {
            builder.append(callParams[i].toString());
            if (i != callParams.length -1) builder.append(',');
        }
        builder.append(')');
        return builder.toString();
    }
}
