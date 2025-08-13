package jmp0.abc.disasm.ins.creaters.obj;

import jmp0.abc.disasm.ins.PandaInstruction;
import jmp0.abc.disasm.param.PandaInstructionID;
import jmp0.abc.disasm.param.PandaInstructionIMM;
import jmp0.abc.disasm.param.PandaInstructionVReg;
import jmp0.abc.disasm.types.PandaOPCode;
import jmp0.abc.file.desc.Offset;
import jmp0.abc.file.desc.PandaString;
import jmp0.abc.file.literal.PandaLiteralArray;
import jmp0.abc.file.method.PandaMethod;
import lombok.Getter;

import java.util.HashMap;
import java.util.LinkedHashMap;

import static jmp0.abc.disasm.types.PandaOPCode.CREATEEMPTYOBJECT_NONE;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
@Getter
public abstract class CreateObjectPandaInstruction extends PandaInstruction {
    private final HashMap<String, Offset> objMap = new LinkedHashMap<>();
    private boolean isExcludeObject = false;
    private PandaInstructionIMM excludeIndex;
    private PandaInstructionVReg excludeObj;
    private PandaInstructionVReg excludeFormObj;
    public CreateObjectPandaInstruction(int pc, PandaOPCode opCode, byte[] param, PandaMethod pandaMethod) {
        super(pc, opCode, param, pandaMethod);
        if (opCode == CREATEEMPTYOBJECT_NONE) return;
        if (opCode == PandaOPCode.CREATEOBJECTWITHEXCLUDEDKEYS_IMM8_V8_V8 || opCode == PandaOPCode.WIDE_CREATEOBJECTWITHEXCLUDEDKEYS_PREF_IMM16_V8_V8){
            this.isExcludeObject = true;
            this.excludeIndex = (PandaInstructionIMM) getParams()[0];
            this.excludeFormObj = (PandaInstructionVReg) getParams()[1];
            this.excludeObj = (PandaInstructionVReg) getParams()[2];
            return;
        }
        PandaInstructionID instructionID = (PandaInstructionID)this.getParams()[1];
        PandaLiteralArray literalArray = (PandaLiteralArray) instructionID.getObj();
        for (int i = 0; i < literalArray.getPandaLiterals().length; i+=2) {
            String objName = ((PandaString)literalArray.getPandaLiterals()[i]).getContent();
            Offset obj = literalArray.getPandaLiterals()[i+1];
            if (obj instanceof PandaMethod){
                i++;
            }
            objMap.put(objName,obj);
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(baseToString());
        int size = objMap.keySet().size();
        int idx = 0;
        builder.append("{");
        for (String s : objMap.keySet()) {
            builder.append(s).append(':').append(objMap.get(s));
            if (idx++ != size -1) builder.append(", ");
        }
        builder.append("}");
        return builder.toString();
    }
}
