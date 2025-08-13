package jmp0.abc.decompiler;

import jmp0.abc.decompiler.codegen.CodeGenerator;
import jmp0.abc.decompiler.ins.*;
import jmp0.abc.decompiler.simple.SimpleAnalysis;
import jmp0.abc.decompiler.structure.StructureAnalysis;
import jmp0.abc.disasm.block.PandaIRBasicBlock;
import jmp0.abc.disasm.block.PandaIRCFG;
import jmp0.abc.disasm.ins.NOP_NONEPandaInstruction;
import jmp0.abc.disasm.ins.PandaInstruction;
import jmp0.abc.disasm.ins.abs.AbstractOperationPandaInstruction;
import jmp0.abc.disasm.ins.async.AsyncPandaInstruction;
import jmp0.abc.disasm.ins.binary.BinaryOperationPandaInstruction;
import jmp0.abc.disasm.ins.call.CallPandaInstruction;
import jmp0.abc.disasm.ins.call.spr.SuperCallPandaInstruction;
import jmp0.abc.disasm.ins.call.spr.SuperCallSpreadPandaInstruction;
import jmp0.abc.disasm.ins.callruntime.CallRuntimePandaInstruction;
import jmp0.abc.disasm.ins.creaters.NewLexEnvPandaInstruction;
import jmp0.abc.disasm.ins.creaters.NewObjectRangePandaInstruction;
import jmp0.abc.disasm.ins.creaters.array.CreateArrayPandaInstruction;
import jmp0.abc.disasm.ins.creaters.obj.CreateObjectPandaInstruction;
import jmp0.abc.disasm.ins.creaters.spread.NewObjectSpreadPandaInstruction;
import jmp0.abc.disasm.ins.definition.DEFINECLASSWITHBUFFERPandaInstruction;
import jmp0.abc.disasm.ins.definition.DEFINEGETTERSETTERBYVALUE_V8_V8_V8_V8PandaInstruction;
import jmp0.abc.disasm.ins.definition.DefineFuncPandaInstruction;
import jmp0.abc.disasm.ins.generator.GeneratorPandaInstruction;
import jmp0.abc.disasm.ins.iterator.GetIteratorPandaInstruction;
import jmp0.abc.disasm.ins.iterator.GetNextPandaInstruction;
import jmp0.abc.disasm.ins.jump.JumpDirectPandaInstruction;
import jmp0.abc.disasm.ins.jump.JumpEqualPandaInstruction;
import jmp0.abc.disasm.ins.jump.JumpNotEqualPandaInstruction;
import jmp0.abc.disasm.ins.load.LoadPandaInstruction;
import jmp0.abc.disasm.ins.load.POPLEXENV_NONEPandaInstruction;
import jmp0.abc.disasm.ins.mov.MovPandaInstruction;
import jmp0.abc.disasm.ins.ret.ReturnPandaInstruction;
import jmp0.abc.disasm.ins.store.StorePandaInstruction;
import jmp0.abc.disasm.ins.trw.AssertPandaInstruction;
import jmp0.abc.disasm.ins.trw.THROW_PREF_NONEPandaInstruction;
import jmp0.abc.disasm.ins.trw.ThrowIfPandaInstruction;
import jmp0.abc.disasm.ins.unary.UnaryPandaInstruction;
import jmp0.abc.disasm.ins.visitors.LoadModuleVarPandaInstruction;
import jmp0.abc.disasm.ins.visitors.array.StoreArraySpreadPandaInstruction;
import jmp0.abc.disasm.ins.visitors.array.StoreByIndexPandaInstruction;
import jmp0.abc.disasm.ins.visitors.copyobj.CopyDataPropertiesPandaInstruction;
import jmp0.abc.disasm.ins.visitors.copyrestargs.CopyRestArgsPandaInstruction;
import jmp0.abc.disasm.ins.visitors.delobj.DeleteObjectPandaInstruction;
import jmp0.abc.disasm.ins.visitors.global.LoadGlobalPandaInstruction;
import jmp0.abc.disasm.ins.visitors.ldext.LoadExternalModuleVarPandaInstruction;
import jmp0.abc.disasm.ins.visitors.ldlex.LoadLexPandaInstruction;
import jmp0.abc.disasm.ins.visitors.ldobj.LoadObjectByPandaInstruction;
import jmp0.abc.disasm.ins.visitors.ldspr.LoadSuperPandaInstruction;
import jmp0.abc.disasm.ins.visitors.setobj.SetObjectWithProtoPandaInstruction;
import jmp0.abc.disasm.ins.visitors.stlex.StoreLexPandaInstruction;
import jmp0.abc.disasm.ins.visitors.stmod.StoreModuleVarPandaInstruction;
import jmp0.abc.disasm.ins.visitors.stobj.StoreObjectPandaInstruction;
import jmp0.abc.disasm.ins.visitors.stobj.spr.LoadSuperObjectPandaInstruction;
import jmp0.abc.disasm.ins.visitors.stobj.spr.StoreSuperObjectPandaInstruction;
import jmp0.abc.disasm.ins.visitors.stobjrec.StoreObjectRecordPandaInstruction;
import jmp0.abc.disasm.ins.yield.CreateGeneratorObjectPandaInstruction;
import jmp0.abc.disasm.lexical.PandaLexical;
import jmp0.abc.file.method.PandaMethod;
import jmp0.abc.util.PandaLogger;
import lombok.Getter;
import lombok.SneakyThrows;

