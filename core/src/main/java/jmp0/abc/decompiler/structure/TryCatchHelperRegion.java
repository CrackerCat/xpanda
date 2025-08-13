package jmp0.abc.decompiler.structure;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class TryCatchHelperRegion extends Region{
    public TryCatchHelperRegion(Region tryEntryRegion,Region handleEntryRegion) {
        super("tryCatchHelper_"+tryEntryRegion+"_"+handleEntryRegion, RegionType.Condition);
    }
}
