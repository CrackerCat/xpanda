package jmp0.abc.disasm.ins;

import jmp0.abc.PandaParseException;
import jmp0.abc.disasm.param.IPandaInstructionParam;
import jmp0.abc.disasm.param.PandaInstructionID;
import jmp0.abc.disasm.param.PandaInstructionIMM;
import jmp0.abc.disasm.param.PandaInstructionVReg;
import jmp0.abc.disasm.types.PandaOPCode;
import jmp0.abc.disasm.types.PandaOPCodeFormat;
import jmp0.abc.file.method.PandaMethod;
import lombok.Getter;
import lombok.SneakyThrows;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.LinkedList;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */

public class PandaInstruction implements IPandaInstruction {
    private final int pc;
    private final PandaOPCode opCode;
    private final IPandaInstructionParam[] paramObjects;
    public PandaInstruction(int pc, PandaOPCode opCode, byte[] param, PandaMethod pandaMethod){
        this.pc = pc;
        this.opCode = opCode;
        this.paramObjects = parseParamByFormat(opCode,param,pandaMethod);
    }
    @Override
    public int getPC() {
        return this.pc;
    }

    @Override
    public PandaOPCode getOpCode() {
        return opCode;
    }

    @Override
    public PandaOPCodeFormat getFormat() {
        return opCode.getFormat();
    }

    @Override
    public IPandaInstructionParam[] getParams() {
        return this.paramObjects;
    }


    @SneakyThrows
    private IPandaInstructionParam[] parseParamByFormat(PandaOPCode opCode,byte[] param,PandaMethod pandaMethod){
        ByteBuffer buffer = ByteBuffer.wrap(param).order(ByteOrder.LITTLE_ENDIAN);
        LinkedList<IPandaInstructionParam> list = new LinkedList<>();
        String realName = this.opCode.getFormat().name().replace("PREF_","");
        int index = 0;
        int bits = 0;
        int paramIndex = 0;
        String[] typeArr = realName.split("_");
        for (String s : typeArr) {
            switch (s){
                case "NONE":{
                    paramIndex++;
                    break;
                }
                case "IMM4" :{
                    int temp = (buffer.get(index) >> bits) & 0xf;
                    list.add(new PandaInstructionIMM(temp));
                    bits += 4;
                    paramIndex++;
                    break;
                }
                case "IMM8":{
                    if (bits != 0) throw new PandaParseException("bits should equal to zero.");
                    list.add(new PandaInstructionIMM(buffer.get(index)));
                    index++;
                    paramIndex++;
                    break;
                }
                case "IMM16":{
                    if (bits != 0) throw new PandaParseException("bits should equal to zero.");
                    list.add(new PandaInstructionIMM(buffer.getShort(index)));
                    index+=2;
                    paramIndex++;
                    break;
                }
                case "IMM32":{
                    if (bits != 0) throw new PandaParseException("bits should equal to zero.");
                    list.add(new PandaInstructionIMM(buffer.getInt(index)));
                    index+=4;
                    paramIndex++;
                    break;
                }
                case "IMM64":{
                    if (bits != 0) throw new PandaParseException("bits should equal to zero.");
                    list.add(new PandaInstructionIMM(buffer.getDouble(index)));
                    index+=8;
                    paramIndex++;
                    break;
                }
                case "V4": {
                    int temp = buffer.get(index) >> bits;
                    list.add(new PandaInstructionVReg((short) (temp & 0xf),pandaMethod));
                    bits += 4;
                    paramIndex++;
                    break;
                }
                case "V8" : {
                    if (bits != 0) throw new PandaParseException("bits should equal to zero.");
                    list.add(new PandaInstructionVReg(buffer.get(index),pandaMethod));
                    index++;
                    paramIndex++;
                    break;
                }
                case "V16": {
                    if (bits != 0) throw new PandaParseException("bits should equal to zero.");
                    list.add(new PandaInstructionVReg(buffer.getShort(index),pandaMethod));
                    index+=2;
                    paramIndex++;
                    break;
                }
                case "ID16":{
                    if (bits != 0) throw new PandaParseException("bits should equal to zero.");
                    int value = buffer.getShort(index) & 0xffff;
                    list.add(new PandaInstructionID(opCode,paramIndex,value,pandaMethod));
                    index+=2;
                    paramIndex++;
                    break;
                }
                case "ID32": {
                    if (bits != 0) throw new PandaParseException("bits should equal to zero.");
                    list.add(new PandaInstructionID(opCode,paramIndex,buffer.getInt(index),pandaMethod));
                    index+=4;
                    paramIndex++;
                    break;
                }
                default:
                    throw new PandaParseException(String.format("format param type not recognized %s",s));
            }
            if (bits == 8){
                index++;
                bits = 0;
            }
        }
        return list.toArray(new IPandaInstructionParam[]{});
    }

    protected final String baseToString(){
        String ret =  this.pc + ":" + this.opCode.getName() + " || ";
        if (getOpCode().isAccWrite()){
            return ret + "acc = ";
        }
        return ret;
    }


    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder(baseToString());
        for (int i = 0; i < this.paramObjects.length; i++) {
            stringBuilder.append(this.paramObjects[i]);
            if (i != this.paramObjects.length -1) stringBuilder.append(", ");
        }
        return stringBuilder.toString();
    }
}
