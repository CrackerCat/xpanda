package jmp0.abc.disasm.ins.definition;

import jmp0.abc.disasm.ins.PandaInstruction;
import jmp0.abc.disasm.param.PandaInstructionID;
import jmp0.abc.disasm.param.PandaInstructionIMM;
import jmp0.abc.disasm.param.PandaInstructionVReg;
import jmp0.abc.disasm.types.PandaOPCode;
import jmp0.abc.file.desc.Offset;
import jmp0.abc.file.desc.PandaString;
import jmp0.abc.file.literal.PandaLiteral;
import jmp0.abc.file.literal.PandaLiteralArray;
import jmp0.abc.file.method.PandaMethod;
import lombok.Getter;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
@Getter
public abstract class DEFINECLASSWITHBUFFERPandaInstruction extends PandaInstruction {
    @Getter
    public static class MethodDescription{
        private final String protoMethodName;
        private final PandaMethod method;
        private final int paramSize;
        public MethodDescription(String protoMethodName,PandaMethod method,int paramSize){
            this.protoMethodName = protoMethodName;
            this.method = method;
            this.paramSize = paramSize;
        }
    }

    @Getter
    public static class SendClassVariableDescription{
        private final String variableName;
        private final PandaLiteral variableValue;
        public SendClassVariableDescription(String name,PandaLiteral value) {
            this.variableName = name;
            this.variableValue = value;
        }
    }
    private final PandaMethod constructorMethod;
    private final MethodDescription[] methodDescriptions;
    private final MethodDescription[] staticMethodDescriptions;
    private final Number constructorParamSize;
    private final PandaInstructionVReg proto;
    private boolean isSendClass = false;
    private SendClassVariableDescription[] sendClassVariableDescriptions;
    public DEFINECLASSWITHBUFFERPandaInstruction(int pc, PandaOPCode opCode, byte[] param, PandaMethod pandaMethod) {
        super(pc, opCode, param, pandaMethod);
        this.constructorMethod = (PandaMethod) ((PandaInstructionID)(this.getParams()[1])).getObj();
        this.constructorParamSize =  ((PandaInstructionIMM)(this.getParams()[3])).getImm();
        this.proto = ((PandaInstructionVReg)(this.getParams()[4]));
        PandaLiteralArray literalArray = (PandaLiteralArray) ((PandaInstructionID)(this.getParams()[2])).getObj();
        Offset[] literals = literalArray.getPandaLiterals();
        if (opCode == PandaOPCode.CALLRUNTIME_DEFINESENDABLECLASS_PREF_IMM16_ID16_ID16_IMM16_V8){
            this.isSendClass = true;
        }
        int methodNum;
        int allMethodNum;
        if (isSendClass){
            methodNum = ((PandaLiteral)literals[literals.length - 2 ]).getValue().intValue();
            allMethodNum = (literals.length - 2) / 3;
        }else {
            methodNum = ((PandaLiteral)literals[literals.length -1 ]).getValue().intValue();
            allMethodNum = (literals.length - 1) / 3;
        }
        this.methodDescriptions = new MethodDescription[methodNum];
        this.staticMethodDescriptions = new MethodDescription[allMethodNum - methodNum];
        for (int i = 0; i < allMethodNum; i++) {
            PandaString name = ((PandaString)literals[i*3]);
            PandaMethod method = ((PandaMethod)literals[i*3 + 1]);
            short paramSize = ((PandaLiteral)literals[i*3 + 2 ]).getValue().shortValue();
            if (i < methodNum){
                this.methodDescriptions[i] = new MethodDescription(name.getContent(),method,paramSize);
            }else {
                this.staticMethodDescriptions[i-methodNum] = new MethodDescription(name.getContent(),method,paramSize);
            }
        }
        if (isSendClass){
            Offset[] initLiterals = ((PandaLiteralArray) literals[literals.length - 1]).getPandaLiterals();
            int initSize  = ((PandaLiteral)initLiterals[initLiterals.length -1 ]).getValue().intValue();
            this.sendClassVariableDescriptions = new SendClassVariableDescription[initSize];
            for (int i = 0; i < initSize; i++) {
                PandaString name = ((PandaString)initLiterals[i*2]);
                PandaLiteral value = ((PandaLiteral)initLiterals[i*2 + 1]);
                this.sendClassVariableDescriptions[i] = new SendClassVariableDescription(name.getContent(),value);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(baseToString());
        String indentation = "\t";
        builder.append("class ");
        builder.append(constructorMethod.getName().getContent()).append("{\n");
        if (isSendClass){
            for (SendClassVariableDescription sendClassVariableDescription : this.sendClassVariableDescriptions) {
                builder.append(indentation).append(sendClassVariableDescription.getVariableName())
                        .append(" = ").append(sendClassVariableDescription.getVariableValue()).append('\n');
            }
        }
        builder.append(indentation).append(this.proto).append(".prototype.").append("constructor = ").append(this.constructorMethod.getName().getContent()).append("(");
        for (int i = 0; i < this.constructorParamSize.intValue(); i++) {
            builder.append("p").append(i);
            if (i != this.constructorParamSize.intValue() -1) builder.append(",");
        }
        builder.append(");\n");
        for (MethodDescription methodDescription : this.methodDescriptions) {
            builder.append(indentation).append(this.proto).append(".prototype.").append(methodDescription.protoMethodName).append(" = ")
                    .append(methodDescription.method.getName().getContent()).append("(");
            for (int m = 0; m < methodDescription.paramSize; m++) {
                builder.append("p").append(m);
                if (m != methodDescription.paramSize -1) builder.append(",");
            }
            builder.append(");\n");
        }
        builder.append("}");
        return builder.toString();
    }
}
