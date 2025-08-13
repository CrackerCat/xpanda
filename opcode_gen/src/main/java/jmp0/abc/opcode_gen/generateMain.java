package jmp0.abc.opcode_gen;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class generateMain {
    private static final String PATH = "core/src/main/java/jmp0/abc/disasm/types".replace('/', File.separatorChar);

    private static void writeFile(File file, String content){
        try(FileOutputStream stream = new FileOutputStream(file)) {
            stream.write(content.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        File descDir = new File(PATH);
        PandaGenerator generator = new PandaGenerator();
        //write OPCode file
        writeFile(new File(descDir,"PandaOPCode.java"), generator.generateOPCodeFile());
        //write format file
        writeFile(new File(descDir,"PandaOPCodeFormat.java"), generator.generateFormatFile());
        //write flags file
        writeFile(new File(descDir,"PandaOPCodeFlag.java"), generator.generateFlagsFile());
    }
}
