import jmp0.abc.disasm.ins.binary.BinaryOperationPandaInstruction;
import jmp0.abc.disasm.types.PandaOPCode;
import org.junit.jupiter.api.Test;

public class GenTest {

    @Test
    public void genSwitch(){
//        for (PandaOPCode value : PandaOPCode.values()) {
//            if (value.getName().contains("supercallthisrange") && !value.getName().contains("deprecated")){
//                System.out.println(value.name()+"PandaInstruction");
//                System.out.println("            case %%:\n                return new jmp0.abc.disasm.ins.visitors.copyrestargs.%%PandaInstruction(pc,opCode,param,pandaMethod);".replace("%%",value.name()));
//            }
//        }
        for (PandaOPCode pandaOPCode : PandaOPCode.values()) {
            if (pandaOPCode.getCode() >= 28 && pandaOPCode.getCode() <= 36){
//                System.out.println(pandaOPCode.name()+"PandaInstruction");
                System.out.println("            case %%:\n                return new jmp0.abc.disasm.ins.unary.%%PandaInstruction(pc,opCode,param,pandaMethod);".replace("%%",pandaOPCode.name()));
            }
        }
    }
}