import java.util.HashMap;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
@Getter
public final class PandaDecompilerMethodHandler {
    private final PandaLogger logger = new PandaLogger(PandaDecompilerMethodHandler.class);
    private final CodeGenerator codeGenerator;
    private static int incNum = 0;
    private static final HashMap<Class<?>, IInstructionHandle<?>> handleHashMap = new HashMap<>();
    static {
        handleHashMap.put(MovPandaInstruction.class,new MovPandaInstructionHandle());
        handleHashMap.put(LoadPandaInstruction.class,new LoadPandaInstructionHandle());
        handleHashMap.put(StorePandaInstruction.class,new StorePandaInstructionHandle());
        handleHashMap.put(DEFINECLASSWITHBUFFERPandaInstruction.class,new DEFINECLASSWITHBUFFERPandaInstructionHandle());
        handleHashMap.put(StoreObjectPandaInstruction.class,new StoreObjectPandaInstructionHandle());
        handleHashMap.put(LoadObjectByPandaInstruction.class,new LoadObjectByPandaInstructionHandle());
        handleHashMap.put(CallPandaInstruction.class, new CallPandaInstructionHandle());
        handleHashMap.put(ReturnPandaInstruction.class, new ReturnPandaInstructionHandle());
        handleHashMap.put(LoadExternalModuleVarPandaInstruction.class,new LoadExternalModuleVarPandaInstructionHandle());
        handleHashMap.put(CopyRestArgsPandaInstruction.class,new CopyRestArgsPandaInstructionHandle());
        handleHashMap.put(CreateArrayPandaInstruction.class,new CreateArrayPandaInstructionHandle());
        handleHashMap.put(StoreArraySpreadPandaInstruction.class,new StoreArraySpreadPandaInstructionHandle());
        handleHashMap.put(SuperCallSpreadPandaInstruction.class,new SuperCallSpreadPandaInstructionHandle());
        handleHashMap.put(StoreModuleVarPandaInstruction.class,new StoreModuleVarPandaInstructionHandle());
        handleHashMap.put(DefineFuncPandaInstruction.class,new DefineFuncPandaInstructionHandle());
        handleHashMap.put(CreateObjectPandaInstruction.class,new CreateObjectPandaInstructionHandle());
        handleHashMap.put(LoadModuleVarPandaInstruction.class,new LoadModuleVarPandaInstructionHandle());
        handleHashMap.put(BinaryOperationPandaInstruction.class, new BinaryOperationPandaInstructionHandle());
        handleHashMap.put(NewLexEnvPandaInstruction.class,new NewLexEnvPandaInstructionHandle());
        handleHashMap.put(StoreLexPandaInstruction.class,new StoreLexPandaInstructionHandle());
        handleHashMap.put(LoadGlobalPandaInstruction.class,new LoadGlobalPandaInstructionHandle());
        handleHashMap.put(SuperCallPandaInstruction.class,new SuperCallPandaInstructionHandle());
        handleHashMap.put(DEFINEGETTERSETTERBYVALUE_V8_V8_V8_V8PandaInstruction.class,new DEFINEGETTERSETTERBYVALUE_V8_V8_V8_V8PandaInstructionHandle());
        handleHashMap.put(LoadLexPandaInstruction.class,new LoadLexPandaInstructionHandle());
        handleHashMap.put(NewObjectRangePandaInstruction.class,new NewObjectRangePandaInstructionHandle());
        handleHashMap.put(StoreByIndexPandaInstruction.class,new StoreByIndexPandaInstructionHandle());
        handleHashMap.put(UnaryPandaInstruction.class,new UnaryPandaInstructionHandle());
        handleHashMap.put(DeleteObjectPandaInstruction.class,new DeleteObjectPandaInstructionHandle());
        handleHashMap.put(LoadSuperPandaInstruction.class,new LoadSuperPandaInstructionHandle());
        handleHashMap.put(THROW_PREF_NONEPandaInstruction.class,new THROW_PREF_NONEPandaInstructionHandle());
        handleHashMap.put(CopyDataPropertiesPandaInstruction.class,new CopyDataPropertiesPandaInstructionHandle());
        handleHashMap.put(GetIteratorPandaInstruction.class,new GetIteratorPandaInstructionHandle());
        handleHashMap.put(GetNextPandaInstruction.class,new GetNextPandaInstructionHandle());
        handleHashMap.put(AsyncPandaInstruction.class,new AsyncPandaInstructionHandle());
        handleHashMap.put(GeneratorPandaInstruction.class,new GeneratorPandaInstructionHandle());
        handleHashMap.put(StoreObjectRecordPandaInstruction.class,new StoreObjectRecordPandaInstructionHandle());
        handleHashMap.put(SetObjectWithProtoPandaInstruction.class,new SetObjectWithProtoPandaInstructionHandle());
        handleHashMap.put(CallRuntimePandaInstruction.class,new CallRuntimePandaInstructionHandle());
        handleHashMap.put(CreateGeneratorObjectPandaInstruction.class,new CreateGeneratorObjectPandaInstructionHandle());
        handleHashMap.put(NewObjectSpreadPandaInstruction.class,new NewObjectSpreadPandaInstructionHandle());
        handleHashMap.put(StoreSuperObjectPandaInstruction.class,new StoreSuperObjectPandaInstructionHandle());
        handleHashMap.put(LoadSuperObjectPandaInstruction.class,new LoadSuperObjectPandaInstructionHandle());
        handleHashMap.put(POPLEXENV_NONEPandaInstruction.class,new POPLEXENV_NONEPandaInstructionHandle());

        handleHashMap.put(AssertPandaInstruction.class,new DoNothingPandaInstructionHandle());
        handleHashMap.put(NOP_NONEPandaInstruction.class,new DoNothingPandaInstructionHandle());
        handleHashMap.put(JumpEqualPandaInstruction.class,new DoNothingPandaInstructionHandle());
        handleHashMap.put(JumpNotEqualPandaInstruction.class,new DoNothingPandaInstructionHandle());
        handleHashMap.put(JumpDirectPandaInstruction.class,new DoNothingPandaInstructionHandle());
        handleHashMap.put(ThrowIfPandaInstruction.class,new DoNothingPandaInstructionHandle());
        handleHashMap.put(AbstractOperationPandaInstruction.class,new DoNothingPandaInstructionHandle());
    }

