package jmp0.abc.gui;

import com.formdev.flatlaf.FlatIntelliJLaf;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import jmp0.abc.file.PandaFile;
import jmp0.abc.file.clazz.PandaClass;
import jmp0.abc.file.desc.IndexHeader;
import jmp0.abc.gui.data.ClassTabbedDataComponent;
import jmp0.abc.gui.data.JTreeClassItem;
import jmp0.abc.gui.data.PandaProjectInfo;
import jmp0.abc.gui.handle.CodeJAreaKeyAdapter;
import jmp0.abc.gui.handle.DragTransferHandler;
import jmp0.abc.gui.utils.PandaDecompilerUtils;
import jmp0.abc.gui.utils.PandaUIUtils;
import org.fife.ui.rsyntaxtextarea.ActiveLineRangeEvent;
import org.fife.ui.rsyntaxtextarea.ActiveLineRangeListener;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.fife.ui.rtextarea.SearchContext;
import org.fife.ui.rtextarea.SearchEngine;
import org.fife.ui.rtextarea.SearchResult;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public class DecompilerMainForm {
    public DecompilerGUIContext decompilerGUIContext = new DecompilerGUIContext();

    private JTree classJTree;
    private JButton testJButton;
    private JPanel mainJPanel;
    private JPanel topJPanel;
    private JSplitPane belowJPanel;
    private JTextField classSearchTextField;
    private JPanel classTreeJPanel;
    private JButton collapseClassTreeButton;
    public JTextField codeSearchTextField;
    public JPanel codePanel;
    public RSyntaxTextArea codeTextArea;
    private RTextScrollPane codeTScrollPane;
    public JTabbedPane classShowTabbedPane;

    private final DecompilerMainForm instance = this;

    public DecompilerMainForm() {
        classJTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getSource() == classJTree && e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
                    TreePath selPath = classJTree.getPathForLocation(e.getX(), e.getY());
                    if (selPath != null) {
                        DefaultMutableTreeNode node = (DefaultMutableTreeNode) selPath.getLastPathComponent();
                        if (node.getChildCount() != 0) return;
                        JTreeClassItem treeClassItem = (JTreeClassItem) node.getUserObject();
                        int count = classShowTabbedPane.getTabCount();
                        for (int i = 0; i < count; i++) {
                            ClassTabbedDataComponent component1 = (ClassTabbedDataComponent) classShowTabbedPane.getTabComponentAt(i);
                            if (component1.getPandaClass() == treeClassItem.getPandaClass()) {
                                classShowTabbedPane.setSelectedIndex(i);
                                return;
                            }
                        }
                        classShowTabbedPane.addTab(treeClassItem.toString(), null);
                        int index = classShowTabbedPane.getTabCount() - 1;
                        classShowTabbedPane.setTabComponentAt(index, new ClassTabbedDataComponent(treeClassItem, classShowTabbedPane));
                        classShowTabbedPane.setSelectedIndex(index);
                        PandaUIUtils.decompileWithUI(instance, index);
                    }
                }
            }
        });

        classJTree.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (PandaUIUtils.isControlOrCommand(e) && e.getKeyCode() == KeyEvent.VK_F) {
                    classSearchTextField.setVisible(!classSearchTextField.isVisible());
                    if (classSearchTextField.isVisible()) {
                        classSearchTextField.requestFocus();
                        classSearchTextField.selectAll();
                    }
                    classTreeJPanel.revalidate();
                    classTreeJPanel.repaint();
                }
            }
        });
        collapseClassTreeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getSource() == collapseClassTreeButton
                        && e.getClickCount() == 1
                        && e.getButton() == MouseEvent.BUTTON1) {
                    PandaUIUtils.collapseAll(classJTree);
                }
            }
        });
        classSearchTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String searchText = classSearchTextField.getText();
                    PandaUIUtils.expandSearchTestNode(classJTree, searchText);
                    classJTree.requestFocus();
                }
            }
        });

        codeSearchTextField.addKeyListener(new KeyAdapter() {
            private final SearchContext searchContext = new SearchContext();

            {
                searchContext.setMatchCase(false);
                searchContext.setWholeWord(false);
                searchContext.setSearchForward(true);
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String searchText = codeSearchTextField.getText();
                    searchContext.setSearchFor(searchText);
                    SearchResult result = SearchEngine.find(codeTextArea, searchContext);
                    if (result.getCount() == 0 && result.getMarkedCount() != 0) {
                        codeTextArea.setCaretPosition(0);
                    }
                }
            }
        });
        codeTextArea.addKeyListener(new CodeJAreaKeyAdapter(instance));
        classShowTabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int index = classShowTabbedPane.getSelectedIndex();
                if (index == -1) {
                    codeTextArea.setText("");
                    return;
                }
                PandaUIUtils.decompileWithUI(instance, index);
            }
        });
        codeTextArea.addCaretListener(new CaretListener() {
            private String nowSelectText;
            private int posion = -1;
            private final Timer timer = new Timer();
            private final SearchContext searchContext = new SearchContext();

            {
                searchContext.setMatchCase(true);
                searchContext.setWholeWord(false);
                searchContext.setSearchForward(false);
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (posion != -1 && posion != codeTextArea.getCaretPosition()) {
                            codeTextArea.clearMarkAllHighlights();
                            posion = -1;
                        }
                        if (nowSelectText != null && !nowSelectText.isEmpty() && !codeSearchTextField.hasFocus()) {
                            System.out.println(nowSelectText);
                            searchContext.setSearchFor(nowSelectText);
                            try {
                                SearchEngine.markAll(codeTextArea, searchContext);
                            } catch (Throwable ignore) {
                            }
                            nowSelectText = null;
                        }

                    }
                }, 0, 500);
            }


            @Override
            public void caretUpdate(CaretEvent e) {
                int dot = e.getDot(); // 返回插入符号的位置
                int mark = e.getMark(); // 返回选择文本的起始位置
                if (dot != mark) {
                    nowSelectText = codeTextArea.getSelectedText();
                    posion = codeTextArea.getCaretPosition();
                }
            }
        });
    }

    public static void main(String[] args) {
        FlatIntelliJLaf.setup();
        Toolkit.getDefaultToolkit().setDynamicLayout(true);
        JFrame frame = new JFrame("xpanda decompiler gui");
        DecompilerMainForm decompilerMainForm = new DecompilerMainForm();
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("empty class tree");
        DefaultTreeModel treeModel = new DefaultTreeModel(root);
        decompilerMainForm.classSearchTextField.setVisible(false);
        decompilerMainForm.codeSearchTextField.setVisible(false);
        decompilerMainForm.classJTree.setModel(treeModel);
        decompilerMainForm.codeTScrollPane.setFoldIndicatorEnabled(true);
        decompilerMainForm.codeTScrollPane.setLineNumbersEnabled(true);
        decompilerMainForm.codeTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT);
        decompilerMainForm.codeTextArea.setCodeFoldingEnabled(true);
        decompilerMainForm.codeTextArea.setTransferHandler(new DragTransferHandler(decompilerMainForm));
        decompilerMainForm.mainJPanel.setTransferHandler(new DragTransferHandler(decompilerMainForm));
        decompilerMainForm.classJTree.setTransferHandler(new DragTransferHandler(decompilerMainForm));
        frame.setContentPane(decompilerMainForm.mainJPanel);
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        frame.setSize(screenSize.width / 2, screenSize.height / 2);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /** Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        mainJPanel = new JPanel();
        mainJPanel.setLayout(new GridBagLayout());
        topJPanel = new JPanel();
        topJPanel.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        mainJPanel.add(topJPanel, gbc);
        testJButton = new JButton();
        testJButton.setText("open");
        topJPanel.add(testJButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        topJPanel.add(spacer1, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        collapseClassTreeButton = new JButton();
        collapseClassTreeButton.setText("collapseAll");
        topJPanel.add(collapseClassTreeButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        belowJPanel = new JSplitPane();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.weighty = 50.0;
        gbc.fill = GridBagConstraints.BOTH;
        mainJPanel.add(belowJPanel, gbc);
        classTreeJPanel = new JPanel();
        classTreeJPanel.setLayout(new GridBagLayout());
        belowJPanel.setLeftComponent(classTreeJPanel);
        final JScrollPane scrollPane1 = new JScrollPane();
        scrollPane1.setHorizontalScrollBarPolicy(30);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        classTreeJPanel.add(scrollPane1, gbc);
        classJTree = new JTree();
        classJTree.setEditable(false);
        scrollPane1.setViewportView(classJTree);
        classSearchTextField = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        classTreeJPanel.add(classSearchTextField, gbc);
        codePanel = new JPanel();
        codePanel.setLayout(new GridBagLayout());
        belowJPanel.setRightComponent(codePanel);
        codeSearchTextField = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        codePanel.add(codeSearchTextField, gbc);
        codeTScrollPane = new RTextScrollPane();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 1000.0;
        gbc.weighty = 1000.0;
        gbc.fill = GridBagConstraints.BOTH;
        codePanel.add(codeTScrollPane, gbc);
        codeTextArea = new RSyntaxTextArea();
        codeTextArea.setEditable(false);
        codeTScrollPane.setViewportView(codeTextArea);
        classShowTabbedPane = new JTabbedPane();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        codePanel.add(classShowTabbedPane, gbc);
    }

    /** @noinspection ALL */
    public JComponent $$$getRootComponent$$$() {
        return mainJPanel;
    }

    private void buildJTreeWithClassMap() {
        ConcurrentHashMap<String, PandaClass> classMap = decompilerGUIContext.getClassMap();
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(decompilerGUIContext.getProjectInfo().getFileName());
        DefaultTreeModel treeModel = new DefaultTreeModel(root);
        HashMap<String, DefaultMutableTreeNode> treeNodeHashMap = new HashMap<String, DefaultMutableTreeNode>();
        for (PandaClass value : classMap.values()) {
            String result = PandaDecompilerUtils.classNameDemangle(value.getName().getContent());
            String[] classPath = result.split("/");
            DefaultMutableTreeNode pre = root;
            String name = "";
            for (String s : classPath) {
                name += s;
                DefaultMutableTreeNode treeNode = treeNodeHashMap.get(name);
                if (treeNode == null) {
                    treeNode = new DefaultMutableTreeNode(new JTreeClassItem(s, value));
                    treeNodeHashMap.put(name, treeNode);
                    treeModel.insertNodeInto(treeNode, pre, pre.getChildCount());
                }
                pre = treeNode;
            }
        }
        this.classJTree.setModel(treeModel);
    }

    public void setClassJTreeContentWithABCFile(File file) {
        try {
            PandaFile pandaFile = new PandaFile(new FileInputStream(file));
            decompilerGUIContext.clearALL();
            PandaProjectInfo info = PandaProjectInfo.createPandaProject(file);
            if (info == null) return;
            decompilerGUIContext.setProjectInfo(info);
            ConcurrentHashMap<String, PandaClass> classMap = decompilerGUIContext.getClassMap();
            for (IndexHeader indexHeader : decompilerGUIContext.getProjectInfo().getPandaFile().getIndexHeaders()) {
                for (PandaClass pandaClass : indexHeader.getPandaClasses()) {
                    classMap.put(pandaClass.getName().getContent(), pandaClass);
                }
            }
            this.buildJTreeWithClassMap();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(mainJPanel, "open file failed!");
        }
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
