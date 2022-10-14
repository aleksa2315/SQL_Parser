package controller;

import gui.view.MainFrame;

import java.rmi.server.ExportException;

public class ActionManager {
    private ExitAction exitAction;
    private BulkImportAction bulkImportAction;
    private ExportAction exportAction;
    private SqlPrettyAction sqlPrettyAction;
    public ActionManager() {
        exitAction = new ExitAction(MainFrame.getInstance());
        bulkImportAction = new BulkImportAction();
        exportAction = new ExportAction();
        sqlPrettyAction = new SqlPrettyAction();
    }

    public SqlPrettyAction getSqlPrettyAction() {
        return sqlPrettyAction;
    }

    public void setSqlPrettyAction(SqlPrettyAction sqlPrettyAction) {
        this.sqlPrettyAction = sqlPrettyAction;
    }

    public BulkImportAction getBulkImportAction() {
        return bulkImportAction;
    }

    public void setBulkImportAction(BulkImportAction bulkImportAction) {
        this.bulkImportAction = bulkImportAction;
    }

    public ExportAction getExportAction() {
        return exportAction;
    }

    public void setExportAction(ExportAction exportAction) {
        this.exportAction = exportAction;
    }

    public ExitAction getExitAction() {
        return exitAction;
    }

    public void setExitAction(ExitAction exitAction) {
        this.exitAction = exitAction;
    }
}