    public PandaDecompilerMethodHandler(CodeGenerator codeGenerator){
        this.codeGenerator = codeGenerator;
    }

    public int getNextID(){
        return incNum++;
    }

    @SneakyThrows
    @SuppressWarnings({"unchecked","rawtypes"})
    public void handleBlock(PandaIRBasicBlock block, PandaLexical pandaLexical, Object node){
        PandaInstruction[] instructions = block.getPandaInstructions();
        for (PandaInstruction instruction : instructions) {
            Class<?> superClass = instruction.getClass().getSuperclass();
            IInstructionHandle handle = handleHashMap.get(superClass);
            if (handle == null) handle = handleHashMap.get(instruction.getClass());
            if (handle == null){
                throw new PandaDecompileException(instruction.getOpCode().getName() + " not handled!");
            }
            HandleStatus handleStatus = handle.handle(instruction,block,this,pandaLexical, node);
            if (handleStatus.getStatus() == HandleStatus.STATUS.STOP) return;
        }
    }

    public void insertFunctionPreamble(PandaMethod method,PandaLexical pandaLexical, Object node){
        int numVRegs = method.getMethodCode().getNumVregs().intValue();
        Object arrayObject = codeGenerator.arrayObject();
        for (int i = 0; i < numVRegs; i++) {
            Object variableDeclaratorNode;
            String name = "v"+i;
            String type = pandaLexical.getTypeInfo(method,name);
            if (type == null){
                variableDeclaratorNode = codeGenerator.getNodeFactory().variableDeclaratorNode(codeGenerator.getNodeFactory().identifierNode(name));
            }else {
                variableDeclaratorNode = codeGenerator.getNodeFactory().variableDeclaratorNode(codeGenerator.getNodeFactory().identifierNode(name,type));
            }
            codeGenerator.addToArray(arrayObject,variableDeclaratorNode);
        }
        codeGenerator.addToArray(arrayObject,codeGenerator.getNodeFactory().variableDeclaratorNode(codeGenerator.getNodeFactory().identifierNode("acc")));
        //add reserved param
        for (int i = 0; i < 3; i++) {
            codeGenerator.addToArray(arrayObject,codeGenerator.getNodeFactory().variableDeclaratorNode(codeGenerator.getNodeFactory().identifierNode("panda_jmp0_reserved_param_p"+i)));
        }
        Object variableDeclarationNode = codeGenerator.getNodeFactory().variableDeclarationNode(arrayObject,"let");
        codeGenerator.getNodeUtils().insertNodeToBody(variableDeclarationNode,node);
        Object assignmentExpressionNode = codeGenerator.getNodeFactory().assignmentExpressionNode("=",codeGenerator.getNodeFactory().identifierNode("panda_jmp0_reserved_param_p2"),codeGenerator.getNodeFactory().thisExpressionNode());
        Object expressionStatementNode = codeGenerator.getNodeFactory().expressionStatementNode(assignmentExpressionNode);
        codeGenerator.getNodeUtils().insertNodeToBody(expressionStatementNode,node);
    }

