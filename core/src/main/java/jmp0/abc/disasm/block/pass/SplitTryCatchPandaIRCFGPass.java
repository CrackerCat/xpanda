package jmp0.abc.disasm.block.pass;

import jmp0.abc.Pair;
import jmp0.abc.disasm.block.IPandaIRCFGPass;
import jmp0.abc.disasm.block.PandaIRBasicBlock;
import jmp0.abc.disasm.block.PandaIRCFG;
import jmp0.abc.file.method.PandaMethodCatchBlock;
import jmp0.abc.file.method.PandaMethodTryBlock;

import java.util.*;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class SplitTryCatchPandaIRCFGPass implements IPandaIRCFGPass {
    @Override
    public boolean runOnPandaIRCFG(PandaIRCFG ircfg) {
        HashMap<Pair<Integer, Integer>, LinkedList<Integer>> tryDescMap = getPairLinkedListHashMap(ircfg);
        if (!tryDescMap.isEmpty()){
            for (Map.Entry<Pair<Integer, Integer>, LinkedList<Integer>> pairLinkedListEntry : tryDescMap.entrySet()) {
                Pair<Integer,Integer> pair = pairLinkedListEntry.getKey();
                LinkedList<Integer> handlePCList = pairLinkedListEntry.getValue();
                LinkedList<PandaIRBasicBlock> catchBlockList = new LinkedList<>();
                for (Integer i : handlePCList) {
                    catchBlockList.add(ircfg.getBasicBlockByPC(i));
                }
                Integer startPC = pair.getFirst();
                Integer endPC = pair.getSecond();
                int idx = ircfg.getBlockIndexPC(startPC);
                for (int i = idx; i < ircfg.getPandaIRBasicBlocks().size(); i++) {
                    PandaIRBasicBlock block = ircfg.getPandaIRBasicBlocks().get(i);
                    if (block.getStartPC() >= startPC && block.getStartPC() +block.getSize() <= endPC){
                        block.setCatchInfo(catchBlockList);
                    }
                }
            }
        } else return false;
        return true;
    }

    private static HashMap<Pair<Integer, Integer>, LinkedList<Integer>> getPairLinkedListHashMap(PandaIRCFG ircfg) {
        Set<Integer> splitPCList = new HashSet<>();
        PandaMethodTryBlock[] tryBlocks = ircfg.getPandaMethod().getMethodCode().getPandaMethodTryBlocks();
        HashMap<Pair<Integer,Integer>, LinkedList<Integer>> tryDescMap = new HashMap<>();
        if (tryBlocks != null){
            for (PandaMethodTryBlock tryBlock : tryBlocks) {
                int tryStartPC = tryBlock.getStartPC().intValue();
                int tryEndPC = tryStartPC + tryBlock.getLength().intValue();
                splitPCList.add(tryStartPC);
                splitPCList.add(tryEndPC);
                PandaMethodCatchBlock[] catchBlocks = tryBlock.getPandaMethodCatchBlocks();
                if (catchBlocks == null) continue;
                Pair<Integer,Integer> pair = new Pair<>(tryStartPC,tryEndPC);
                LinkedList<Integer> catchPCList = new LinkedList<>();
                for (PandaMethodCatchBlock catchBlock : catchBlocks) {
                    int catchBlockHandlerPC = catchBlock.getHandlerPC().intValue();
                    int catchBlockEndPC = catchBlockHandlerPC + catchBlock.getCodeSize().intValue();
                    splitPCList.add(catchBlockHandlerPC);
                    if (catchBlockEndPC <= ircfg.getPandaIRBasicBlocks().getLast().getTerminator().getPC())
                        splitPCList.add(catchBlockEndPC);
                    catchPCList.add(catchBlockHandlerPC);

                }
                tryDescMap.put(pair,catchPCList);
            }
        }
        for (Integer pc : splitPCList) {
            SplitJumpPandaIRCFGPass.splitBlockByPc(ircfg,pc);
        }
        return tryDescMap;
    }
}
