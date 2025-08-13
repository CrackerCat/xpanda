package jmp0.abc.gui.data;

import jmp0.abc.file.clazz.PandaClass;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class JTreeClassItem {
    private final String name;
    private final PandaClass pandaClass;

    public JTreeClassItem(String name, PandaClass pandaClass){
        this.name = name;
        this.pandaClass = pandaClass;
    }

    public PandaClass getPandaClass() {
        return pandaClass;
    }

    @Override
    public String toString() {
        return name;
    }
}
