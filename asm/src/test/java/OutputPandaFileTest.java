import jmp0.abc.PandaParseException;
import jmp0.abc.asm.OutputPandaFile;
import jmp0.abc.file.PandaFile;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class OutputPandaFileTest {

    @Test
    void test() throws IOException, PandaParseException {
        InputStream stream = this.getClass().getClassLoader().getResource("test.abc").openStream();
        PandaFile pandaFile = new PandaFile(stream);
        new OutputPandaFile(pandaFile);
    }
}
