package jmp0.abc.decompiler.structure;

import com.google.common.graph.MutableValueGraph;
import jmp0.abc.disasm.block.PandaIRBasicBlock;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class RegionFactory {
    private final MutableValueGraph<PandaIRBasicBlock, Boolean> cfg;

    public RegionFactory(MutableValueGraph<PandaIRBasicBlock, Boolean> cfg){
        this.cfg = cfg;
    }

    public Region create(PandaIRBasicBlock basicBlock){
        Region.RegionType regType;
        if (cfg.outDegree(basicBlock) == 0){
            regType = Region.RegionType.Tail;
        }else {
            regType = Region.RegionType.Linear;
        }
        if (basicBlock.getTerminator().getOpCode().isConditionalIns()){
            regType = Region.RegionType.Condition;
        }
        return new Region(basicBlock,regType);
    }
}
