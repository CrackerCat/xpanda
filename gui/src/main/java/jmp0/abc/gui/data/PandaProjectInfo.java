package jmp0.abc.gui.data;

import jmp0.abc.file.PandaFile;

import java.io.File;
import java.io.FileInputStream;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class PandaProjectInfo {
    private final String fileName;
    private final PandaFile pandaFile;
    private final File projectFile;
    private final File classDir;

    private PandaProjectInfo(String name, PandaFile pandaFile, File projectDir) {
        this.fileName = name;
        this.pandaFile = pandaFile;
        this.projectFile = projectDir;
        this.classDir = new File(projectFile,"class");
        if (!classDir.exists()) classDir.mkdirs();
    }

    public static PandaProjectInfo createPandaProject(File file) {
        try {
            PandaFile pandaFile = new PandaFile(new FileInputStream(file));
            int checksum = pandaFile.getHeader().getChecksum().intValue();
            File projectDir = new File(file.getParent(),file.getName() + "_" + checksum);
            if (!projectDir.isDirectory()){
                projectDir.mkdirs();
                return new PandaProjectInfo(file.getName(),pandaFile,projectDir);
            }else{
                return new PandaProjectInfo(file.getName(),pandaFile,projectDir);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public PandaFile getPandaFile() {
        return pandaFile;
    }

    public String getFileName() {
        return fileName;
    }

    public File getClassDir() {
        return classDir;
    }
}
