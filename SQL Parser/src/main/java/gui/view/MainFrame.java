package gui.view;

import controller.ActionManager;
import controller.ExitAction;
import controller.RunQuery;
import core.AppCore;
import queryChecker.QueryCheckerImpl;
import resource.implementation.InformationResource;
import tree.Implementation.SelectionListener;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;

public class MainFrame extends JFrame {
    private AppCore appCore;
    private static MainFrame instance;
    private JToolBar toolBar;
    private JPanel desktop;
    private JScrollPane scrollPane;
    private JTable jTable;
    private ActionManager actionManager;
    private JTree jTree;

    private JTextPane textPane;

    public void initGui() {
        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize();

        int screenWidth = screenSize.width / 2;
        int screenHeight = screenSize.height / 2;

        setSize(screenWidth, screenHeight);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        toolBar = new Toolbar();
        add(toolBar, BorderLayout.NORTH);

        this.desktop = new JPanel();
        this.desktop.setLayout(new BorderLayout());

        jTable = new JTable();
        jTable.setFillsViewportHeight(true);

        textPane = new JTextPane();
        textPane.setPreferredSize(new Dimension(200,200));
        JButton dugme = new JButton("Run Query");
        dugme.addActionListener(new RunQuery());
//        dugme.addActionListener(e -> {
//            QueryCheckerImpl queryChecker = new QueryCheckerImpl();
//            queryChecker.uradi();
//        });
        dugme.setSize(100,50);
        desktop.add(textPane,BorderLayout.CENTER);
        desktop.add(dugme,BorderLayout.SOUTH);

        JSplitPane tabelaSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT,desktop,new JScrollPane(jTable));

        JSplitPane glavniSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,scrollPane,tabelaSplit);

        this.add(glavniSplit,BorderLayout.CENTER);
        this.pack();
        setLocationRelativeTo(null);
        setVisible(true);

    }
    public MainFrame(){
        this.addWindowListener(new ExitAction(this));
    }

    public static MainFrame getInstance() {
        if (instance == null){
            instance = new MainFrame();
            instance.initialise();
        }
        return instance;
    }

    public void initTree(){
        DefaultTreeModel defaultTreeModel = appCore.loadResource();
        jTree = new JTree(defaultTreeModel);
        jTree.addTreeSelectionListener(new SelectionListener());
        scrollPane = new JScrollPane(jTree, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED );
        pack();
    }

    public void setAppCore(AppCore appCore){
        this.appCore = appCore;
    }
    public void setTable(){
        this.jTable.setModel(appCore.getTableModel());
    }
    public AppCore getAppCore() {
        return appCore;
    }

    public JTree getjTree() {
        return jTree;
    }

    public void setjTree(JTree jTree) {
        this.jTree = jTree;
    }

    public void initialise(){actionManager = new ActionManager();}

    public static void setInstance(MainFrame instance) {
        MainFrame.instance = instance;
    }

    public JToolBar getToolBar() {
        return toolBar;
    }

    public void setToolBar(JToolBar toolBar) {
        this.toolBar = toolBar;
    }

    public JPanel getDesktop() {
        return desktop;
    }

    public void setDesktop(JPanel desktop) {
        this.desktop = desktop;
    }

    public JScrollPane getScrollPane() {
        return scrollPane;
    }

    public void setScrollPane(JScrollPane scrollPane) {
        this.scrollPane = scrollPane;
    }

    public JTable getjTable() {
        return jTable;
    }

    public void setjTable(JTable jTable) {
        this.jTable = jTable;
    }

    public ActionManager getActionManager() {
        return actionManager;
    }

    public void setActionManager(ActionManager actionManager) {
        this.actionManager = actionManager;
    }

    public JTextPane getTextPane() {
        return textPane;
    }

    public void setTextPane(String text) {
        DefaultStyledDocument document = new DefaultStyledDocument();
        textPane.setDocument(document);
        StyleContext context = new StyleContext();
        Style style = context.addStyle("blue", null);
        StyleConstants.setForeground(style, Color.BLUE);
        Style style0 = context.addStyle("blue", null);
        StyleConstants.setForeground(style0, Color.BLACK);

        try {
            int z = text.length();
            for(int y = 0; y < z; y++){
                if(Character.isUpperCase(text.charAt(y))){
                    document.insertString(y, String.valueOf(text.charAt(y)), style);

                }else{
                    document.insertString(y, String.valueOf(text.charAt(y)), style0);
                }
            }
            document.insertString(z, " ", style0);

        } catch (BadLocationException e) {
            throw new RuntimeException(e);
        }
    }
}
