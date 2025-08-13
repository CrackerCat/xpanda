package jmp0.abc.disasm.block.pass;

import jmp0.abc.PandaParseException;
import jmp0.abc.disasm.block.IPandaIRCFGPass;
import jmp0.abc.disasm.block.PandaIRBasicBlock;
import jmp0.abc.disasm.block.PandaIRCFG;
import jmp0.abc.disasm.ins.jump.JumpPandaInstruction;
import jmp0.abc.disasm.ins.PandaInstruction;
import lombok.SneakyThrows;

import java.util.HashSet;
import java.util.Set;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class SplitJumpPandaIRCFGPass implements IPandaIRCFGPass {
    @SneakyThrows
    @Override
    public boolean runOnPandaIRCFG(PandaIRCFG ircfg) {
        Set<Integer> splitPCList = new HashSet<>();
        for (PandaIRBasicBlock basicBlock : ircfg.getPandaIRBasicBlocks()) {
            PandaInstruction pandaInstruction = basicBlock.getTerminator();
            if (pandaInstruction instanceof JumpPandaInstruction){
                int pc = ((JumpPandaInstruction) pandaInstruction).getDestinationPC();
                if (ircfg.getBasicBlockByPC(pc) != null) continue;
                PandaIRBasicBlock destBasicBlock = ircfg.getContainBasicBlockByPC(pc);
                if (destBasicBlock == null)
                    throw new PandaParseException("can not resolve pc: "+ pc);
                splitPCList.add(pc);
            }
        }
        for (Integer pc : splitPCList) {
            SplitJumpPandaIRCFGPass.splitBlockByPc(ircfg,pc);
        }
        return !splitPCList.isEmpty();
    }

    @SneakyThrows
    public static void splitBlockByPc(PandaIRCFG ircfg, int pc){
        if (ircfg.getBasicBlockByPC(pc) != null) return;
        PandaIRBasicBlock pandaIRBasicBlock = ircfg.getContainBasicBlockByPC(pc);
        if (pandaIRBasicBlock == null)
            throw new PandaParseException("getContainBasicBlockByPC not found!");
        PandaIRBasicBlock[] splitBasicBlocks = pandaIRBasicBlock.splitBasicBlock(pc);
        Integer idx = ircfg.getBlockIndexPC(pandaIRBasicBlock.getStartPC());
        if (idx == null) throw new PandaParseException("this.blockIndexMap not found!");
        ircfg.replaceBlock(idx,splitBasicBlocks[0]);
        ircfg.addBlock(idx + 1,splitBasicBlocks[1]);
    }
}
