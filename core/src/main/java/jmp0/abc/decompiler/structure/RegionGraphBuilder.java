package jmp0.abc.decompiler.structure;

import com.google.common.base.Function;
import com.google.common.graph.*;
import jmp0.abc.decompiler.PandaDecompileException;
import jmp0.abc.disasm.block.PandaIRBasicBlock;
import jmp0.abc.disasm.block.PandaIRCFG;
import jmp0.abc.disasm.ins.PandaInstruction;
import jmp0.abc.disasm.ins.async.ASYNCFUNCTIONAWAITUNCAUGHT_V8PandaInstruction;
import jmp0.abc.disasm.ins.async.ASYNCFUNCTIONREJECT_V8PandaInstruction;
import jmp0.abc.disasm.ins.iterator.GetIteratorPandaInstruction;
import jmp0.abc.disasm.ins.load.LDHOLE_NONEPandaInstruction;
import jmp0.abc.disasm.ins.trw.THROW_PREF_NONEPandaInstruction;
import jmp0.abc.disasm.ins.visitors.ldobj.LoadObjectByPandaInstruction;
import jmp0.abc.disasm.ins.yield.CreateGeneratorObjectPandaInstruction;
import jmp0.abc.disasm.param.IPandaInstructionParam;
import jmp0.abc.file.method.PandaMethodCatchBlock;
import jmp0.abc.file.method.PandaMethodTryBlock;
import jmp0.abc.util.PandaLogger;
import lombok.Getter;
import lombok.SneakyThrows;

