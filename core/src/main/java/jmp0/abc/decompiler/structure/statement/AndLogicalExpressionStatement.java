package jmp0.abc.decompiler.structure.statement;

import jmp0.abc.decompiler.PandaDecompilerMethodHandler;
import jmp0.abc.decompiler.codegen.CodeGenerator;
import jmp0.abc.decompiler.structure.Region;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class AndLogicalExpressionStatement extends LogicalExpressionStatement{

    /**
     *  first block
     *  if((first_block,acc) && (second_block,acc)){
     *      block
     *  }
     */
    public AndLogicalExpressionStatement(Region first, Region second) {
        super(first, second);
    }

    @Override
    public TYPE getType() {
        return TYPE.AND;
    }
}
