package jmp0.abc.decompiler.structure;

import jmp0.abc.decompiler.PandaDecompileException;
import jmp0.abc.decompiler.PandaDecompilerMethodHandler;
import jmp0.abc.decompiler.structure.statement.LinearPandaStatement;
import jmp0.abc.disasm.block.PandaIRBasicBlock;
import jmp0.abc.disasm.ins.PandaInstruction;
import jmp0.abc.disasm.ins.jump.JumpEqualPandaInstruction;
import jmp0.abc.disasm.ins.jump.JumpNotEqualPandaInstruction;
import jmp0.abc.disasm.lexical.PandaLexical;
import jmp0.abc.util.PandaLogger;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.util.LinkedList;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public class Region implements IPandaDecompileAble{
    private final PandaLogger logger = new PandaLogger(Region.class);
    public enum RegionType {
        Linear,
        Condition,
        Tail,
    }
    @Getter private PandaIRBasicBlock block;
    @Getter
    private RegionType type;
    @Getter private LinkedList<IPandaDecompileAble> statements = new LinkedList<>();
    @Getter @Setter
    private String name;
    @Getter @Setter private boolean conditionalExp;
    @Getter @Setter
    private boolean exceptionLandingPad = false;
    @Getter @Setter
    private boolean tryCatchHelperRegion = false;
    @Getter @Setter
    private boolean logicalRegion = false;

    @SneakyThrows
    public Region(PandaIRBasicBlock block, RegionType type){
        this.block = block;
        this.type = type;
        this.statements.add(new LinearPandaStatement(block));
        this.name = block.getName();
        if (this.type == RegionType.Condition){
            PandaInstruction jumpPandaInstruction = block.getTerminator();
            if (jumpPandaInstruction instanceof JumpNotEqualPandaInstruction){
                conditionalExp = false;
            }else if (jumpPandaInstruction instanceof JumpEqualPandaInstruction){
                conditionalExp = true;
            }else throw new PandaDecompileException("generateTestExpression");
        }
    }

    public Region(String name,RegionType type){
        this.type = type;
        this.name = name;
        tryCatchHelperRegion = true;
    }

    public void addRegion(Region region){
        this.statements.addAll(region.getStatements());
    }

    public void addStatement(IPandaDecompileAble statement){
        this.statements.add(statement);
    }

    public void addFirstStatement(IPandaDecompileAble statement){
        this.statements.add(0,statement);
    }

    public void clearStatements(){
        this.statements.clear();
    }

    @Override
    public void decompile(PandaDecompilerMethodHandler methodHandler, PandaLexical pandaLexical, Object astNode){
        for (IPandaDecompileAble statement : statements) {
            statement.decompile(methodHandler,pandaLexical , astNode);
        }
    }

    public Region copy(){
        Region copyOne = new Region(this.block,this.type);
        copyOne.getStatements().clear();
        copyOne.getStatements().addAll(this.statements);
        return copyOne;
    }

    public void setType(RegionType type){
        logger.logD("setType",String.format("region %s from %s to %s",getName(),this.type,type));
        this.type = type;
    }

    public void expInvert(){
        this.conditionalExp = !conditionalExp;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public String toGraphString(){
        if (this.block != null)
            return this.name  + '|' + this.block;
        else
            return this.name;
    }
}
