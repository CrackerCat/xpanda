import jmp0.abc.opcode_gen.PandaGenerator;
import jmp0.abc.opcode_gen.PandaIsaParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public class TestGen {

    @Test
    public void test() throws IOException {
        new PandaIsaParser();
    }

    @Test
    public void testGen(){
        PandaGenerator pandaGenerator = new PandaGenerator();
        System.out.println(pandaGenerator.generateOPCodeFile());
        System.out.println(pandaGenerator.generateFormatFile());
        System.out.println(pandaGenerator.generateFlagsFile());
    }
}
