package jmp0.abc.gui.utils;

import jmp0.abc.gui.DecompilerMainForm;
import jmp0.abc.gui.data.ClassTabbedDataComponent;
import jmp0.abc.gui.data.JTreeClassItem;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class PandaUIUtils {

    public interface JTreeNodeTraverse {
        void traverse(DefaultMutableTreeNode node);
    }

    public static void decompileWithUI(DecompilerMainForm instance,int index){
        ClassTabbedDataComponent component = (ClassTabbedDataComponent) instance.classShowTabbedPane.getTabComponentAt(index);
        if (component != null) {
            instance.codeTextArea.setText("//decompile...");
            PandaDecompilerUtils.decompile(instance, component.getPandaClass(), new PandaDecompilerUtils.DecompileCallBack() {
                @Override
                public void complete(String out) {
                    int index = instance.classShowTabbedPane.getSelectedIndex();
                    if (instance.classShowTabbedPane.getTabComponentAt(index) != component) return;
                    instance.codeTextArea.setText(out);
                    instance.codeTextArea.setCaretPosition(0);
                }
            });
        }
    }

    public static boolean isControlOrCommand(KeyEvent keyEvent){
        return keyEvent.isMetaDown() || keyEvent.isControlDown();
    }

    private static void travelsAllNodes(JTree tree, JTreeNodeTraverse travels) {
        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
        for (int i = 0; i < root.getChildCount(); i++) {
            travelsNode(tree, (DefaultMutableTreeNode) root.getChildAt(i),travels);
        }

    }

    private static void travelsNode(JTree tree, DefaultMutableTreeNode node, JTreeNodeTraverse travels) {
        int childCount = node.getChildCount();
        for (int i = 0; i < childCount; i++) {
            DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) node.getChildAt(i);
            travelsNode(tree, childNode,travels);
        }
        travels.traverse(node);
    }

    public static void collapseAll(JTree tree){
        int count = tree.getRowCount();
        if (count == 0) return;
        travelsAllNodes(tree, node -> {
            TreePath path = new TreePath(node.getPath());
            tree.collapsePath(path);
        });
    }

    public static void expandSearchTestNode(JTree tree,String text){
        collapseAll(tree);
        travelsAllNodes(tree, node -> {
            Object[] nodes = node.getPath();
            for (Object treeNode : nodes) {
                if (treeNode instanceof DefaultMutableTreeNode defaultMutableTreeNode){
                    Object item =  defaultMutableTreeNode.getUserObject();
                    if (item instanceof JTreeClassItem && item.toString().toLowerCase().contains(text.toLowerCase())){
                        DefaultMutableTreeNode needOpenNode = node;
                        if (defaultMutableTreeNode.getChildCount() == 0){
                            needOpenNode = (DefaultMutableTreeNode) defaultMutableTreeNode.getParent();
                        }
                        TreePath path = new TreePath(needOpenNode.getPath());
                        tree.expandPath(path);
                    }

                }
            }
        });
    }
}
