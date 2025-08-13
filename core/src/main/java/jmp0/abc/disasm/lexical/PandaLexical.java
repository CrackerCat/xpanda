package jmp0.abc.disasm.lexical;

import jmp0.abc.disasm.block.PandaIRBasicBlock;
import jmp0.abc.disasm.block.PandaIRCFG;
import jmp0.abc.disasm.ins.PandaInstruction;
import jmp0.abc.disasm.ins.async.ASYNCFUNCTIONRESOLVE_V8PandaInstruction;
import jmp0.abc.disasm.ins.creaters.NewLexEnvPandaInstruction;
import jmp0.abc.disasm.ins.creaters.obj.CreateObjectPandaInstruction;
import jmp0.abc.disasm.ins.definition.DEFINECLASSWITHBUFFERPandaInstruction;
import jmp0.abc.disasm.ins.definition.DefineFuncPandaInstruction;
import jmp0.abc.disasm.ins.load.POPLEXENV_NONEPandaInstruction;
import jmp0.abc.disasm.ins.mov.MovPandaInstruction;
import jmp0.abc.disasm.ins.store.StorePandaInstruction;
import jmp0.abc.disasm.ins.trw.AssertPandaInstruction;
import jmp0.abc.disasm.ins.visitors.LDLOCALMODULEVAR_IMM8PandaInstruction;
import jmp0.abc.disasm.ins.visitors.ldlex.LoadLexPandaInstruction;
import jmp0.abc.disasm.ins.visitors.stlex.StoreLexPandaInstruction;
import jmp0.abc.file.PandaFile;
import jmp0.abc.file.clazz.PandaClass;
import jmp0.abc.file.desc.Offset;
import jmp0.abc.file.method.PandaMethod;
import jmp0.abc.file.method.PandaMethodCatchBlock;
import jmp0.abc.file.method.PandaMethodTryBlock;
import jmp0.abc.util.PandaLogger;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class PandaLexical {
    private final PandaLogger logger = new PandaLogger(PandaLexical.class);
    private final PandaClass pandaClass;
    private boolean finished = false;
    private final HashMap<PandaMethod, PandaLexicalDescription> lexicalMap = new HashMap<>();
    private final HashMap<PandaMethod,HashMap<String,String>> typeMap = new HashMap<>();

    public PandaLexical(PandaClass pandaClass) {
        this.pandaClass = pandaClass;
        analyze();
    }

    private LinkedList<PandaMethod> getInnerDeclaredMethods(PandaInstruction instruction) {
        LinkedList<PandaMethod> pandaMethods = new LinkedList<>();
        //method in object
        if (instruction instanceof CreateObjectPandaInstruction){
            for (Offset value : ((CreateObjectPandaInstruction) instruction).getObjMap().values()) {
                if (value instanceof PandaMethod) pandaMethods.add((PandaMethod) value);
            }
        // method in class
        }else if (instruction instanceof DEFINECLASSWITHBUFFERPandaInstruction){
            pandaMethods.add(((DEFINECLASSWITHBUFFERPandaInstruction) instruction).getConstructorMethod());
            for (DEFINECLASSWITHBUFFERPandaInstruction.MethodDescription methodDescription
                    : ((DEFINECLASSWITHBUFFERPandaInstruction) instruction).getMethodDescriptions()) {
                pandaMethods.add(methodDescription.getMethod());
            }
            for (DEFINECLASSWITHBUFFERPandaInstruction.MethodDescription staticMethodDescription
                    : ((DEFINECLASSWITHBUFFERPandaInstruction) instruction).getStaticMethodDescriptions()) {
                pandaMethods.add(staticMethodDescription.getMethod());
            }
        // method in inner define
        }else if (instruction instanceof DefineFuncPandaInstruction){
            pandaMethods.add(((DefineFuncPandaInstruction) instruction).getFunc());
        }
        return pandaMethods;
    }

    private int checkNeedHandleLevel(PandaMethod nowMethod,PandaInstruction instruction,boolean ignorePop,int nowLevel){
        if (instruction instanceof NewLexEnvPandaInstruction){
            lexicalMap.get(nowMethod).recordLevel(instruction.getPC(),nowLevel);
            nowLevel++;
        }else if (instruction instanceof StoreLexPandaInstruction || instruction instanceof LoadLexPandaInstruction){
            lexicalMap.get(nowMethod).recordLevel(instruction.getPC(),nowLevel);
        }else if (instruction instanceof POPLEXENV_NONEPandaInstruction && !ignorePop){
            if (nowLevel==0){
                logger.logD("checkNeedHandleLevel","fixme nowLevel equal 0");
                return 0;
            }
            nowLevel--;
        }
        return nowLevel;
    }
    private boolean checkAsyncFunctionResolveContain(PandaIRBasicBlock pandaIRBasicBlock){
        boolean isReturn = pandaIRBasicBlock.getTerminator().getOpCode().isReturnIns();
        for (PandaInstruction pandaInstruction : pandaIRBasicBlock.getPandaInstructions()) {
            if (pandaInstruction instanceof ASYNCFUNCTIONRESOLVE_V8PandaInstruction && isReturn){
                return true;
            }
        }
        return false;
    }

    private void checkAddDebugInfo(PandaIRBasicBlock block, AssertPandaInstruction instruction){
        String type = instruction.getTypeString().getContent();
        PandaInstruction preInstruction = block.getPreInstructionByPc(instruction.getPC());
        if (preInstruction instanceof StorePandaInstruction){
            String name = ((StorePandaInstruction) preInstruction).getDestObj().toString();
            addTypeInfo(block.getParent(), name, type);
        }else if (preInstruction instanceof LoadLexPandaInstruction){;
            addTypeInfo(block.getParent(), ((LoadLexPandaInstruction) preInstruction).toSignature(), type);
        }else if (preInstruction instanceof MovPandaInstruction){
            String name = ((MovPandaInstruction) preInstruction).getDestObj().toString();
            addTypeInfo(block.getParent(), name, type);
        }
    }

    public void analyze() {
        if (finished) return;
        PandaMethod mainMethod = pandaClass.getPandaMethods().get(PandaFile.ENTRY_FUNCTION_MAIN);
        if (mainMethod == null) return;
        analyzeWithPandaMethod(mainMethod,0);
        finished = true;
    }

    private void analyzeWithPandaMethod(PandaMethod mth,int nowLevel) {
        PandaIRCFG pandaIRCFG = mth.disAssemble();
        lexicalMap.put(mth,new PandaLexicalDescription(pandaIRCFG));
        typeMap.put(mth,new HashMap<>());
        //generate exception handle block,just ignore it...
        HashSet<PandaIRBasicBlock> exceptionHandleBlockSet = new HashSet<>();
        PandaMethodTryBlock[] pandaMethodTryBlock = mth.getMethodCode().getPandaMethodTryBlocks();
        if (pandaMethodTryBlock != null) {
            for (PandaMethodTryBlock methodTryBlock : pandaMethodTryBlock) {
                for (PandaMethodCatchBlock pandaMethodCatchBlock : methodTryBlock.getPandaMethodCatchBlocks()) {
                    int pc = pandaMethodCatchBlock.getHandlerPC().intValue();
                    PandaIRBasicBlock exceptionHandleBlock = pandaIRCFG.getBasicBlockByPC(pc);
                    if (exceptionHandleBlock != null) exceptionHandleBlockSet.add(exceptionHandleBlock);
                }
            }
        }

        for (PandaIRBasicBlock pandaIRBasicBlock : pandaIRCFG.getPandaIRBasicBlocks()) {
            boolean ignorePop = false;
            if (exceptionHandleBlockSet.contains(pandaIRBasicBlock) || checkAsyncFunctionResolveContain(pandaIRBasicBlock)) ignorePop = true;
            for (PandaInstruction pandaInstruction : pandaIRBasicBlock.getPandaInstructions()) {
                if (pandaInstruction instanceof AssertPandaInstruction){
                    checkAddDebugInfo(pandaIRBasicBlock, (AssertPandaInstruction) pandaInstruction);
                }
                //check lexical instruction before
                nowLevel = checkNeedHandleLevel(mth,pandaInstruction,ignorePop,nowLevel);

                LinkedList<PandaMethod> declaredMethods = getInnerDeclaredMethods(pandaInstruction);
                for (PandaMethod method : declaredMethods) {
                    analyzeWithPandaMethod(method,nowLevel);
                }
            }

        }
    }

    public int getLevel(PandaMethod method,int pc) {
        return lexicalMap.get(method).getLevel(pc);
    }

    public PandaIRCFG getPandaIRCFG(PandaMethod pandaMethod) {
        return this.lexicalMap.get(pandaMethod).getPandaIRCFG();
    }

    private void addTypeInfo(PandaMethod method,String name,String type){
        typeMap.get(method).put(name,type);
    }

    public String getTypeInfo(PandaMethod method,String name){
        return typeMap.get(method).get(name);
    }
}
