package jmp0.abc.disasm.block;

import com.google.common.graph.ElementOrder;
import com.google.common.graph.MutableValueGraph;
import com.google.common.graph.ValueGraphBuilder;
import jmp0.abc.PandaParseException;
import jmp0.abc.disasm.block.pass.SplitJumpPandaIRCFGPass;
import jmp0.abc.disasm.block.pass.SplitTryCatchPandaIRCFGPass;
import jmp0.abc.disasm.ins.jump.JumpPandaInstruction;
import jmp0.abc.disasm.ins.PandaInstruction;
import jmp0.abc.file.method.PandaMethod;
import lombok.Getter;
import lombok.SneakyThrows;

import java.util.*;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */

public final class PandaIRCFG {
    @Getter private final PandaMethod pandaMethod;
    private final HashMap<Integer,Integer> blockIndexMap = new HashMap<>();
    private final IPandaIRCFGPass[] passes = new IPandaIRCFGPass[]{new SplitJumpPandaIRCFGPass(),new SplitTryCatchPandaIRCFGPass()};
    @Getter private final LinkedList<PandaIRBasicBlock> pandaIRBasicBlocks;
    @Getter private MutableValueGraph<PandaIRBasicBlock, Boolean> graph;
    @SneakyThrows
    public PandaIRCFG(LinkedList<PandaIRBasicBlock> basicBlocks, PandaMethod pandaMethod){
        this.pandaMethod = pandaMethod;
        this.pandaIRBasicBlocks = basicBlocks;
        for (int i = 0; i < basicBlocks.size(); i++) {
            blockIndexMap.put(pandaIRBasicBlocks.get(i).getStartPC(),i);
        }
        for (IPandaIRCFGPass pass : passes) {
            if (pass.runOnPandaIRCFG(this))
                buildGraph();
        }
        buildGraph();
    }

    @SneakyThrows
    private void buildGraph(){
        this.graph = ValueGraphBuilder.directed().allowsSelfLoops(true).nodeOrder(ElementOrder.stable()).build();
        for (PandaIRBasicBlock basicBlock : this.pandaIRBasicBlocks) {
            this.graph.addNode(basicBlock);
        }
        for (int i = 0; i < this.pandaIRBasicBlocks.size(); i++) {
            PandaIRBasicBlock prePandaIRBasicBlock = this.pandaIRBasicBlocks.get(i);
            if (i + 1 == this.pandaIRBasicBlocks.size()) break;
            PandaIRBasicBlock pandaIRBasicBlock = this.pandaIRBasicBlocks.get(i + 1);
            PandaInstruction instruction = prePandaIRBasicBlock.getTerminator();

            if (instruction.getOpCode().isReturnIns()||
                    //if throw instruction find,it should have no successor
                    instruction.getOpCode().isThrow()) continue;
            if (instruction instanceof  JumpPandaInstruction){
                int pc = ((JumpPandaInstruction) instruction).getDestinationPC();
                PandaIRBasicBlock destBasicBlock = getBasicBlockByPC(pc);
                if (destBasicBlock == null)
                    throw new PandaParseException("can not resolve pc: "+ pc);
                this.graph.putEdgeValue(prePandaIRBasicBlock,destBasicBlock,true);
                if (instruction.getOpCode().isConditionalIns()){
                    this.graph.putEdgeValue(prePandaIRBasicBlock,pandaIRBasicBlock,false);
                    continue;
                }
                continue;
            }
            this.graph.putEdgeValue(prePandaIRBasicBlock,pandaIRBasicBlock,false);
        }
    }

    public PandaIRBasicBlock getBasicBlockByPC(int pc){
        Integer index = this.blockIndexMap.get(pc);
        if (index == null) return null;
        return this.pandaIRBasicBlocks.get(index);
    }

    public PandaIRBasicBlock getContainBasicBlockByPC(int pc){
        for (PandaIRBasicBlock node : this.pandaIRBasicBlocks) {
            if (node.getStartPC() > pc) return null;
            if (node.getByPC(pc) != null) return node;
        }
        return null;
    }

    public void replaceBlock(int idx,PandaIRBasicBlock block){
        this.pandaIRBasicBlocks.add(idx,block);
        this.pandaIRBasicBlocks.remove(idx+1);
    }

    public void addBlock(int idx,PandaIRBasicBlock block){
        this.pandaIRBasicBlocks.add(idx,block);
        //update index...
        for (Integer i : this.blockIndexMap.keySet()) {
            Integer index = this.blockIndexMap.get(i);
            if (index >= idx) this.blockIndexMap.put(i,index + 1);
        }
        this.blockIndexMap.put(block.getStartPC(),idx);
    }

    public Integer getBlockIndexPC(int pc){
        return this.blockIndexMap.get(pc);
    }

    public PandaIRBasicBlock getEntryBlock(){
        return this.pandaIRBasicBlocks.getFirst();
    }

    public PandaIRBasicBlock getLastBlock(){
        return this.pandaIRBasicBlocks.getLast();
    }
    public PandaIRBasicBlock getLastBlock(PandaIRBasicBlock block){
        int index = blockIndexMap.get(block.getStartPC());
        return this.pandaIRBasicBlocks.get(index -1);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (PandaIRBasicBlock pandaIRBasicBlock : this.pandaIRBasicBlocks) {
            stringBuilder.append(pandaIRBasicBlock.toString());
        }
        return stringBuilder.toString();
    }
}
