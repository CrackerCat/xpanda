package jmp0.abc.decompiler.structure.statement;
import jmp0.abc.decompiler.PandaDecompilerMethodHandler;
import jmp0.abc.decompiler.structure.Region;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class OrLogicalExpressionStatement extends LogicalExpressionStatement {

    /**
     *  first block
     *  if((first_block,acc)||(second_block,acc)){
     *      block
     *  }
     * @param first first region
     * @param second second region
     */

    public OrLogicalExpressionStatement(Region first, Region second){
        super(first,second);
    }

    @Override
    public TYPE getType() {
        return TYPE.OR;
    }
}
