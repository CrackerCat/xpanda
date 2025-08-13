package jmp0.abc.decompiler.ins;

import jmp0.abc.decompiler.PandaDecompilerMethodHandler;
import jmp0.abc.decompiler.codegen.CodeGenerator;
import jmp0.abc.disasm.block.PandaIRBasicBlock;
import jmp0.abc.disasm.ins.yield.CREATEGENERATOROBJ_V8PandaInstruction;
import jmp0.abc.disasm.ins.yield.CREATEITERRESULTOBJ_V8_V8PandaInstruction;
import jmp0.abc.disasm.ins.yield.CreateGeneratorObjectPandaInstruction;
import jmp0.abc.disasm.lexical.PandaLexical;
import jmp0.abc.disasm.param.PandaInstructionVReg;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class CreateGeneratorObjectPandaInstructionHandle implements IInstructionHandle<CreateGeneratorObjectPandaInstruction>{
    @Override
    public HandleStatus handle(CreateGeneratorObjectPandaInstruction instruction, PandaIRBasicBlock block, PandaDecompilerMethodHandler methodHandler, PandaLexical pandaLexical, Object node) {
        CodeGenerator codeGenerator = methodHandler.getCodeGenerator();
        if (instruction instanceof CREATEGENERATOROBJ_V8PandaInstruction){
            codeGenerator.getNodeUtils().setIsGeneratorBlock(node);
            return HandleStatus.createNormalStatus();
        } else if (instruction instanceof CREATEITERRESULTOBJ_V8_V8PandaInstruction) {
            PandaInstructionVReg reg = ((CREATEITERRESULTOBJ_V8_V8PandaInstruction) instruction).getYieldReg();
            Object identifierNode = codeGenerator.getNodeFactory().identifierNode(reg.toString());
            Object yieldExpressionNode = codeGenerator.getNodeFactory().yieldExpressionNode(identifierNode);
            codeGenerator.getNodeUtils().insertNodeToBody(codeGenerator.getNodeFactory().expressionStatementNode(yieldExpressionNode),node);
            return HandleStatus.createNormalStatus();
        }

        return HandleStatus.createNormalStatus();
    }
}
