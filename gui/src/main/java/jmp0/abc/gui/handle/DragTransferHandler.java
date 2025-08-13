package jmp0.abc.gui.handle;

import jmp0.abc.gui.DecompilerMainForm;

import javax.swing.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.util.List;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class DragTransferHandler extends TransferHandler {
    private final DecompilerMainForm decompilerMainForm;
    public DragTransferHandler(DecompilerMainForm decompilerMainForm){
        this.decompilerMainForm = decompilerMainForm;
    }

    @Override
    public boolean importData(JComponent comp, Transferable t) {
        try {
            Object arrayList = t.getTransferData(DataFlavor.javaFileListFlavor);
            if (arrayList instanceof List<?>){
                int size = ((List<?>) arrayList).size();
                if (size != 1){
                    //multi file not support now.
                    return false;
                }
                File file = (File) ((List<?>) arrayList).get(0);
                this.decompilerMainForm.setClassJTreeContentWithABCFile(file);
                return true;
            }
            return false;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean canImport(JComponent comp, DataFlavor[] flavors) {
        for (DataFlavor flavor : flavors) {
            if (DataFlavor.javaFileListFlavor.equals(flavor)) {
                return true;
            }
        }
        return false;
    }
}
