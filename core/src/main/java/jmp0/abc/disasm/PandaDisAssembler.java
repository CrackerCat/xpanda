package jmp0.abc.disasm;

import jmp0.abc.PandaParseException;
import jmp0.abc.disasm.block.PandaIRBasicBlock;
import jmp0.abc.disasm.block.PandaIRCFG;
import jmp0.abc.disasm.ins.NOP_NONEPandaInstruction;
import jmp0.abc.disasm.ins.PandaInstruction;
import jmp0.abc.disasm.ins.abs.CALLRUNTIME_TOPROPERTYKEY_PREF_NONEPandaInstruction;
import jmp0.abc.disasm.ins.async.ASYNCFUNCTIONAWAITUNCAUGHT_V8PandaInstruction;
import jmp0.abc.disasm.ins.async.ASYNCFUNCTIONENTER_NONEPandaInstruction;
import jmp0.abc.disasm.ins.async.ASYNCFUNCTIONREJECT_V8PandaInstruction;
import jmp0.abc.disasm.ins.async.ASYNCFUNCTIONRESOLVE_V8PandaInstruction;
import jmp0.abc.disasm.ins.binary.INSTANCEOF_IMM8_V8PandaInstruction;
import jmp0.abc.disasm.ins.binary.ISIN_IMM8_V8PandaInstruction;
import jmp0.abc.disasm.ins.binary.STRICTEQ_IMM8_V8PandaInstruction;
import jmp0.abc.disasm.ins.binary.STRICTNOTEQ_IMM8_V8PandaInstruction;
import jmp0.abc.disasm.ins.call.*;
import jmp0.abc.disasm.ins.call.spr.*;
import jmp0.abc.disasm.ins.callruntime.CALLRUNTIME_CALLINIT_PREF_IMM8_V8PandaInstruction;
import jmp0.abc.disasm.ins.callruntime.CALLRUNTIME_ISFALSE_PREF_IMM8PandaInstruction;
import jmp0.abc.disasm.ins.callruntime.CALLRUNTIME_ISTRUE_PREF_IMM8PandaInstruction;
import jmp0.abc.disasm.ins.callruntime.CALLRUNTIME_NOTIFYCONCURRENTRESULT_PREF_NONEPandaInstruction;
import jmp0.abc.disasm.ins.creaters.CALLRUNTIME_NEWSENDABLEENV_PREF_IMM8PandaInstruction;
import jmp0.abc.disasm.ins.creaters.CALLRUNTIME_WIDENEWSENDABLEENV_PREF_IMM16PandaInstruction;
import jmp0.abc.disasm.ins.creaters.NEWOBJRANGE_IMM16_IMM8_V8PandaInstruction;
import jmp0.abc.disasm.ins.creaters.NEWOBJRANGE_IMM8_IMM8_V8PandaInstruction;
import jmp0.abc.disasm.ins.creaters.obj.CREATEOBJECTWITHEXCLUDEDKEYS_IMM8_V8_V8PandaInstruction;
import jmp0.abc.disasm.ins.creaters.obj.WIDE_CREATEOBJECTWITHEXCLUDEDKEYS_PREF_IMM16_V8_V8PandaInstruction;
import jmp0.abc.disasm.ins.creaters.spread.NEWOBJAPPLY_IMM16_V8PandaInstruction;
import jmp0.abc.disasm.ins.creaters.spread.NEWOBJAPPLY_IMM8_V8PandaInstruction;
import jmp0.abc.disasm.ins.definition.*;
import jmp0.abc.disasm.ins.generator.GETRESUMEMODE_NONEPandaInstruction;
import jmp0.abc.disasm.ins.generator.RESUMEGENERATOR_NONEPandaInstruction;
import jmp0.abc.disasm.ins.generator.SUSPENDGENERATOR_V8PandaInstruction;
import jmp0.abc.disasm.ins.iterator.GETITERATOR_IMM16PandaInstruction;
import jmp0.abc.disasm.ins.iterator.GETITERATOR_IMM8PandaInstruction;
import jmp0.abc.disasm.ins.iterator.GETNEXTPROPNAME_V8PandaInstruction;
import jmp0.abc.disasm.ins.iterator.GETPROPITERATOR_NONEPandaInstruction;
import jmp0.abc.disasm.ins.load.*;
import jmp0.abc.disasm.ins.mov.MOV_V16_V16PandaInstruction;
import jmp0.abc.disasm.ins.mov.MOV_V4_V4PandaInstruction;
import jmp0.abc.disasm.ins.mov.MOV_V8_V8PandaInstruction;
import jmp0.abc.disasm.ins.ret.RETURNUNDEFINED_NONEPandaInstruction;
import jmp0.abc.disasm.ins.ret.RETURN_NONEPandaInstruction;
import jmp0.abc.disasm.ins.store.STA_V8PandaInstruction;
import jmp0.abc.disasm.ins.trw.THROW_PREF_NONEPandaInstruction;
import jmp0.abc.disasm.ins.trw.THROW_UNDEFINEDIFHOLEWITHNAME_PREF_ID16PandaInstruction;
import jmp0.abc.disasm.ins.trw.ThrowIfPandaInstruction;
import jmp0.abc.disasm.ins.visitors.WIDE_LDLOCALMODULEVAR_PREF_IMM16PandaInstruction;
import jmp0.abc.disasm.ins.visitors.copyobj.COPYDATAPROPERTIES_V8PandaInstruction;
import jmp0.abc.disasm.ins.visitors.ldext.*;
import jmp0.abc.disasm.ins.visitors.LDLOCALMODULEVAR_IMM8PandaInstruction;
import jmp0.abc.disasm.ins.visitors.ldlex.CALLRUNTIME_LDSENDABLEVAR_PREF_IMM4_IMM4PandaInstruction;
import jmp0.abc.disasm.ins.visitors.ldlex.CALLRUNTIME_LDSENDABLEVAR_PREF_IMM8_IMM8PandaInstruction;
import jmp0.abc.disasm.ins.visitors.ldlex.CALLRUNTIME_WIDELDSENDABLEVAR_PREF_IMM16_IMM16PandaInstruction;
import jmp0.abc.disasm.ins.visitors.setobj.SETOBJECTWITHPROTO_IMM16_V8PandaInstruction;
import jmp0.abc.disasm.ins.visitors.setobj.SETOBJECTWITHPROTO_IMM8_V8PandaInstruction;
import jmp0.abc.disasm.ins.visitors.stlex.CALLRUNTIME_STSENDABLEVAR_PREF_IMM4_IMM4PandaInstruction;
import jmp0.abc.disasm.ins.visitors.stlex.CALLRUNTIME_STSENDABLEVAR_PREF_IMM8_IMM8PandaInstruction;
import jmp0.abc.disasm.ins.visitors.stlex.CALLRUNTIME_WIDESTSENDABLEVAR_PREF_IMM16_IMM16PandaInstruction;
import jmp0.abc.disasm.ins.visitors.stmod.STMODULEVAR_IMM8PandaInstruction;
import jmp0.abc.disasm.ins.visitors.stmod.WIDE_STMODULEVAR_PREF_IMM16PandaInstruction;
import jmp0.abc.disasm.ins.visitors.array.STARRAYSPREAD_V8_V8;
import jmp0.abc.disasm.ins.visitors.array.STOWNBYINDEX_IMM16_V8_IMM16PandaInstruction;
import jmp0.abc.disasm.ins.visitors.array.STOWNBYINDEX_IMM8_V8_IMM16PandaInstruction;
import jmp0.abc.disasm.ins.visitors.array.WIDE_STOWNBYINDEX_PREF_V8_IMM32PandaInstruction;
import jmp0.abc.disasm.ins.visitors.delobj.DELOBJPROP_V8PandaInstruction;
import jmp0.abc.disasm.ins.visitors.global.TRYLDGLOBALBYNAME_IMM16_ID16PandaInstruction;
import jmp0.abc.disasm.ins.visitors.global.TRYLDGLOBALBYNAME_IMM8_ID16PandaInstruction;
import jmp0.abc.disasm.ins.visitors.ldobj.*;
import jmp0.abc.disasm.ins.visitors.ldspr.LDSUPERBYNAME_IMM16_ID16PandaInstruction;
import jmp0.abc.disasm.ins.visitors.ldspr.LDSUPERBYNAME_IMM8_ID16PandaInstruction;
import jmp0.abc.disasm.ins.visitors.stobj.*;
import jmp0.abc.disasm.ins.visitors.stobj.spr.LDSUPERBYVALUE_IMM16_V8PandaInstruction;
import jmp0.abc.disasm.ins.visitors.stobj.spr.LDSUPERBYVALUE_IMM8_V8PandaInstruction;
import jmp0.abc.disasm.ins.visitors.stobj.spr.STSUPERBYNAME_IMM16_ID16_V8PandaInstruction;
import jmp0.abc.disasm.ins.visitors.stobj.spr.STSUPERBYNAME_IMM8_ID16_V8PandaInstruction;
import jmp0.abc.disasm.ins.visitors.stobjrec.STCONSTTOGLOBALRECORD_IMM16_ID16PandaInstruction;
import jmp0.abc.disasm.ins.visitors.stobjrec.STTOGLOBALRECORD_IMM16_ID16PandaInstruction;
import jmp0.abc.disasm.ins.yield.CREATEGENERATOROBJ_V8PandaInstruction;
import jmp0.abc.disasm.ins.yield.CREATEITERRESULTOBJ_V8_V8PandaInstruction;
import jmp0.abc.disasm.types.PandaOPCode;
import jmp0.abc.file.PandaFileUtils;
import jmp0.abc.file.method.PandaMethod;
import lombok.SneakyThrows;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class PandaDisAssembler {
    private static final HashMap<Integer, PandaOPCode> OPCODE_MAP = new HashMap<>();

    static{
        for (PandaOPCode value : PandaOPCode.values()) {
            OPCODE_MAP.put(value.getCode(),value);
        }
    }

    @SneakyThrows
    public static PandaIRCFG disAssembly(PandaMethod method){
        byte[] instructions = method.getMethodCode().getInstructions();
        LinkedList<PandaIRBasicBlock> pandaIRBasicBlockLinkedList = new LinkedList<>();
        LinkedList<PandaInstruction> pandaInstructionLinkedList = new LinkedList<>();
        int pc = 0;
        while (pc < instructions.length){
            int nowPC = pc;
            int op1 = instructions[pc++] & 0xff;
            if ( op1>= PandaOPCode.getLastInstruction().getCode()){
                int op2 = instructions[pc++];
                op1 = op1 | ((op2 & 0xff) << 8);
            }
            PandaOPCode code = OPCODE_MAP.get(op1);
            if (code == null){
                throw new PandaParseException(String.format("opcode: 0x%x, not recognized",instructions[pc]));
            }
            PandaInstruction pandaInstruction = createInstruction(nowPC,code,
                    PandaFileUtils.readSubBytes(instructions,pc,code.getFormat().getParamSize()),method);
            pandaInstructionLinkedList.add(pandaInstruction);
            if (pandaInstruction.getOpCode().isTerminator()){
                if (pandaIRBasicBlockLinkedList.isEmpty()){
                    pandaIRBasicBlockLinkedList.add(new PandaIRBasicBlock("entry",pandaInstructionLinkedList.toArray(new PandaInstruction[]{}),method));
                    pandaInstructionLinkedList.clear();
                }else {
                    pandaIRBasicBlockLinkedList.add(new PandaIRBasicBlock("label_" + pandaInstructionLinkedList.get(0).getPC(), pandaInstructionLinkedList.toArray(new PandaInstruction[]{}),method));
                    pandaInstructionLinkedList.clear();
                }

            }
            pc += code.getFormat().getParamSize();
        }
        return new PandaIRCFG(pandaIRBasicBlockLinkedList,method);
    }

    @SneakyThrows
    private static PandaInstruction createInstruction(int pc, PandaOPCode opCode, byte[] param, PandaMethod pandaMethod){
        switch (opCode){
            case  JMP_IMM8:
                return new jmp0.abc.disasm.ins.jump.JMP_IMM8PandaInstruction(pc,opCode,param,pandaMethod);
            case  JMP_IMM16:
                return new jmp0.abc.disasm.ins.jump.JMP_IMM16PandaInstruction(pc,opCode,param,pandaMethod);
            case  JEQZ_IMM8:
                return new jmp0.abc.disasm.ins.jump.JEQZ_IMM8PandaInstruction(pc,opCode,param,pandaMethod);
            case  JEQZ_IMM16:
                return new jmp0.abc.disasm.ins.jump.JEQZ_IMM16PandaInstruction(pc,opCode,param,pandaMethod);
            case  JNEZ_IMM8:
                return new jmp0.abc.disasm.ins.jump.JNEZ_IMM8PandaInstruction(pc,opCode,param,pandaMethod);
            case  JSTRICTEQZ_IMM8:
                return new jmp0.abc.disasm.ins.jump.JSTRICTEQZ_IMM8PandaInstruction(pc,opCode,param,pandaMethod);
            case  JNSTRICTEQZ_IMM8:
                return new jmp0.abc.disasm.ins.jump.JNSTRICTEQZ_IMM8PandaInstruction(pc,opCode,param,pandaMethod);
            case  JEQNULL_IMM8:
                return new jmp0.abc.disasm.ins.jump.JEQNULL_IMM8PandaInstruction(pc,opCode,param,pandaMethod);
            case  JNENULL_IMM8:
                return new jmp0.abc.disasm.ins.jump.JNENULL_IMM8PandaInstruction(pc,opCode,param,pandaMethod);
            case  JSTRICTEQNULL_IMM8:
                return new jmp0.abc.disasm.ins.jump.JSTRICTEQNULL_IMM8PandaInstruction(pc,opCode,param,pandaMethod);
            case  JNSTRICTEQNULL_IMM8:
                return new jmp0.abc.disasm.ins.jump.JNSTRICTEQNULL_IMM8PandaInstruction(pc,opCode,param,pandaMethod);
            case  JEQUNDEFINED_IMM8:
                return new jmp0.abc.disasm.ins.jump.JEQUNDEFINED_IMM8PandaInstruction(pc,opCode,param,pandaMethod);
            case  JNEUNDEFINED_IMM8:
                return new jmp0.abc.disasm.ins.jump.JNEUNDEFINED_IMM8PandaInstruction(pc,opCode,param,pandaMethod);
            case  JSTRICTEQUNDEFINED_IMM8:
                return new jmp0.abc.disasm.ins.jump.JSTRICTEQUNDEFINED_IMM8PandaInstruction(pc,opCode,param,pandaMethod);
            case  JNSTRICTEQUNDEFINED_IMM8:
                return new jmp0.abc.disasm.ins.jump.JNSTRICTEQUNDEFINED_IMM8PandaInstruction(pc,opCode,param,pandaMethod);
            case  JEQ_V8_IMM8:
                return new jmp0.abc.disasm.ins.jump.JEQ_V8_IMM8PandaInstruction(pc,opCode,param,pandaMethod);
            case  JNE_V8_IMM8:
                return new jmp0.abc.disasm.ins.jump.JNE_V8_IMM8PandaInstruction(pc,opCode,param,pandaMethod);
            case  JSTRICTEQ_V8_IMM8:
                return new jmp0.abc.disasm.ins.jump.JSTRICTEQ_V8_IMM8PandaInstruction(pc,opCode,param,pandaMethod);
            case  JNSTRICTEQ_V8_IMM8:
                return new jmp0.abc.disasm.ins.jump.JNSTRICTEQ_V8_IMM8PandaInstruction(pc,opCode,param,pandaMethod);
            case  JMP_IMM32:
                return new jmp0.abc.disasm.ins.jump.JMP_IMM32PandaInstruction(pc,opCode,param,pandaMethod);
            case  JEQZ_IMM32:
                return new jmp0.abc.disasm.ins.jump.JEQZ_IMM32PandaInstruction(pc,opCode,param,pandaMethod);
            case  JNEZ_IMM16:
                return new jmp0.abc.disasm.ins.jump.JNEZ_IMM16PandaInstruction(pc,opCode,param,pandaMethod);
            case  JNEZ_IMM32:
                return new jmp0.abc.disasm.ins.jump.JNEZ_IMM32PandaInstruction(pc,opCode,param,pandaMethod);
            case  JSTRICTEQZ_IMM16:
                return new jmp0.abc.disasm.ins.jump.JSTRICTEQZ_IMM16PandaInstruction(pc,opCode,param,pandaMethod);
            case  JNSTRICTEQZ_IMM16:
                return new jmp0.abc.disasm.ins.jump.JNSTRICTEQZ_IMM16PandaInstruction(pc,opCode,param,pandaMethod);
            case  JEQNULL_IMM16:
                return new jmp0.abc.disasm.ins.jump.JEQNULL_IMM16PandaInstruction(pc,opCode,param,pandaMethod);
            case  JNENULL_IMM16:
                return new jmp0.abc.disasm.ins.jump.JNENULL_IMM16PandaInstruction(pc,opCode,param,pandaMethod);
            case  JSTRICTEQNULL_IMM16:
                return new jmp0.abc.disasm.ins.jump.JSTRICTEQNULL_IMM16PandaInstruction(pc,opCode,param,pandaMethod);
            case  JNSTRICTEQNULL_IMM16:
                return new jmp0.abc.disasm.ins.jump.JNSTRICTEQNULL_IMM16PandaInstruction(pc,opCode,param,pandaMethod);
            case  JEQUNDEFINED_IMM16:
                return new jmp0.abc.disasm.ins.jump.JEQUNDEFINED_IMM16PandaInstruction(pc,opCode,param,pandaMethod);
            case  JNEUNDEFINED_IMM16:
                return new jmp0.abc.disasm.ins.jump.JNEUNDEFINED_IMM16PandaInstruction(pc,opCode,param,pandaMethod);
            case  JSTRICTEQUNDEFINED_IMM16:
                return new jmp0.abc.disasm.ins.jump.JSTRICTEQUNDEFINED_IMM16PandaInstruction(pc,opCode,param,pandaMethod);
            case  JNSTRICTEQUNDEFINED_IMM16:
                return new jmp0.abc.disasm.ins.jump.JNSTRICTEQUNDEFINED_IMM16PandaInstruction(pc,opCode,param,pandaMethod);
            case  JEQ_V8_IMM16:
                return new jmp0.abc.disasm.ins.jump.JEQ_V8_IMM16PandaInstruction(pc,opCode,param,pandaMethod);
            case  JNE_V8_IMM16:
                return new jmp0.abc.disasm.ins.jump.JNE_V8_IMM16PandaInstruction(pc,opCode,param,pandaMethod);
            case  JSTRICTEQ_V8_IMM16:
                return new jmp0.abc.disasm.ins.jump.JSTRICTEQ_V8_IMM16PandaInstruction(pc,opCode,param,pandaMethod);
            case  JNSTRICTEQ_V8_IMM16:
                return new jmp0.abc.disasm.ins.jump.JNSTRICTEQ_V8_IMM16PandaInstruction(pc,opCode,param,pandaMethod);
            case LDEXTERNALMODULEVAR_IMM8:
                return new LDEXTERNALMODULEVAR_IMM8PandaInstruction(pc,opCode,param,pandaMethod);
            case STMODULEVAR_IMM8:
                return new STMODULEVAR_IMM8PandaInstruction(pc,opCode,param,pandaMethod);
            case LDLOCALMODULEVAR_IMM8:
                return new LDLOCALMODULEVAR_IMM8PandaInstruction(pc,opCode,param,pandaMethod);
            case WIDE_LDLOCALMODULEVAR_PREF_IMM16:
                return new WIDE_LDLOCALMODULEVAR_PREF_IMM16PandaInstruction(pc,opCode,param,pandaMethod);
            case NEWOBJRANGE_IMM8_IMM8_V8:
                return new NEWOBJRANGE_IMM8_IMM8_V8PandaInstruction(pc,opCode,param,pandaMethod);
            case NEWOBJRANGE_IMM16_IMM8_V8:
                return new NEWOBJRANGE_IMM16_IMM8_V8PandaInstruction(pc,opCode,param,pandaMethod);
            case CALLTHIS0_IMM8_V8:
                return new CALLTHIS0_IMM8_V8PandaInstruction(pc,opCode,param,pandaMethod);
            case CALLTHIS1_IMM8_V8_V8:
                return new CALLTHIS1_IMM8_V8_V8PandaInstruction(pc,opCode,param,pandaMethod);
            case CALLTHIS2_IMM8_V8_V8_V8:
                return new CALLTHIS2_IMM8_V8_V8_V8PandaInstruction(pc,opCode,param,pandaMethod);
            case CALLTHIS3_IMM8_V8_V8_V8_V8:
                return new CALLTHIS3_IMM8_V8_V8_V8_V8PandaInstruction(pc,opCode,param,pandaMethod);
            case CALLTHISRANGE_IMM8_IMM8_V8:
                return new CALLTHISRANGE_IMM8_IMM8_V8PandaInstruction(pc,opCode,param,pandaMethod);
            case DEFINECLASSWITHBUFFER_IMM8_ID16_ID16_IMM16_V8:
                return new DEFINECLASSWITHBUFFER_IMM8_ID16_ID16_IMM16_V8PandaInstruction(pc,opCode,param,pandaMethod);
            case DEFINECLASSWITHBUFFER_IMM16_ID16_ID16_IMM16_V8:
                return new DEFINECLASSWITHBUFFER_IMM16_ID16_ID16_IMM16_V8PandaInstruction(pc,opCode,param,pandaMethod);
            case MOV_V4_V4:
                return new MOV_V4_V4PandaInstruction(pc,opCode,param,pandaMethod);
            case MOV_V8_V8:
                return new MOV_V8_V8PandaInstruction(pc,opCode,param,pandaMethod);
            case MOV_V16_V16:
                return new MOV_V16_V16PandaInstruction(pc,opCode,param,pandaMethod);
            case STA_V8:
                return new STA_V8PandaInstruction(pc,opCode,param,pandaMethod);
            case RETURN_NONE:
                return new RETURN_NONEPandaInstruction(pc,opCode,param,pandaMethod);
            case RETURNUNDEFINED_NONE:
                return new RETURNUNDEFINED_NONEPandaInstruction(pc,opCode,param,pandaMethod);
            case FLDAI_IMM64:
                return new FLDAI_IMM64PandaInstruction(pc,opCode,param,pandaMethod);
            case LDA_STR_ID16:
                return new LDA_STR_ID16PandaInstruction(pc,opCode,param,pandaMethod);
            case LDA_V8:
                return new LDA_V8PandaInstruction(pc,opCode,param,pandaMethod);
            case LDAI_IMM32:
                return new LDAI_IMM32PandaInstruction(pc,opCode,param,pandaMethod);
            case LDBIGINT_ID16:
                return new LDBIGINT_ID16PandaInstruction(pc,opCode,param,pandaMethod);
            case LDFALSE_NONE:
                return new LDFALSE_NONEPandaInstruction(pc,opCode,param,pandaMethod);
            case LDGLOBAL_NONE:
                return new LDGLOBAL_NONEPandaInstruction(pc,opCode,param,pandaMethod);
            case LDHOLE_NONE:
                return new LDHOLE_NONEPandaInstruction(pc,opCode,param,pandaMethod);
            case LDINFINITY_NONE:
                return new LDINFINITY_NONEPandaInstruction(pc,opCode,param,pandaMethod);
            case LDNAN_NONE:
                return new LDNAN_NONEPandaInstruction(pc,opCode,param,pandaMethod);
            case LDNULL_NONE:
                return new LDNULL_NONEPandaInstruction(pc,opCode,param,pandaMethod);
            case LDTHIS_NONE:
                return new LDTHIS_NONEPandaInstruction(pc,opCode,param,pandaMethod);
            case LDTRUE_NONE:
                return new LDTRUE_NONEPandaInstruction(pc,opCode,param,pandaMethod);
            case LDUNDEFINED_NONE:
                return new LDUNDEFINED_NONEPandaInstruction(pc,opCode,param,pandaMethod);
            case LDOBJBYINDEX_IMM8_IMM16:
                return new LDOBJBYINDEX_IMM8_IMM16PandaInstruction(pc,opCode,param,pandaMethod);
            case LDOBJBYINDEX_IMM16_IMM16:
                return new LDOBJBYINDEX_IMM16_IMM16PandaInstruction(pc,opCode,param,pandaMethod);
            case LDOBJBYVALUE_IMM8_V8:
                return new LDOBJBYVALUE_IMM8_V8PandaInstruction(pc,opCode,param,pandaMethod);
            case LDOBJBYVALUE_IMM16_V8:
                return new LDOBJBYVALUE_IMM16_V8PandaInstruction(pc,opCode,param,pandaMethod);
            case LDOBJBYNAME_IMM8_ID16:
                return new LDOBJBYNAME_IMM8_ID16pandaInstruction(pc,opCode,param,pandaMethod);
            case LDOBJBYNAME_IMM16_ID16:
                return new LDOBJBYNAME_IMM16_ID16PandaInstruction(pc,opCode,param,pandaMethod);
            case NEWLEXENV_IMM8:
                return new jmp0.abc.disasm.ins.creaters.NEWLEXENV_IMM8PandaInstruction(pc,opCode,param,pandaMethod);
            case NEWLEXENVWITHNAME_IMM8_ID16:
                return new jmp0.abc.disasm.ins.creaters.NEWLEXENVWITHNAME_IMM8_ID16PandaInstruction(pc,opCode,param,pandaMethod);
            case WIDE_NEWLEXENV_PREF_IMM16:
                return new jmp0.abc.disasm.ins.creaters.WIDE_NEWLEXENV_PREF_IMM16PandaInstruction(pc,opCode,param,pandaMethod);
            case WIDE_NEWLEXENVWITHNAME_PREF_IMM16_ID16:
                return new jmp0.abc.disasm.ins.creaters.WIDE_NEWLEXENVWITHNAME_PREF_IMM16_ID16PandaInstruction(pc,opCode,param,pandaMethod);
            case LDLEXVAR_IMM4_IMM4:
                return new jmp0.abc.disasm.ins.visitors.ldlex.LDLEXVAR_IMM4_IMM4PandaInstruction(pc,opCode,param,pandaMethod);
            case LDLEXVAR_IMM8_IMM8:
                return new jmp0.abc.disasm.ins.visitors.ldlex.LDLEXVAR_IMM8_IMM8PandaInstruction(pc,opCode,param,pandaMethod);
            case WIDE_LDLEXVAR_PREF_IMM16_IMM16:
                return new jmp0.abc.disasm.ins.visitors.ldlex.WIDE_LDLEXVAR_PREF_IMM16_IMM16PandaInstruction(pc,opCode,param,pandaMethod);
            case STLEXVAR_IMM4_IMM4:
                return new jmp0.abc.disasm.ins.visitors.stlex.STLEXVAR_IMM4_IMM4PandaInstruction(pc,opCode,param,pandaMethod);
            case STLEXVAR_IMM8_IMM8:
                return new jmp0.abc.disasm.ins.visitors.stlex.STLEXVAR_IMM8_IMM8PandaInstruction(pc,opCode,param,pandaMethod);
            case WIDE_STLEXVAR_PREF_IMM16_IMM16:
                return new jmp0.abc.disasm.ins.visitors.stlex.WIDE_STLEXVAR_PREF_IMM16_IMM16PandaInstruction(pc,opCode,param,pandaMethod);
            case STOBJBYVALUE_IMM8_V8_V8:
                return new jmp0.abc.disasm.ins.visitors.stobj.STOBJBYVALUE_IMM8_V8_V8PandaInstruction(pc,opCode,param,pandaMethod);
            case STOBJBYINDEX_IMM8_V8_IMM16:
                return new jmp0.abc.disasm.ins.visitors.stobj.STOBJBYINDEX_IMM8_V8_IMM16PandaInstruction(pc,opCode,param,pandaMethod);
            case STOBJBYNAME_IMM8_ID16_V8:
                return new jmp0.abc.disasm.ins.visitors.stobj.STOBJBYNAME_IMM8_ID16_V8PandaInstruction(pc,opCode,param,pandaMethod);
            case STOBJBYVALUE_IMM16_V8_V8:
                return new jmp0.abc.disasm.ins.visitors.stobj.STOBJBYVALUE_IMM16_V8_V8PandaInstruction(pc,opCode,param,pandaMethod);
            case STOBJBYINDEX_IMM16_V8_IMM16:
                return new jmp0.abc.disasm.ins.visitors.stobj.STOBJBYINDEX_IMM16_V8_IMM16PandaInstruction(pc,opCode,param,pandaMethod);
            case STOBJBYNAME_IMM16_ID16_V8:
                return new jmp0.abc.disasm.ins.visitors.stobj.STOBJBYNAME_IMM16_ID16_V8PandaInstruction(pc,opCode,param,pandaMethod);
            case WIDE_STOBJBYINDEX_PREF_V8_IMM32:
                return new jmp0.abc.disasm.ins.visitors.stobj.WIDE_STOBJBYINDEX_PREF_V8_IMM32PandaInstruction(pc,opCode,param,pandaMethod);
            case THROW_NOTEXISTS_PREF_NONE:
            case THROW_PATTERNNONCOERCIBLE_PREF_NONE:
            case THROW_DELETESUPERPROPERTY_PREF_NONE:
            case THROW_CONSTASSIGNMENT_PREF_V8:
            case THROW_IFNOTOBJECT_PREF_V8:
            case THROW_UNDEFINEDIFHOLE_PREF_V8_V8:
            case THROW_IFSUPERNOTCORRECTCALL_PREF_IMM8:
            case THROW_IFSUPERNOTCORRECTCALL_PREF_IMM16:
                return new ThrowIfPandaInstruction(pc,opCode,param,pandaMethod);
            case THROW_UNDEFINEDIFHOLEWITHNAME_PREF_ID16:
                return new THROW_UNDEFINEDIFHOLEWITHNAME_PREF_ID16PandaInstruction(pc,opCode,param,pandaMethod);
            case COPYRESTARGS_IMM8:
                return new jmp0.abc.disasm.ins.visitors.copyrestargs.COPYRESTARGS_IMM8PandaInstruction(pc,opCode,param,pandaMethod);
            case WIDE_COPYRESTARGS_PREF_IMM16:
                return new jmp0.abc.disasm.ins.visitors.copyrestargs.WIDE_COPYRESTARGS_PREF_IMM16PandaInstruction(pc,opCode,param,pandaMethod);
            case CREATEEMPTYARRAY_IMM8:
                return new jmp0.abc.disasm.ins.creaters.array.CREATEEMPTYARRAY_IMM8PandaInstruction(pc,opCode,param,pandaMethod);
            case CREATEEMPTYARRAY_IMM16:
                return new jmp0.abc.disasm.ins.creaters.array.CREATEEMPTYARRAY_IMM16PandaInstruction(pc,opCode,param,pandaMethod);
            case CREATEARRAYWITHBUFFER_IMM8_ID16:
                return new jmp0.abc.disasm.ins.creaters.array.CREATEARRAYWITHBUFFER_IMM8_ID16PandaInstruction(pc,opCode,param,pandaMethod);
            case CREATEARRAYWITHBUFFER_IMM16_ID16:
                return new jmp0.abc.disasm.ins.creaters.array.CREATEARRAYWITHBUFFER_IMM16_ID16PandaInstruction(pc,opCode,param,pandaMethod);
            case CREATEEMPTYOBJECT_NONE:
                return new jmp0.abc.disasm.ins.creaters.obj.CREATEEMPTYOBJECT_NONEPandaInstruction(pc,opCode,param,pandaMethod);
            case CREATEOBJECTWITHBUFFER_IMM8_ID16:
                return new jmp0.abc.disasm.ins.creaters.obj.CREATEOBJECTWITHBUFFER_IMM8_ID16PandaInstruction(pc,opCode,param,pandaMethod);
            case CREATEOBJECTWITHBUFFER_IMM16_ID16:
                return new jmp0.abc.disasm.ins.creaters.obj.CREATEOBJECTWITHBUFFER_IMM16_ID16PandaInstruction(pc,opCode,param,pandaMethod);
            case STARRAYSPREAD_V8_V8:
                return new STARRAYSPREAD_V8_V8(pc,opCode,param,pandaMethod);
            case SUPERCALLSPREAD_IMM8_V8:
                return new SUPERCALLSPREAD_IMM8_V8PandaInstruction(pc,opCode,param,pandaMethod);
            case SUPERCALLTHISRANGE_IMM8_IMM8_V8:
                return new SUPERCALLTHISRANGE_IMM8_IMM8_V8PandaInstruction(pc,opCode,param,pandaMethod);
            case WIDE_SUPERCALLTHISRANGE_PREF_IMM16_V8:
                return new WIDE_SUPERCALLTHISRANGE_PREF_IMM16_V8PandaInstruction(pc,opCode,param,pandaMethod);
            case SUPERCALLARROWRANGE_IMM8_IMM8_V8:
                return new SUPERCALLARROWRANGE_IMM8_IMM8_V8PandaInstruction(pc,opCode,param,pandaMethod);
            case WIDE_SUPERCALLARROWRANGE_PREF_IMM16_V8:
                return new WIDE_SUPERCALLARROWRANGE_PREF_IMM16_V8PandaInstruction(pc,opCode,param,pandaMethod);
            case ADD2_IMM8_V8:
                return new jmp0.abc.disasm.ins.binary.ADD2_IMM8_V8PandaInstruction(pc,opCode,param,pandaMethod);
            case SUB2_IMM8_V8:
                return new jmp0.abc.disasm.ins.binary.SUB2_IMM8_V8PandaInstruction(pc,opCode,param,pandaMethod);
            case MUL2_IMM8_V8:
                return new jmp0.abc.disasm.ins.binary.MUL2_IMM8_V8PandaInstruction(pc,opCode,param,pandaMethod);
            case DIV2_IMM8_V8:
                return new jmp0.abc.disasm.ins.binary.DIV2_IMM8_V8PandaInstruction(pc,opCode,param,pandaMethod);
            case MOD2_IMM8_V8:
                return new jmp0.abc.disasm.ins.binary.MOD2_IMM8_V8PandaInstruction(pc,opCode,param,pandaMethod);
            case EQ_IMM8_V8:
                return new jmp0.abc.disasm.ins.binary.EQ_IMM8_V8PandaInstruction(pc,opCode,param,pandaMethod);
            case NOTEQ_IMM8_V8:
                return new jmp0.abc.disasm.ins.binary.NOTEQ_IMM8_V8PandaInstruction(pc,opCode,param,pandaMethod);
            case LESS_IMM8_V8:
                return new jmp0.abc.disasm.ins.binary.LESS_IMM8_V8PandaInstruction(pc,opCode,param,pandaMethod);
            case LESSEQ_IMM8_V8:
                return new jmp0.abc.disasm.ins.binary.LESSEQ_IMM8_V8PandaInstruction(pc,opCode,param,pandaMethod);
            case GREATER_IMM8_V8:
                return new jmp0.abc.disasm.ins.binary.GREATER_IMM8_V8PandaInstruction(pc,opCode,param,pandaMethod);
            case GREATEREQ_IMM8_V8:
                return new jmp0.abc.disasm.ins.binary.GREATEREQ_IMM8_V8PandaInstruction(pc,opCode,param,pandaMethod);
            case SHL2_IMM8_V8:
                return new jmp0.abc.disasm.ins.binary.SHL2_IMM8_V8PandaInstruction(pc,opCode,param,pandaMethod);
            case SHR2_IMM8_V8:
                return new jmp0.abc.disasm.ins.binary.SHR2_IMM8_V8PandaInstruction(pc,opCode,param,pandaMethod);
            case ASHR2_IMM8_V8:
                return new jmp0.abc.disasm.ins.binary.ASHR2_IMM8_V8PandaInstruction(pc,opCode,param,pandaMethod);
            case AND2_IMM8_V8:
                return new jmp0.abc.disasm.ins.binary.AND2_IMM8_V8PandaInstruction(pc,opCode,param,pandaMethod);
            case OR2_IMM8_V8:
                return new jmp0.abc.disasm.ins.binary.OR2_IMM8_V8PandaInstruction(pc,opCode,param,pandaMethod);
            case XOR2_IMM8_V8:
                return new jmp0.abc.disasm.ins.binary.XOR2_IMM8_V8PandaInstruction(pc,opCode,param,pandaMethod);
            case EXP_IMM8_V8:
                return new jmp0.abc.disasm.ins.binary.EXP_IMM8_V8PandaInstruction(pc,opCode,param,pandaMethod);
            case TYPEOF_IMM8:
                return new jmp0.abc.disasm.ins.unary.TYPEOF_IMM8PandaInstruction(pc,opCode,param,pandaMethod);
            case TYPEOF_IMM16:
                return new jmp0.abc.disasm.ins.unary.TYPEOF_IMM16PandaInstruction(pc,opCode,param,pandaMethod);
            case TONUMBER_IMM8:
                return new jmp0.abc.disasm.ins.unary.TONUMBER_IMM8PandaInstruction(pc,opCode,param,pandaMethod);
            case TONUMERIC_IMM8:
                return new jmp0.abc.disasm.ins.unary.TONUMERIC_IMM8PandaInstruction(pc,opCode,param,pandaMethod);
            case NEG_IMM8:
                return new jmp0.abc.disasm.ins.unary.NEG_IMM8PandaInstruction(pc,opCode,param,pandaMethod);
            case NOT_IMM8:
                return new jmp0.abc.disasm.ins.unary.NOT_IMM8PandaInstruction(pc,opCode,param,pandaMethod);
            case INC_IMM8:
                return new jmp0.abc.disasm.ins.unary.INC_IMM8PandaInstruction(pc,opCode,param,pandaMethod);
            case DEC_IMM8:
                return new jmp0.abc.disasm.ins.unary.DEC_IMM8PandaInstruction(pc,opCode,param,pandaMethod);
            case ISTRUE_NONE:
                return new jmp0.abc.disasm.ins.unary.ISTRUE_NONEPandaInstruction(pc,opCode,param,pandaMethod);
            case ISFALSE_NONE:
                return new jmp0.abc.disasm.ins.unary.ISFALSE_NONEPandaInstruction(pc,opCode,param,pandaMethod);
            case STRICTEQ_IMM8_V8:
                return new STRICTEQ_IMM8_V8PandaInstruction(pc,opCode,param,pandaMethod);
            case STRICTNOTEQ_IMM8_V8:
                return new STRICTNOTEQ_IMM8_V8PandaInstruction(pc,opCode,param,pandaMethod);
            case TRYLDGLOBALBYNAME_IMM8_ID16:
                return new TRYLDGLOBALBYNAME_IMM8_ID16PandaInstruction(pc,opCode,param,pandaMethod);
            case TRYLDGLOBALBYNAME_IMM16_ID16:
                return new TRYLDGLOBALBYNAME_IMM16_ID16PandaInstruction(pc,opCode,param,pandaMethod);
            case STOWNBYINDEX_IMM8_V8_IMM16:
                return new STOWNBYINDEX_IMM8_V8_IMM16PandaInstruction(pc,opCode,param,pandaMethod);
            case STOWNBYINDEX_IMM16_V8_IMM16:
                return new STOWNBYINDEX_IMM16_V8_IMM16PandaInstruction(pc,opCode,param,pandaMethod);
            case WIDE_STOWNBYINDEX_PREF_V8_IMM32:
                return new WIDE_STOWNBYINDEX_PREF_V8_IMM32PandaInstruction(pc,opCode,param,pandaMethod);
            case STOWNBYNAME_IMM8_ID16_V8:
                return new STOWNBYNAME_IMM8_ID16_V8PandaInstruction(pc,opCode,param,pandaMethod);
            case STOWNBYNAME_IMM16_ID16_V8:
                return new STOWNBYNAME_IMM16_ID16_V8PandaInstruction(pc,opCode,param,pandaMethod);
            case DEFINEFUNC_IMM8_ID16_IMM8:
                return new DEFINEFUNC_IMM8_ID16_IMM8PandaInstruction(pc,opCode,param,pandaMethod);
            case DEFINEFUNC_IMM16_ID16_IMM8:
                return new DEFINEFUNC_IMM16_ID16_IMM8PandaInstruction(pc,opCode,param,pandaMethod);
            case GETUNMAPPEDARGS_NONE:
                return new GETUNMAPPEDARGS_NONEPandaInstruction(pc,opCode,param,pandaMethod);
            case CALLARG0_IMM8:
                return new CALLARG0_IMM8PandaInstruction(pc,opCode,param,pandaMethod);
            case CALLARG1_IMM8_V8:
                return new CALLARG1_IMM8_V8PandaInstruction(pc,opCode,param,pandaMethod);
            case CALLARGS2_IMM8_V8_V8:
                return new CALLARGS2_IMM8_V8_V8PandaInstruction(pc,opCode,param,pandaMethod);
            case CALLARGS3_IMM8_V8_V8_V8:
                return new CALLARGS3_IMM8_V8_V8_V8PandaInstruction(pc,opCode,param,pandaMethod);
            case CALLRANGE_IMM8_IMM8_V8:
                return new CALLRANGE_IMM8_IMM8_V8PandaInstruction(pc,opCode,param,pandaMethod);
            case WIDE_CALLRANGE_PREF_IMM16_V8:
                return new WIDE_CALLRANGE_PREF_IMM16_V8PandaInstruction(pc,opCode,param,pandaMethod);
            case LDSUPERBYNAME_IMM8_ID16:
                return new LDSUPERBYNAME_IMM8_ID16PandaInstruction(pc,opCode,param,pandaMethod);
            case LDSUPERBYNAME_IMM16_ID16:
                return new LDSUPERBYNAME_IMM16_ID16PandaInstruction(pc,opCode,param,pandaMethod);
            case ASYNCFUNCTIONENTER_NONE:
                return new ASYNCFUNCTIONENTER_NONEPandaInstruction(pc,opCode,param,pandaMethod);
            case ASYNCFUNCTIONRESOLVE_V8:
                return new ASYNCFUNCTIONRESOLVE_V8PandaInstruction(pc,opCode,param,pandaMethod);
            case ASYNCFUNCTIONREJECT_V8:
                return new ASYNCFUNCTIONREJECT_V8PandaInstruction(pc,opCode,param,pandaMethod);
            case ASYNCFUNCTIONAWAITUNCAUGHT_V8:
                return new ASYNCFUNCTIONAWAITUNCAUGHT_V8PandaInstruction(pc,opCode,param,pandaMethod);
            case SUSPENDGENERATOR_V8:
                return new SUSPENDGENERATOR_V8PandaInstruction(pc,opCode,param,pandaMethod);
            case RESUMEGENERATOR_NONE:
                return new RESUMEGENERATOR_NONEPandaInstruction(pc,opCode,param,pandaMethod);
            case GETRESUMEMODE_NONE:
                return new GETRESUMEMODE_NONEPandaInstruction(pc,opCode,param,pandaMethod);
            case THROW_PREF_NONE:
                return new THROW_PREF_NONEPandaInstruction(pc,opCode,param,pandaMethod);
            case CALLRUNTIME_NOTIFYCONCURRENTRESULT_PREF_NONE:
                return new CALLRUNTIME_NOTIFYCONCURRENTRESULT_PREF_NONEPandaInstruction(pc,opCode,param,pandaMethod);
            case DEFINEMETHOD_IMM8_ID16_IMM8:
                return new DEFINEMETHOD_IMM8_ID16_IMM8PandaInstruction(pc,opCode,param,pandaMethod);
            case DEFINEMETHOD_IMM16_ID16_IMM8:
                return new DEFINEMETHOD_IMM16_ID16_IMM8PandaInstruction(pc,opCode,param,pandaMethod);
            case DEFINEGETTERSETTERBYVALUE_V8_V8_V8_V8:
                return new DEFINEGETTERSETTERBYVALUE_V8_V8_V8_V8PandaInstruction(pc,opCode,param,pandaMethod);
            case INSTANCEOF_IMM8_V8:
                return new INSTANCEOF_IMM8_V8PandaInstruction(pc,opCode,param,pandaMethod);
            case ISIN_IMM8_V8:
                return new ISIN_IMM8_V8PandaInstruction(pc,opCode,param,pandaMethod);
            case DELOBJPROP_V8:
                return new DELOBJPROP_V8PandaInstruction(pc,opCode,param,pandaMethod);
            case GETITERATOR_IMM8:
                return new GETITERATOR_IMM8PandaInstruction(pc,opCode,param,pandaMethod);
            case GETITERATOR_IMM16:
                return new GETITERATOR_IMM16PandaInstruction(pc,opCode,param,pandaMethod);
            case GETPROPITERATOR_NONE:
                return new GETPROPITERATOR_NONEPandaInstruction(pc,opCode,param,pandaMethod);
            case GETNEXTPROPNAME_V8:
                return new GETNEXTPROPNAME_V8PandaInstruction(pc,opCode,param,pandaMethod);
            case POPLEXENV_NONE:
                return new POPLEXENV_NONEPandaInstruction(pc,opCode,param,pandaMethod);
            case APPLY_IMM8_V8_V8:
                return new APPLY_IMM8_V8_V8PandaInstruction(pc,opCode,param,pandaMethod);
            case STOWNBYVALUEWITHNAMESET_IMM8_V8_V8:
                return new STOWNBYVALUEWITHNAMESET_IMM8_V8_V8PandaInstruction(pc,opCode,param,pandaMethod);
            case STOWNBYVALUEWITHNAMESET_IMM16_V8_V8:
                return new STOWNBYVALUEWITHNAMESET_IMM16_V8_V8PandaInstruction(pc,opCode,param,pandaMethod);
            case WIDE_STMODULEVAR_PREF_IMM16:
                return new WIDE_STMODULEVAR_PREF_IMM16PandaInstruction(pc,opCode,param,pandaMethod);
            case WIDE_LDEXTERNALMODULEVAR_PREF_IMM16:
                return new WIDE_LDEXTERNALMODULEVAR_PREF_IMM16PandaInstruction(pc,opCode,param,pandaMethod);
            case STOWNBYVALUE_IMM8_V8_V8:
                return new STOWNBYVALUE_IMM8_V8_V8PandaInstruction(pc,opCode,param,pandaMethod);
            case STOWNBYVALUE_IMM16_V8_V8:
                return new STOWNBYVALUE_IMM16_V8_V8PandaInstruction(pc,opCode,param,pandaMethod);
            case COPYDATAPROPERTIES_V8:
                return new COPYDATAPROPERTIES_V8PandaInstruction(pc,opCode,param,pandaMethod);
            case STGLOBALVAR_IMM16_ID16:
                return new STGLOBALVAR_IMM16_ID16PandaInstruction(pc,opCode,param,pandaMethod);
            case LDGLOBALVAR_IMM16_ID16:
                return new LDGLOBALVAR_IMM16_ID16PandaInstruction(pc,opCode,param,pandaMethod);
            case TRYSTGLOBALBYNAME_IMM8_ID16:
                return new TRYSTGLOBALBYNAME_IMM8_ID16PandaInstruction(pc,opCode,param,pandaMethod);
            case TRYSTGLOBALBYNAME_IMM16_ID16:
                return new TRYSTGLOBALBYNAME_IMM16_ID16PandaInstruction(pc,opCode,param,pandaMethod);
            case GETMODULENAMESPACE_IMM8:
                return new GETMODULENAMESPACE_IMM8PandaInstruction(pc,opCode,param,pandaMethod);
            case WIDE_GETMODULENAMESPACE_PREF_IMM16:
                return new WIDE_GETMODULENAMESPACE_PREF_IMM16PandaInstruction(pc,opCode,param,pandaMethod);
            case DYNAMICIMPORT_NONE:
                return new DYNAMICIMPORT_NONEPandaInstruction(pc,opCode,param,pandaMethod);
            case SETOBJECTWITHPROTO_IMM8_V8:
                return new SETOBJECTWITHPROTO_IMM8_V8PandaInstruction(pc,opCode,param,pandaMethod);
            case SETOBJECTWITHPROTO_IMM16_V8:
                return new SETOBJECTWITHPROTO_IMM16_V8PandaInstruction(pc,opCode,param,pandaMethod);
            case STTOGLOBALRECORD_IMM16_ID16:
                return new STTOGLOBALRECORD_IMM16_ID16PandaInstruction(pc,opCode,param,pandaMethod);
            case STCONSTTOGLOBALRECORD_IMM16_ID16:
                return new STCONSTTOGLOBALRECORD_IMM16_ID16PandaInstruction(pc,opCode,param,pandaMethod);
            case NOP_NONE:
                return new NOP_NONEPandaInstruction(pc,opCode,param,pandaMethod);
            case CALLRUNTIME_CALLINIT_PREF_IMM8_V8:
                return new CALLRUNTIME_CALLINIT_PREF_IMM8_V8PandaInstruction(pc,opCode,param,pandaMethod);
            case DEFINEFIELDBYNAME_IMM8_ID16_V8:
                return new DEFINEFIELDBYNAME_IMM8_ID16_V8PandaInstruction(pc,opCode,param,pandaMethod);
            case CREATEGENERATOROBJ_V8:
                return new CREATEGENERATOROBJ_V8PandaInstruction(pc,opCode,param,pandaMethod);
            case CREATEITERRESULTOBJ_V8_V8:
                return new CREATEITERRESULTOBJ_V8_V8PandaInstruction(pc,opCode,param,pandaMethod);
            case DEFINEPROPERTYBYNAME_IMM8_ID16_V8:
                return new DEFINEPROPERTYBYNAME_IMM8_ID16_V8PandaInstruction(pc,opCode,param,pandaMethod);
            case CALLRUNTIME_ISFALSE_PREF_IMM8:
                return new CALLRUNTIME_ISFALSE_PREF_IMM8PandaInstruction(pc,opCode,param,pandaMethod);
            case CALLRUNTIME_ISTRUE_PREF_IMM8:
                return new CALLRUNTIME_ISTRUE_PREF_IMM8PandaInstruction(pc,opCode,param,pandaMethod);
            case NEWOBJAPPLY_IMM8_V8:
                return new NEWOBJAPPLY_IMM8_V8PandaInstruction(pc,opCode,param,pandaMethod);
            case NEWOBJAPPLY_IMM16_V8:
                return new NEWOBJAPPLY_IMM16_V8PandaInstruction(pc,opCode,param,pandaMethod);
            case STSUPERBYNAME_IMM8_ID16_V8:
                return new STSUPERBYNAME_IMM8_ID16_V8PandaInstruction(pc,opCode,param,pandaMethod);
            case STSUPERBYNAME_IMM16_ID16_V8:
                return new STSUPERBYNAME_IMM16_ID16_V8PandaInstruction(pc,opCode,param,pandaMethod);
            case CALLRUNTIME_LDLAZYMODULEVAR_PREF_IMM8:
                return new CALLRUNTIME_LDLAZYMODULEVAR_PREF_IMM8PandaInstruction(pc,opCode,param,pandaMethod);
            case CALLRUNTIME_WIDELDLAZYMODULEVAR_PREF_IMM16:
                return new CALLRUNTIME_WIDELDLAZYMODULEVAR_PREF_IMM16PandaInstruction(pc,opCode,param,pandaMethod);
            case CALLRUNTIME_LDSENDABLEEXTERNALMODULEVAR_PREF_IMM8:
                return new CALLRUNTIME_LDSENDABLEEXTERNALMODULEVAR_PREF_IMM8PandaInstruction(pc,opCode,param,pandaMethod);
            case CALLRUNTIME_WIDELDSENDABLEEXTERNALMODULEVAR_PREF_IMM16:
                return new CALLRUNTIME_WIDELDSENDABLEEXTERNALMODULEVAR_PREF_IMM16PandaInstruction(pc,opCode,param,pandaMethod);
            case CALLRUNTIME_LDLAZYSENDABLEMODULEVAR_PREF_IMM8:
                return new CALLRUNTIME_LDLAZYSENDABLEMODULEVAR_PREF_IMM8PandaInstruction(pc,opCode,param,pandaMethod);
            case CALLRUNTIME_WIDELDLAZYSENDABLEMODULEVAR_PREF_IMM16:
                return new CALLRUNTIME_WIDELDLAZYSENDABLEMODULEVAR_PREF_IMM16PandaInstruction(pc,opCode,param,pandaMethod);
            case CALLRUNTIME_DEFINESENDABLECLASS_PREF_IMM16_ID16_ID16_IMM16_V8:
                return new CALLRUNTIME_DEFINESENDABLECLASS_PREF_IMM16_ID16_ID16_IMM16_V8PandaInstruction(pc,opCode,param,pandaMethod);
            case CALLRUNTIME_NEWSENDABLEENV_PREF_IMM8:
                return new CALLRUNTIME_NEWSENDABLEENV_PREF_IMM8PandaInstruction(pc,opCode,param,pandaMethod);
            case CALLRUNTIME_WIDENEWSENDABLEENV_PREF_IMM16:
                return new CALLRUNTIME_WIDENEWSENDABLEENV_PREF_IMM16PandaInstruction(pc,opCode,param,pandaMethod);
            case CALLRUNTIME_STSENDABLEVAR_PREF_IMM4_IMM4:
                return new CALLRUNTIME_STSENDABLEVAR_PREF_IMM4_IMM4PandaInstruction(pc,opCode,param,pandaMethod);
            case CALLRUNTIME_STSENDABLEVAR_PREF_IMM8_IMM8:
                return new CALLRUNTIME_STSENDABLEVAR_PREF_IMM8_IMM8PandaInstruction(pc,opCode,param,pandaMethod);
            case CALLRUNTIME_WIDESTSENDABLEVAR_PREF_IMM16_IMM16:
                return new CALLRUNTIME_WIDESTSENDABLEVAR_PREF_IMM16_IMM16PandaInstruction(pc,opCode,param,pandaMethod);
            case CALLRUNTIME_LDSENDABLEVAR_PREF_IMM4_IMM4:
                return new CALLRUNTIME_LDSENDABLEVAR_PREF_IMM4_IMM4PandaInstruction(pc, opCode, param, pandaMethod);
            case CALLRUNTIME_LDSENDABLEVAR_PREF_IMM8_IMM8:
                return new CALLRUNTIME_LDSENDABLEVAR_PREF_IMM8_IMM8PandaInstruction(pc,opCode, param, pandaMethod);
            case CALLRUNTIME_WIDELDSENDABLEVAR_PREF_IMM16_IMM16:
                return new CALLRUNTIME_WIDELDSENDABLEVAR_PREF_IMM16_IMM16PandaInstruction(pc, opCode, param, pandaMethod);
            case CALLRUNTIME_LDSENDABLECLASS_PREF_IMM16:
                return new CALLRUNTIME_LDSENDABLECLASS_PREF_IMM16PandaInstruction(pc,opCode,param,pandaMethod);
            case CALLRUNTIME_TOPROPERTYKEY_PREF_NONE:
                return new CALLRUNTIME_TOPROPERTYKEY_PREF_NONEPandaInstruction(pc,opCode,param,pandaMethod);
            case CALLRUNTIME_DEFINEFIELDBYINDEX_PREF_IMM8_IMM32_V8:
                return new CALLRUNTIME_DEFINEFIELDBYINDEX_PREF_IMM8_IMM32_V8PandaInstruction(pc,opCode,param,pandaMethod);
            case CALLRUNTIME_DEFINEFIELDBYVALUE_PREF_IMM8_V8_V8:
                return new CALLRUNTIME_DEFINEFIELDBYVALUE_PREF_IMM8_V8_V8PandaInstruction(pc,opCode,param,pandaMethod);
            case CREATEOBJECTWITHEXCLUDEDKEYS_IMM8_V8_V8:
                return new CREATEOBJECTWITHEXCLUDEDKEYS_IMM8_V8_V8PandaInstruction(pc,opCode,param,pandaMethod);
            case WIDE_CREATEOBJECTWITHEXCLUDEDKEYS_PREF_IMM16_V8_V8:
                return new WIDE_CREATEOBJECTWITHEXCLUDEDKEYS_PREF_IMM16_V8_V8PandaInstruction(pc,opCode,param,pandaMethod);
            case LDSUPERBYVALUE_IMM16_V8:
                return new LDSUPERBYVALUE_IMM16_V8PandaInstruction(pc,opCode,param,pandaMethod);
            case LDSUPERBYVALUE_IMM8_V8:
                return new LDSUPERBYVALUE_IMM8_V8PandaInstruction(pc,opCode,param,pandaMethod);

            default:
//                throw new PandaParseException(opCode.getName() + " not recognized!");
                System.out.println(opCode.getName() + " not recognized!");
                return new PandaInstruction(pc,opCode,param,pandaMethod);

        }
    }

}
