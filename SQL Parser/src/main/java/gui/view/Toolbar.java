package gui.view;

import javax.swing.*;

public class Toolbar extends JToolBar {
    public Toolbar(){
        super(HORIZONTAL);
        setFloatable(false);
        add(MainFrame.getInstance().getActionManager().getExitAction());
        addSeparator();
        add(MainFrame.getInstance().getActionManager().getExportAction());
        add(MainFrame.getInstance().getActionManager().getBulkImportAction());
        addSeparator();
        add(MainFrame.getInstance().getActionManager().getSqlPrettyAction());
    }
}