import java.util.*;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class RegionGraphBuilder {
    private final PandaLogger logger = new PandaLogger(RegionGraphBuilder.class);
    private final MutableValueGraph<PandaIRBasicBlock, Boolean> graph;

    private final HashMap<PandaIRBasicBlock,Region> btor = new HashMap<>();
    private final MutableValueGraph<Region,Boolean> regionGraph = ValueGraphBuilder.directed().allowsSelfLoops(true).nodeOrder(ElementOrder.stable()).incidentEdgeOrder(ElementOrder.stable()).build();
    private final RegionFactory factory;
    private final PandaIRBasicBlock entryBlock;
    private final PandaIRBasicBlock lastBlock;
    private final TreeMap<PandaIRBasicBlock,PandaIRBasicBlock> exceptionHandleBlocks = new TreeMap<>(new Comparator<PandaIRBasicBlock>() {
        @Override
        public int compare(PandaIRBasicBlock o1, PandaIRBasicBlock o2) {
            //fixme too lazy
            return o2.getStartPC() - o1.getStartPC() ;
        }
    });

    private boolean isAsyncFunction = false;
    private boolean isGeneratorFunction = false;

    public RegionGraphBuilder(PandaIRCFG pandaIRCFG, PandaIRBasicBlock entryBlock, PandaIRBasicBlock lastBlock){
        this.graph = pandaIRCFG.getGraph();
        this.entryBlock = entryBlock;
        this.lastBlock = lastBlock;
        this.factory = new RegionFactory(pandaIRCFG.getGraph());
        if (pandaIRCFG.getPandaMethod().hasException()){
            for (PandaMethodTryBlock pandaMethodTryBlock : pandaIRCFG.getPandaMethod().getMethodCode().getPandaMethodTryBlocks()) {
                //fixme just one
                PandaMethodCatchBlock pandaMethodCatchBlock = pandaMethodTryBlock.getPandaMethodCatchBlocks()[0];
                PandaIRBasicBlock tryEntryBlock = pandaIRCFG.getBasicBlockByPC(pandaMethodTryBlock.getStartPC().intValue());
                int tryCodeLength = pandaMethodTryBlock.getLength().intValue();
                PandaIRBasicBlock handleEntryBlock = pandaIRCFG.getBasicBlockByPC(pandaMethodCatchBlock.getHandlerPC().intValue());
                int handleCodeLength =  pandaMethodCatchBlock.getCodeSize().intValue();
                if (checkIsForInException(pandaIRCFG,tryEntryBlock)){
                    logger.logD("RegionGraphBuilder[checkIsForInException] ignore exception",String.format("%s %s",tryEntryBlock.getName(),handleEntryBlock.getName()));
                    continue;
                }
                if (checkIsForOfException(pandaIRCFG,tryEntryBlock,handleEntryBlock)){
                    logger.logD("RegionGraphBuilder[checkIsForOfException] ignore exception",String.format("%s %s",tryEntryBlock.getName(),handleEntryBlock.getName()));
                    continue;
                }
                if (checkIsAsyncException(handleEntryBlock)){
                    logger.logD("RegionGraphBuilder[checkIsAsyncException] ignore exception",String.format("%s %s",tryEntryBlock.getName(),handleEntryBlock.getName()));
                    isAsyncFunction = true;
                    continue;
                }
                if (checkIsGeneratorException(tryEntryBlock)){
                    logger.logD("RegionGraphBuilder[checkIsGeneratorException] ignore exception",String.format("%s %s",tryEntryBlock.getName(),handleEntryBlock.getName()));
                    isGeneratorFunction = true;
                    continue;
                }
                if (tryCodeLength == 0 || handleCodeLength == 0){
                    logger.logD("RegionGraphBuilder[checkIsForInException] ignore exception",String.format("%s %s, because of tryCodeLength or handleCodeLength is null.",tryEntryBlock.getName(),handleEntryBlock.getName()));
                    continue;
                }
                logger.logD("RegionGraphBuilder",String.format("add exception %s %s",tryEntryBlock.getName(),handleEntryBlock.getName()));
                exceptionHandleBlocks.put(handleEntryBlock,tryEntryBlock);
            }
        }
    }

    @SneakyThrows
    public MutableValueGraph<Region,Boolean> build(){
        for (PandaIRBasicBlock node : this.graph.nodes()) {
            if (this.graph.inDegree(node) == 0 && node != entryBlock
            && node != lastBlock && !exceptionHandleBlocks.containsKey(node)){
                continue;
            }
            Region region = factory.create(node);
            if (exceptionHandleBlocks.containsKey(node)){
                region.setExceptionLandingPad(true);
            }
            btor.put(node,region);
            this.regionGraph.addNode(region);
        }

        for (PandaIRBasicBlock node : this.graph.nodes()) {
            if (this.graph.inDegree(node) == 0 && node != entryBlock
                    && node != lastBlock && !exceptionHandleBlocks.containsKey(node)) {
                continue;
            }
            Region from = this.btor.get(node);
            for (PandaIRBasicBlock successor : this.graph.successors(node)) {
                Region to = btor.get(successor);
                Optional<Boolean> value = this.graph.edgeValue(node,successor);
                regionGraph.putEdgeValue(from,to,value.get());
            }
            if (this.graph.outDegree(node) == 0){
                from.setType(Region.RegionType.Tail);
            }
        }
        //fixme remove dead code
        while (true){
            boolean flag = false;
            for (Region node : this.regionGraph.nodes()) {
                if (this.regionGraph.inDegree(node) == 0 && btor.get(entryBlock) != node
                        && !node.isExceptionLandingPad()){
                    logger.logD(this.getClass().getName()+"::build",String.format("remove dead node:%s",node.getName()));
                    regionGraph.removeNode(node);
                    flag = true;
                    break;
                }
                if (node.getType() == Region.RegionType.Condition && regionGraph.outDegree(node) == 1){
                    node.setType(Region.RegionType.Linear);
                }
            }
            if (!flag) break;
        }

        if (isAsyncFunction){
            //check and remove await generate if-then region
            for (Region region : regionGraph.nodes().toArray(new Region[]{})) {
                for (PandaInstruction pandaInstruction : region.getBlock().getPandaInstructions()) {
                    if (pandaInstruction instanceof ASYNCFUNCTIONAWAITUNCAUGHT_V8PandaInstruction){
                        if (region.getType() == Region.RegionType.Condition){
                            for (Region region1 : regionGraph.successors(region).toArray(new Region[]{})) {
                                if (region1.getBlock().getTerminator().getOpCode().isThrow()){
                                    regionGraph.removeNode(region1);
                                    region.setType(Region.RegionType.Linear);
                                }
                            }
                        }
                    }
                }
            }
        }

        if (isGeneratorFunction){
            for (Region region : regionGraph.nodes().toArray(new Region[]{})) {
                for (PandaInstruction pandaInstruction : region.getBlock().getPandaInstructions()) {
                    //remove stupid yield handle block
                    if (pandaInstruction instanceof CreateGeneratorObjectPandaInstruction){
                        if (region.getType() == Region.RegionType.Condition){
                            for (Region region1 : regionGraph.successors(region).toArray(new Region[]{})) {
                                if (region1.getBlock().getTerminator().getOpCode().isReturnIns()){
                                    //remove endWith return block
                                    regionGraph.removeNode(region1);
                                    region.setType(Region.RegionType.Linear);
                                }else {
                                    //remove this block
                                    if (region1.getType() == Region.RegionType.Condition){
                                        for (Region region2 : regionGraph.successors(region1).toArray(new Region[]{})){
                                            if (region2.getBlock().getTerminator().getOpCode().isThrow()){
                                                //remove endWith throw block
                                                regionGraph.removeNode(region2);
                                                region1.setType(Region.RegionType.Linear);
                                            }else {
                                                //now remove self
                                                regionGraph.removeNode(region1);
                                                regionGraph.putEdgeValue(region,region2,true);
                                                region.setType(Region.RegionType.Linear);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        for (Map.Entry<PandaIRBasicBlock, PandaIRBasicBlock> pair : exceptionHandleBlocks.entrySet()) {
            Region tryEntryRegion = btor.get(pair.getValue());
            Region handleEntryRegion = btor.get(pair.getKey());
            logger.logD("handle try catch",String.format("%s %s",tryEntryRegion,handleEntryRegion));
            TryCatchHelperRegion tryCatchRegion = new TryCatchHelperRegion(tryEntryRegion,handleEntryRegion);
            regionGraph.addNode(tryCatchRegion);
            for (Region predecessor : regionGraph.predecessors(tryEntryRegion).toArray(new Region[]{})) {
                boolean value = regionGraph.edgeValue(predecessor,tryEntryRegion).get();
                regionGraph.removeEdge(predecessor,tryEntryRegion);
                regionGraph.putEdgeValue(predecessor,tryCatchRegion,value);
            }
            regionGraph.putEdgeValue(tryCatchRegion,tryEntryRegion,false);
            regionGraph.putEdgeValue(tryCatchRegion,handleEntryRegion,true);
        }

        return this.regionGraph;
    }

    public static boolean checkIsAsyncException(PandaIRBasicBlock block){
        for (PandaInstruction pandaInstruction : block.getPandaInstructions()) {
            if (pandaInstruction instanceof ASYNCFUNCTIONREJECT_V8PandaInstruction) return true;
        }
        return false;
    }

    public static boolean checkIsGeneratorException(PandaIRBasicBlock block){
        for (PandaInstruction pandaInstruction : block.getPandaInstructions()) {
            if (pandaInstruction instanceof CreateGeneratorObjectPandaInstruction) return true;
        }
        return false;
    }

    private boolean checkIsForInException(PandaIRCFG pandaIRCFG, PandaIRBasicBlock block){
        Set<PandaIRBasicBlock> pandaIRBasicBlocks = pandaIRCFG.getGraph().successors(block);
        if (pandaIRBasicBlocks.size() != 1) return false;
        block = pandaIRBasicBlocks.iterator().next();
        pandaIRBasicBlocks = pandaIRCFG.getGraph().successors(block);
        if (pandaIRBasicBlocks.size() != 1) return false;
        block = pandaIRBasicBlocks.iterator().next();
        for (PandaInstruction pandaInstruction : block.getPandaInstructions()) {
            if (pandaInstruction instanceof GetIteratorPandaInstruction){
                return true;
            }
        }
        return false;
    }

    private boolean checkIsForOfException(PandaIRCFG pandaIRCFG,PandaIRBasicBlock tryBlock, PandaIRBasicBlock catchBlock){
        if (tryBlock.getPandaInstructions().length > 2){
            PandaInstruction instruction = tryBlock.getPandaInstructions()[1];
            if (instruction instanceof LoadObjectByPandaInstruction){
                IPandaInstructionParam obj = ((LoadObjectByPandaInstruction) instruction).getObj();
                if (obj == null) return false;
                String content = obj.toString();
                if (content.equals("\"return\""))
                    return true;
            }
        }
        Set<PandaIRBasicBlock> blocks = pandaIRCFG.getGraph().successors(catchBlock);
        if (blocks.size() != 2) return false;
        Iterator<PandaIRBasicBlock> iterator = blocks.iterator();
        PandaIRBasicBlock first = iterator.next();
        PandaIRBasicBlock second = iterator.next();
        if (!(first.getTerminator() instanceof THROW_PREF_NONEPandaInstruction)) return false;
        for (PandaInstruction pandaInstruction : second.getPandaInstructions()) {
            if (pandaInstruction instanceof LDHOLE_NONEPandaInstruction){
                return true;
            }
        }
        return false;
    }

    public Region getEntryRegion(){
        return btor.get(entryBlock);
    }

}