    private boolean isNumeric(String str) {
        return str.matches("\\d+"); // 正则表达式 \d 是数字，+ 表示一个或多个
    }

    public Object decompileFunction(PandaMethod inner,PandaLexical pandaLexical){
        PandaIRCFG pandaIRCFG = pandaLexical.getPandaIRCFG(inner);
        int numArgs = inner.getMethodCode().getNumArgs().intValue() - 3;
        Object arrayObject = codeGenerator.arrayObject();
        for (int i = 0; i < numArgs; i++) {
            codeGenerator.addToArray(arrayObject, codeGenerator.getNodeFactory().identifierNode("p" + i));
        }
        Object block = codeGenerator.getNodeFactory().blockStatementNode();
        insertFunctionPreamble(inner,pandaLexical,block);
        try {
            new StructureAnalysis(pandaIRCFG,pandaIRCFG.getEntryBlock(),pandaIRCFG.getLastBlock()).analysis(this,pandaLexical,block);
        }catch (Exception e){
            logger.logD("decompileFunction","failed! use SimpleAnalysis");
            new SimpleAnalysis(pandaIRCFG).analysis(this,pandaLexical,block);
        }
        if (inner.getName().getContent().startsWith("#") || isNumeric(inner.getName().getContent())) return codeGenerator.getNodeFactory().functionExpressionNode(null,arrayObject,block);
        else if(inner.getName().getContent().equals("default")) return codeGenerator.getNodeFactory().functionExpressionNode(
                codeGenerator.getNodeFactory().identifierNode("panda_jmp0_reserved_" + inner.getName().getContent()),arrayObject,block);
        else return codeGenerator.getNodeFactory().functionExpressionNode(codeGenerator.getNodeFactory().identifierNode(inner.getName().getContent()),arrayObject,block);
    }
}
