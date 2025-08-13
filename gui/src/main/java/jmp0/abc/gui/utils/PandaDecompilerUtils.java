package jmp0.abc.gui.utils;

import jmp0.abc.decompiler.PandaDecompiler;
import jmp0.abc.file.clazz.PandaClass;
import jmp0.abc.gui.DecompilerMainForm;
import jmp0.abc.gui.data.PandaProjectInfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class PandaDecompilerUtils {
    public interface DecompileCallBack{
        void complete(String out);
    }


    public static String classNameDemangle(String className) {
        if (className == null) return "";
        if (className.isEmpty()) return "";
        if (className.charAt(0) == 'L'){
            className = className.substring(1);
        }
        if (className.charAt(className.length() - 1) == ';'){
            className = className.substring(0, className.length() - 1);
        }
        return className + ".js";
    }

    public static void decompile(DecompilerMainForm decompilerMainForm, PandaClass pandaClass, DecompileCallBack callBack) {
        PandaProjectInfo projectInfo = decompilerMainForm.decompilerGUIContext.getProjectInfo();
        File classDir = projectInfo.getClassDir();
        File resultFile = new File(classDir,classNameDemangle(pandaClass.getName().getContent()).replace("/",File.separator));
        if (resultFile.isFile() && resultFile.canRead()){
            try(FileInputStream inputStream = new FileInputStream(resultFile)) {
               String out = new String(inputStream.readAllBytes());
                callBack.complete(out);
            } catch (IOException e) {
                callBack.complete("/**read file from project failed**/");
            }
            return;
        }
        new Thread(() -> {
            try {
                PandaDecompiler decompiler = new PandaDecompiler();
                String out = decompiler.decompileClass(pandaClass);
                PandaDecompiler.saveFile(classDir,pandaClass,out);
                callBack.complete(out);
            } catch (IOException e) {
                callBack.complete("/**decompile failed**/");
            }
        }).start();
    }
}
