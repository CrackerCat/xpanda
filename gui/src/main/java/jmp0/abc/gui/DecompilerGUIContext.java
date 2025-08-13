package jmp0.abc.gui;

import jmp0.abc.file.PandaFile;
import jmp0.abc.file.clazz.PandaClass;
import jmp0.abc.gui.data.PandaProjectInfo;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class DecompilerGUIContext {
    private PandaProjectInfo projectInfo;
    private final ConcurrentHashMap<String,PandaClass> classMap = new ConcurrentHashMap<>();

    public void clearALL(){
        this.projectInfo = null;
        this.classMap.clear();
    }

    public PandaProjectInfo getProjectInfo() {
        return projectInfo;
    }

    public void setProjectInfo(PandaProjectInfo projectInfo) {
        this.projectInfo = projectInfo;
    }

    public ConcurrentHashMap<String, PandaClass> getClassMap() {
        return classMap;
    }
}
