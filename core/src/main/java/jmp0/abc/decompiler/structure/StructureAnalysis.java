package jmp0.abc.decompiler.structure;

import com.google.common.graph.MutableValueGraph;
import com.google.common.graph.Traverser;
import com.google.common.primitives.Ints;
import jmp0.abc.Pair;
import jmp0.abc.decompiler.IAnalysis;
import jmp0.abc.decompiler.PandaDecompileException;
import jmp0.abc.decompiler.PandaDecompilerMethodHandler;
import jmp0.abc.decompiler.structure.statement.*;
import jmp0.abc.disasm.block.PandaIRBasicBlock;
import jmp0.abc.disasm.block.PandaIRCFG;
import jmp0.abc.disasm.ins.PandaInstruction;
import jmp0.abc.disasm.ins.creaters.NewLexEnvPandaInstruction;
import jmp0.abc.disasm.ins.jump.JumpPandaInstruction;
import jmp0.abc.disasm.lexical.PandaLexical;
import jmp0.abc.util.GraphHelper;
import jmp0.abc.util.PandaLogger;
import lombok.Getter;
import lombok.SneakyThrows;

import java.util.*;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class StructureAnalysis implements IAnalysis {
    private final PandaLogger logger = new PandaLogger(StructureAnalysis.class);
    @Getter private final MutableValueGraph<Region,Boolean> regionGraph;
    private Region entryRegion;

    private DominatorGraph doms;
    private DominatorGraph postDoms;
    private final HashSet<Region> unresolvedCycles = new HashSet<>();
    private final PandaIRCFG pandaIRCFG;



    @SneakyThrows
    public StructureAnalysis(PandaIRCFG pandaIRCFG, PandaIRBasicBlock entryBlock, PandaIRBasicBlock lastBlock){
        RegionGraphBuilder regionGraphBuilder = new RegionGraphBuilder(pandaIRCFG,entryBlock,lastBlock);
        this.regionGraph = regionGraphBuilder.build();
        this.entryRegion = regionGraphBuilder.getEntryRegion();
        removeSingleJumpRegion();
        simplifyConditionalSequence();
        this.doms = new DominatorGraph(this.regionGraph,entryRegion);
        this.pandaIRCFG = pandaIRCFG;
    }

    private void removeSingleJumpRegion(){
        while (true) {
            Iterable<Region> regions = Traverser.forGraph(regionGraph).depthFirstPostOrder(entryRegion);
            boolean result = false;
            for (Region region : regions) {
                if (region.getType() == Region.RegionType.Linear){
                    PandaInstruction[] instructions = region.getBlock().getPandaInstructions();
                    if (instructions.length == 1 && instructions[0] instanceof JumpPandaInstruction){
                        logger.logD("removeSingleJumpRegion",String.format("simplify || %s ",region.getName()));
                        Region successor = GraphHelper.getOnlyOneSuccessor(regionGraph,region);
                        GraphHelper.replacePredecessors(regionGraph,successor,region);
                        removeNode(region);
                        result = true;
                        break;
                    }
                }
            }
            if (!result) break;
        }
    }

    private Region fixConditionalSequenceContainNewLex(Region region){
        logger.logD("fixConditionalSequenceContainNewLex",String.format("split: %s",region.getName()));
        if (region.getStatements().size() == 1){
            PandaIRBasicBlock block = region.getBlock();
            PandaInstruction pandaInstruction = block.getPandaInstructions()[0];
            //check is new lex instruction
            if (pandaInstruction instanceof NewLexEnvPandaInstruction){
                int pc = pandaInstruction.getOpCode().getInstructionSize();
                PandaIRBasicBlock[] blocks = block.splitBasicBlock(pc);
                Region newLexRegion = new Region(blocks[0], Region.RegionType.Linear);
                Region nextRegion = new Region(blocks[1], region.getType());
                regionGraph.addNode(newLexRegion);
                regionGraph.addNode(nextRegion);
                GraphHelper.replacePredecessors(regionGraph,newLexRegion,region);
                regionGraph.putEdgeValue(newLexRegion,nextRegion,false);
                for (Region successor : regionGraph.successors(region)) {
                    Optional<Boolean> value = regionGraph.edgeValue(region,successor);
                    regionGraph.putEdgeValue(nextRegion,successor,value.get());
                }
                regionGraph.removeNode(region);
                //check need replace entryRegion
                if (entryRegion == region){
                    entryRegion = newLexRegion;
                }
                return nextRegion;
            }
        }
        return region;
    }

    private void simplifyConditionalSequence(){
        while (true){
            Iterable<Region> regions = Traverser.forGraph(regionGraph).depthFirstPostOrder(entryRegion);
            boolean result = false;
            for (Region region : regions) {
                if (region.getType() == Region.RegionType.Condition){
                    Pair<Region,Region> pair = getDestNextPair(region);
                    Region destRegion = pair.getFirst();
                    Region nextRegion = pair.getSecond();
                    if (nextRegion.getType() == Region.RegionType.Condition && regionGraph.inDegree(nextRegion) == 1) {
                        Pair<Region,Region> nextPair = getDestNextPair(nextRegion);
                        Region nextDestRegion = nextPair.getFirst();
                        Region nextNextRegion = nextPair.getSecond();
                        if (nextNextRegion == destRegion){
                            //or condition
                            logger.logD("simplifyConditionalSequence",String.format("simplify || %s %s %s",region.getName(),nextRegion.getName(),destRegion.getName()));
                            region = fixConditionalSequenceContainNewLex(region);
                            IPandaDecompileAble statement = new OrLogicalExpressionStatement(region,nextRegion);
                            GraphHelper.replaceSuccessor(regionGraph,region,nextRegion);
                            region.clearStatements();
                            region.addStatement(statement);
                            region.setLogicalRegion(true);
                            removeNode(nextRegion);
                            result = true;
                            break;
                        } else if (nextDestRegion == destRegion) {
                            //and condition
                            logger.logD("simplifyConditionalSequence",String.format("simplify && %s %s %s",region.getName(),nextRegion.getName(),destRegion.getName()));
                            region = fixConditionalSequenceContainNewLex(region);
                            IPandaDecompileAble statement = new AndLogicalExpressionStatement(region,nextRegion);
                            GraphHelper.replaceSuccessor(regionGraph,region,nextRegion);
                            region.clearStatements();
                            region.addStatement(statement);
                            region.setLogicalRegion(true);
                            removeNode(nextRegion);
                            result = true;
                            break;
                        }
                    }
                }
            }
            if (!result) break;
        }
    }

    @SneakyThrows
    @Override
    public void analysis(PandaDecompilerMethodHandler methodHandler, PandaLexical pandaLexical, Object astNode){
        int count = pandaIRCFG.getPandaIRBasicBlocks().size();
        if (count > 500){
            //fixme ...
            throw new PandaDecompileException("too many blocks！");
        }
        logger.logD("analysis","analysis->" + pandaIRCFG.getPandaMethod().getName());
        while (regionGraph.nodes().size() != 1 || isCyclic(regionGraph.nodes().iterator().next())){
            count++;
            int originNodeCount = regionGraph.nodes().size();
            int newNodeCount;
            Iterable<Region> regions = Traverser.forGraph(regionGraph).depthFirstPostOrder(entryRegion);
            boolean result = false;
            for (Region region : regions) {
                result = reduceAcyclic(region);
                if (!result && isCyclic(region)){
                    result = reduceCyclic(region);
                }
                if (result) break;
            }
            if (!result){
                newNodeCount = regionGraph.nodes().size();
                if (originNodeCount == newNodeCount && newNodeCount > 1){
                    if (!unresolvedCycles.isEmpty()){
                        for (Region region : unresolvedCycles) {
                            Set<Region> loopNodes = new LoopFinder(regionGraph,region,doms).getLoopNodes();
                            result = refineLoop(region,loopNodes);
                            unresolvedCycles.clear();
                            break;
                        }
                    }
                    if (!result){
                        for (Region region : regions) {
                            if (coalesceTailRegion(region,regionGraph.nodes())){
                                result = true;
                                break;
                            }
                        }
                        if (!result && !fixIrreducibleByTailRegion() && count > 3000){
                            throw new PandaDecompileException(pandaIRCFG.getPandaMethod().getName() + " can not reduce the graph");
                        }
                    }
                }
            }
            this.doms = new DominatorGraph(this.regionGraph,entryRegion);
        }
        regionGraph.nodes().iterator().next().decompile(methodHandler,pandaLexical , astNode);
    }


    private boolean reduceAcyclic(Region region){
        boolean didReduce = false;
        switch (region.getType()){
            case Condition : {
                didReduce = reduceIfRegion(region);
                break;
            }
            case Linear : {
                didReduce = reduceSequence(region);
                break;
            }
            case Tail : {
                //just nothing!
                break;
            }
        }
        return didReduce;
    }

    @SneakyThrows
    private boolean reduceCyclic(Region region){
        boolean didReduce = false;
        Region[] succs = regionGraph.successors(region).toArray(new Region[]{});
        if (succs.length != 1 || !reduceSequence(region)){
        }else {
            return true;
        }
        for (Region succ : succs) {
            if (succ == region){
                PandaStatement loopStatement;
                if (succs.length == 1){
                    logger.logD("reduceCyclic",String.format("while %s %s",region.getName(),succ.getName()));
                    loopStatement = new WhileStatement(null,region);
                    region.setType(Region.RegionType.Tail);
                }else {
                    logger.logD("reduceCyclic",String.format("doWhile %s %s",region.getName(),succ.getName()));
                    if (!regionGraph.edgeValue(region,succ).get())
                        region.expInvert();
                    loopStatement = new DoWhileStatement(region);
                    region.setType(Region.RegionType.Linear);
                }
                region.clearStatements();
                region.addStatement(loopStatement);
                removeEdge(region,succ);
                removeEdge(succ,region);
                return true;
            }
        }
        if (region.getType() != Region.RegionType.Condition) return false;
        for (Region succ : succs) {
            if (linearSuccessor(succ) == region && singlePredecessor(succ) == region){
                logger.logD("reduceCyclic",String.format("while %s %s",region.getName(),succ.getName()));
                if (!regionGraph.edgeValue(region,succ).get())
                    region.expInvert();
                PandaStatement loop = new WhileStatement(region,succ);
                region.setType(Region.RegionType.Linear);
                removeEdge(region,succ);
                removeEdge(succ,region);
                removeNode(succ);
                region.clearStatements();
                region.addStatement(loop);
                return true;
            }

        }
        if (unresolvedCycles.isEmpty()){
            logger.logD("reduceCyclic",String.format("loop nodes head %s",region));
            unresolvedCycles.add(region);
        }
        return didReduce;
    }


    private boolean isCyclic(Region n) {
        for (Region region : regionGraph.predecessors(n)) {
            if (region == n || isBackEdge(region,n)){
                return true;
            }
        }
        return false;
    }

    private boolean isBackEdge(Region a, Region b) {
        return doms.dominatesStrictly(b, a);
    }

    private boolean refineLoop(Region head,Set<Region> loopNodes){
        head = ensureSingleEntry(head,loopNodes);
        Pair<Region,Region> loopPair = determineFollowLatch(head,loopNodes);
        if (loopPair.getFirst() == null && loopPair.getSecond() == null) return true;
        Set<Region> lexicalNodes = getLexicalNodes(head,loopPair.getFirst(),loopNodes);
        boolean virtualized = virtualizeIrregularExits(head,loopPair.getSecond(),loopPair.getFirst(),lexicalNodes);
        if (virtualized) return true;
        for (Region lexicalNode : lexicalNodes) {
            if (coalesceTailRegion(lexicalNode, lexicalNodes))
                return true;
        }
        return false;
    }

    private boolean fixIrreducibleByTailRegion(){
        LinkedList<Region> tailRegionLinkedList = new LinkedList<>();
        for (Region node : this.regionGraph.nodes()) {
            if (node.getType() == Region.RegionType.Tail && regionGraph.inDegree(node) > 1){
                tailRegionLinkedList.add(node);
            }
        }
        //get most predecessors node
        tailRegionLinkedList.sort((o1, o2) -> Ints.compare(regionGraph.inDegree(o2),regionGraph.inDegree(o1)));
        if (tailRegionLinkedList.isEmpty()) return false;
        logger.logD("fixIrreducibleByTailRegion","split tail node");
        Region region = tailRegionLinkedList.get(0);
        int nodeIndex = 0;
        for (Region predecessor : regionGraph.predecessors(region)) {
            Boolean value = regionGraph.edgeValue(predecessor,region).get();
            Region copyNode = region.copy();
            copyNode.setName(copyNode.getName() + '_' +nodeIndex++);
            regionGraph.addNode(copyNode);
            regionGraph.putEdgeValue(predecessor,copyNode,value);
        }
        removeNode(region);
        return true;
    }

    private boolean reduceSequence(Region region){
        //merge last region to this
        Region nextRegion = GraphHelper.getOnlyOneSuccessor(regionGraph,region);
        if (regionGraph.inDegree(nextRegion) != 1 || nextRegion == entryRegion) return false;
        logger.logD("reduceSequence",String.format("Concatenated %s and %s",region,nextRegion));
        region.setType(nextRegion.getType());
        if (nextRegion.getType() == Region.RegionType.Condition){
            region.setConditionalExp(nextRegion.isConditionalExp());
        }
        region.addRegion(nextRegion);
        removeEdge(region,nextRegion);
        GraphHelper.replaceSuccessor(this.regionGraph,region,nextRegion);
        if (nextRegion.isTryCatchHelperRegion()){
            region.setTryCatchHelperRegion(true);
        }
//        if (nextRegion.isLogicalRegion()){
//            region.setLogicalRegion(true);
//        }
        removeNode(nextRegion);
        return true;
    }

    private boolean reduceIfRegion(Region region){
        Pair<Region,Region> pair = getDestNextPair(region);
        if (pair == null){
            logger.logD("reduceIfRegion",String.format("reduce if empty body %s",region));
            region.setType(Region.RegionType.Linear);
            return true;
        }
        Region destRegion = pair.getFirst();
        Region nextRegion = pair.getSecond();
        Region destSRegion = linearSuccessor(destRegion);
        Region nextSRegion = linearSuccessor(nextRegion);
        if (nextRegion == destSRegion && !region.isTryCatchHelperRegion()){
            logger.logD("reduceIfRegion",String.format("reduce if then %s %s",region,destRegion));
            region.addStatement(new IfPandaStatement(region,null,destRegion));
            removeEdge(region,destRegion);
            if (regionGraph.inDegree(destRegion) == 0){
                removeNode(destRegion);
            }
            region.setType(Region.RegionType.Linear);
            return true;
        }else if (destRegion == nextSRegion && !region.isTryCatchHelperRegion()){
            logger.logD("reduceIfRegion",String.format("reduce if then %s %s",region,nextRegion));
            region.addStatement(new IfPandaStatement(region,nextRegion,null));
            removeEdge(region,nextRegion);
            if (regionGraph.inDegree(nextRegion) == 0){
                removeNode(nextRegion);
            }
            region.setType(Region.RegionType.Linear);
            return true;
        }else if ((destSRegion == nextSRegion && destSRegion != null)){
            logger.logD("reduceIfRegion",String.format("reduce if else %s %s %s",region,nextRegion,destRegion));
            region.addStatement(new IfPandaStatement(region,nextRegion,destRegion));
            removeEdge(region,nextRegion);
            if (regionGraph.inDegree(nextRegion) == 0){
                removeNode(nextRegion);
            }
            removeEdge(region,destRegion);
            if (regionGraph.inDegree(destRegion) == 0){
                removeNode(destRegion);
            }
            regionGraph.putEdgeValue(region,destSRegion,false);
            region.setType(Region.RegionType.Linear);
            return true;
        }else if (destRegion.getType() == Region.RegionType.Tail && nextRegion.getType() == Region.RegionType.Tail){
            logger.logD("reduceIfRegion",String.format("reduce if else [both tail] %s %s %s",region,nextRegion,destRegion));
            region.addStatement(new IfPandaStatement(region,nextRegion,destRegion));
            removeEdge(region,nextRegion);
            if (regionGraph.inDegree(nextRegion) == 0){
                removeNode(nextRegion);
            }
            removeEdge(region,destRegion);
            if (regionGraph.inDegree(destRegion) == 0){
                removeNode(destRegion);
            }
            region.setType(Region.RegionType.Tail);
            return true;
        }
        return false;
    }

    private Region linearSuccessor(Region region){
        if (region.getType() != Region.RegionType.Linear) return null;
        return GraphHelper.getOnlyOneSuccessor(regionGraph,region);
    }

    private Region singlePredecessor(Region n){
        if (regionGraph.inDegree(n) != 1) return null;
        return regionGraph.predecessors(n).iterator().next();
    }

    private Pair<Region,Region> getDestNextPair(Region region){
        Region destBlock,nextBlock;
        if (regionGraph.outDegree(region) == 1){
            //onlyOneSuck empty conditional block
            return null;
        }
        Iterator<Region> blockIterator = regionGraph.successors(region).iterator();
        Region region1 = blockIterator.next();
        if (regionGraph.edgeValue(region,region1).get()){
            destBlock = region1;
            nextBlock = blockIterator.next();
        }else {
            destBlock = blockIterator.next();
            nextBlock = region1;
        }
        return new Pair<>(destBlock,nextBlock);
    }

    private int countIncomingEdges(Region n, Set<Region> loopNodes) {
        return (int) regionGraph.predecessors(n)
                .stream()
                .filter(p -> !loopNodes.contains(p))
                .count();
    }

    private Region ensureSingleEntry(Region head, Set<Region> loopNodes) {
        int cinMax = countIncomingEdges(head, loopNodes);

        for (Region n : loopNodes) {
            int cin = countIncomingEdges(n, loopNodes);
            if (cin > cinMax) {
                cinMax = cin;
            }
        }
        return head;
    }

    private Pair<Region, Region> determineFollowLatch(Region head, Set<Region> loopNodes) {
        Region[] headSucc = regionGraph.successors(head).toArray(new Region[0]);

        if (headSucc.length == 2) {
            Region follow = null;
            if (!loopNodes.contains(headSucc[0])) {
                follow = headSucc[0];
            } else if (!loopNodes.contains(headSucc[1])) {
                follow = headSucc[1];
            }

            if (follow != null) {
                for (Region latch : regionGraph.predecessors(head)) {
                    if (isBackEdge(latch, head) && linearSuccessor(latch) == head) {
                        return new Pair<>(follow, latch);
                    }
                }
            }
        }

        for (Region latch : regionGraph.predecessors(head)) {
            if (isBackEdge(latch, head)) {
                Region[] latchSuccs = regionGraph.successors(latch).toArray(new Region[0]);
                if (latchSuccs.length == 2) {
                    if (!loopNodes.contains(latchSuccs[0])) {
                        return new Pair<>(latchSuccs[0], latch);
                    }
                    if (!loopNodes.contains(latchSuccs[1])) {
                        return new Pair<>(latchSuccs[1], latch);
                    }
                }
            }
        }
        //fixme, i have no idea,but to insert goto
        for (Region latch : regionGraph.predecessors(head)) {
            if (isBackEdge(latch, head)) {
                addLabelToRegion(head);
                latch.addStatement(new GotoStatement(head));
                removeEdge(latch,head);
                if (regionGraph.outDegree(latch) == 1){
                    latch.setType(Region.RegionType.Linear);
                }else
                    latch.setType(Region.RegionType.Tail);
                return new Pair<>(null, null);
            }
        }
        return new Pair<>(null, null);
    }

    private Set<Region> getLexicalNodes(Region head, Region follow, Set<Region> loopNodes) {
        logger.logD("in getLexicalNodes",String.format("%s %s %s",head,follow,loopNodes));
        Set<Region> excluded = new HashSet<>();
        findReachableRegions(follow, head, excluded);
        logger.logD("findReachableRegions",Arrays.toString(excluded.toArray()));
        Set<Region> lexNodes = new HashSet<>();
        LinkedList<Region> wl = new LinkedList<>(loopNodes);
        int i = 0;
        int size = wl.size();
        while (true){
            for (; i < size; i++) {
                if (loopNodes.contains(wl.get(i))) {
                    lexNodes.add(wl.get(i));
                    for (Region successor : regionGraph.successors(wl.get(i))) {
                        if (!lexNodes.contains(successor) && !wl.contains(successor)){
                            wl.add(successor);
                            break;
                        }
                    }
                } else if (doms.dominatesStrictly(head, wl.get(i)) && !excluded.contains(wl.get(i))) {
                    lexNodes.add(wl.get(i));
                    for (Region successor : regionGraph.successors(wl.get(i))) {
                        if (!lexNodes.contains(successor) && !wl.contains(successor)){
                            wl.add(successor);
                            break;
                        }
                    }
                }
            }
            if (i==wl.size()) break;
            logger.logD("getLexicalNodes",Arrays.toString(wl.toArray()));
            size = wl.size();
        }
        logger.logD("out getLexicalNodes",Arrays.toString(lexNodes.toArray()));
        return lexNodes;
    }

    void findReachableRegions(Region n, Region head, Set<Region> regions) {
        regions.add(n);
        for (Region succ : regionGraph.successors(n)) {
            if (!regions.contains(succ) && !succ.equals(head)) {
                findReachableRegions(succ, head, regions);
            }
        }
    }

    @SneakyThrows
    private boolean virtualizeIrregularExits(Region header, Region latch, Region follow, Set<Region> lexicalNodes) {
        boolean didVirtualize = false;
        LoopDescription.LoopType loopType = determineLoopType(header, latch, follow);
        LinkedList<LoopDescription> loopDescriptions = new LinkedList<>();
        for (Region n : lexicalNodes) {
            for (Region s : regionGraph.successors(n)) {
                if (s.equals(header)) {
                    if (!n.equals(latch)) {
                        loopDescriptions.add(new LoopDescription(n,s, LoopDescription.TYPE.CONTINUE));
                        //continue
                    }
                } else if (!lexicalNodes.contains(s)) {
                    if (s == follow) {
                        if ((loopType == LoopDescription.LoopType.DoWhile && n != latch)||
                                (loopType == LoopDescription.LoopType.While && n != header)){
                            //break
                            loopDescriptions.add(new LoopDescription(n,s, LoopDescription.TYPE.BREAK));
                        }
                    } else {
                        loopDescriptions.add(new LoopDescription(n,s, LoopDescription.TYPE.GOTO));
                        loopDescriptions.add(new LoopDescription(n,s, LoopDescription.TYPE.BREAK));
                    }
                }
            }
        }
        if (!loopDescriptions.isEmpty()) didVirtualize = true;
        for (LoopDescription loopDescription : loopDescriptions) {
            Region from = loopDescription.getFrom();
            Region to = loopDescription.getTo();
            logger.logD("virtualizeIrregularExits", String.format("%s %s %s",loopDescription.getType(),loopDescription.getFrom().getName(),loopDescription.getTo().getName()));
            if (loopDescription.getType() == LoopDescription.TYPE.GOTO){
                addLabelToRegion(to);
            }
            if (from.getType() == Region.RegionType.Condition){
                if (regionGraph.edgeValue(from,to).get())
                    from.expInvert();
                from.addStatement(new IfPandaStatement(from,loopDescription.getConditionalStatement(),null));
                from.setType(Region.RegionType.Linear);
            } else if (from.getType() == Region.RegionType.Linear) {
                from.addStatement(loopDescription.getConditionalStatement());
                from.setType(Region.RegionType.Tail);
            }
            if (regionGraph.nodes().contains(to)){
                removeEdge(from,to);
                if (regionGraph.inDegree(to) == 0 && to != entryRegion && to.getType() == Region.RegionType.Tail){
                    removeNode(to);
                }
            }
        }

        return didVirtualize;
    }
    private boolean coalesceTailRegion(Region n, Collection<Region> regions) {
        Region[] succs = regionGraph.successors(n).toArray(new Region[]{});
        if (succs.length == 2 && n.getType() == Region.RegionType.Condition && !n.isTryCatchHelperRegion()) {
            Region el = succs[0];
            Region th = succs[1];
            if (regions.contains(el) && el.getType() == Region.RegionType.Tail && singlePredecessor(el) == n) {
                logger.logD("coalesceTailRegion",String.format("if else tail %s",el.getName()));
                n.addStatement(new IfPandaStatement(n, el,null));
                removeEdge(n, el);
                removeNode(el);
                n.setType(Region.RegionType.Linear);
                return true;
            }

            if (regions.contains(th) && th.getType() == Region.RegionType.Tail && singlePredecessor(th) == n) {
                logger.logD("coalesceTailRegion",String.format("if then tail %s",el.getName()));
                n.expInvert();
                n.addStatement(new IfPandaStatement(n, th,null));
                removeEdge(n, th);
                removeNode(th);
                n.setType(Region.RegionType.Linear);
                return true;
            }
        }
        return false;
    }


    private void removeEdge(Region region,Region region1){
        logger.logD("removeEdge", String.format("remove edge %s %s",region.getName(),region1.getName()));
        regionGraph.removeEdge(region,region1);
    }

    private void removeNode(Region region){
        logger.logD("removeNode", String.format("remove node %s",region.getName()));
        regionGraph.removeNode(region);
    }


    private boolean hasExitEdgeFrom(Region n, Region follow) {
        for (Region successor : regionGraph.successors(n)) {
            if (successor == follow) return true;
        }
        return false;
    }

    private LoopDescription.LoopType determineLoopType(Region header, Region latch, Region follow)
    {
        if (!hasExitEdgeFrom(latch, follow))
            return LoopDescription.LoopType.While;
        if (!hasExitEdgeFrom(header, follow))
            return LoopDescription.LoopType.DoWhile;
        if (header.getStatements().size() > 1)
            return LoopDescription.LoopType.DoWhile;
        return LoopDescription.LoopType.While;
    }

    @Getter
    private static class LoopDescription{
        private final PandaLogger logger = new PandaLogger(LoopDescription.class);
        private final Region from;
        private final Region to;
        private final TYPE type;

        public enum TYPE{
            CONTINUE,
            BREAK,
            GOTO
        }

        public enum LoopType{
            DoWhile,
            While
        }
        public LoopDescription(Region from,Region to,TYPE type){
            this.from = from;
            this.to = to;
            this.type = type;
        }

        public PandaStatement getConditionalStatement(){
            switch (type){
                case CONTINUE:
                    return new ContinueStatement(null);
                case BREAK:
                    return new BreakStatement(null);
                case GOTO:{
                    logger.logW("getConditionalStatement","just use break to replace goto statement");
                    return new BreakStatement("from goto " + to.getName());
                }

            }
            return null;
        }
    }

    private void addLabelToRegion(Region region){
        if (region.getStatements().get(0) instanceof LabelStatement) return;
        region.addFirstStatement(new LabelStatement(region));
    }

}
