package jmp0.abc.gui.data;

import jmp0.abc.file.clazz.PandaClass;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class ClassTabbedDataComponent extends JPanel {
    private final PandaClass pandaClass;
    public ClassTabbedDataComponent(JTreeClassItem treeClassItem,JTabbedPane tabbedPane) {
        super(new GridBagLayout());
        this.pandaClass = treeClassItem.getPandaClass();
        ClassTabbedDataComponent instance = this;
        JLabel nameLabel = new JLabel(treeClassItem + " ");
        JLabel closeLabel = new JLabel("[x] ");
        closeLabel.setHorizontalAlignment(JLabel.RIGHT);
        nameLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getSource() == nameLabel && e.getClickCount() == 1 && e.getButton() == MouseEvent.BUTTON1) {
                    int index = tabbedPane.indexOfTabComponent(instance);
                    if (index != -1){
                        tabbedPane.setSelectedIndex(index);
                    }
                }
            }
        });

        closeLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getSource() == closeLabel && e.getClickCount() == 1 && e.getButton() == MouseEvent.BUTTON1) {
                    int index = tabbedPane.indexOfTabComponent(instance);
                    if (index != -1){
                        tabbedPane.removeTabAt(index);
                    }
                }
            }
        });
        add(nameLabel);
        add(closeLabel);
    }

    public PandaClass getPandaClass() {
        return pandaClass;
    }
}
