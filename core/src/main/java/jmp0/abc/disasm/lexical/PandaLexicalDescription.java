package jmp0.abc.disasm.lexical;

import jmp0.abc.disasm.block.PandaIRCFG;
import jmp0.abc.file.method.PandaMethod;
import jmp0.abc.util.PandaLogger;
import lombok.Getter;

import java.util.HashMap;

public final class PandaLexicalDescription {
    @Getter private final PandaIRCFG pandaIRCFG;
    private final HashMap<Integer,Integer> opCodeLevelMap = new HashMap<>();

    public PandaLexicalDescription(PandaIRCFG ircfg){
        this.pandaIRCFG = ircfg;
    }

    public void recordLevel(int pc,int level){
        opCodeLevelMap.put(pc, level);
    }

    public int getLevel(int pc){
        return opCodeLevelMap.get(pc);
    }
}
