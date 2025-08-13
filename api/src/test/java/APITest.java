import jmp0.abc.api.controller.RestApiController;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public class APITest {
    @Test
    public void test() {
        try {
            File files = new File("files");
            if (!files.isDirectory()) {
                files.mkdir();
            }
            byte[] bs = new byte[]{1,2,3};
            File file1 = new File("files/" + System.currentTimeMillis()+".abc");
            try(FileOutputStream s = new FileOutputStream(file1)) {
                s.write(bs);
            }
        }catch (Throwable ignore){}
    }
}
