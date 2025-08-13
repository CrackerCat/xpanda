package jmp0.abc.gui.handle;

import jmp0.abc.gui.DecompilerMainForm;
import jmp0.abc.gui.utils.PandaUIUtils;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class CodeJAreaKeyAdapter extends KeyAdapter {
    private final DecompilerMainForm decompilerMainForm;
    public CodeJAreaKeyAdapter(DecompilerMainForm decompilerMainForm) {
        this.decompilerMainForm = decompilerMainForm;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (PandaUIUtils.isControlOrCommand(e) && e.getKeyCode() == KeyEvent.VK_F) {
            decompilerMainForm.codeSearchTextField.setVisible(!decompilerMainForm.codeSearchTextField.isVisible());
            decompilerMainForm.codePanel.revalidate();
            decompilerMainForm.codePanel.repaint();
            if (decompilerMainForm.codeSearchTextField.isVisible()) {
                decompilerMainForm.codeSearchTextField.selectAll();
                decompilerMainForm.codeSearchTextField.requestFocus();
            }
        }
    }
}
