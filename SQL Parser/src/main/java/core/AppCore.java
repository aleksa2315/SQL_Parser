package core;

import database.Database;
import database.DatabaseImpl;
import database.MYSQLRepository;
import database.settings.Settings;
import database.settings.SettingsImpl;
import gui.view.MainFrame;
import gui.view.MyApp;
import gui.view.table.TableModel;
import resource.data.Row;
import resource.implementation.InformationResource;
import tree.Implementation.TreeImplementation;
import tree.Tree;
import utils.Constants;

import javax.swing.tree.DefaultTreeModel;
import java.util.ArrayList;
import java.util.List;

public class AppCore extends AppFramework {
    private Database database;
    private Settings settings;
    private TableModel tableModel;
    private DefaultTreeModel defaultTreeModel;
    private Tree tree;
    private static AppCore instance;

    private List<Row> rowList = new ArrayList<>();

    public List<Row> getRowList() {
        return rowList;
    }

    private AppCore(){
        this.settings = initSettings();
        this.database = new DatabaseImpl(new MYSQLRepository(this.settings));
        this.tableModel = new TableModel();
        this.tree = new TreeImplementation();
    }

    @Override
    public void run() {
        this.gui.start();
    }

    public static AppCore getInstance() {
        if (instance == null){
            instance = new AppCore();
        }
        return instance;
    }

    public static void main(String[] args) {
        Gui gui = new MyApp();
        AppCore appCore = AppCore.getInstance();
        appCore.init(gui);
        MainFrame.getInstance().setAppCore(appCore);
        appCore.run();
        MainFrame.getInstance().setTable();
    }

    public DefaultTreeModel loadResource(){
        InformationResource ir = (InformationResource) this.database.loadResource();
        return this.tree.generateTree(ir);
    }

    public void readDataFromTable(String fromTable){
        this.rowList = (this.database.readDataFromTable(fromTable));
        tableModel.setRows(this.rowList);
    }

    public void fetchData(String sql){
        if (sql.contains("select")) {
            this.rowList = (this.database.fetchDataFromDatabase(sql));
            tableModel.setRows(this.rowList);
        }else{
            this.database.fetchDataFromDatabase(sql);
        }
    }

    private Settings initSettings() {
        Settings settingsImplementation = new SettingsImpl();
        settingsImplementation.addParameter("mysql_ip", Constants.MYSQL_IP);
        settingsImplementation.addParameter("mysql_database", Constants.MYSQL_DATABASE);
        settingsImplementation.addParameter("mysql_username", Constants.MYSQL_USERNAME);
        settingsImplementation.addParameter("mysql_password", Constants.MYSQL_PASSWORD);
        return settingsImplementation;
    }
    public Database getDatabase() {
        return database;
    }

    public void setDatabase(Database database) {
        this.database = database;
    }

    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    public TableModel getTableModel() {
        return tableModel;
    }

    public void setTableModel(TableModel tableModel) {
        this.tableModel = tableModel;
    }

    public DefaultTreeModel getDefaultTreeModel() {
        return defaultTreeModel;
    }

    public void setDefaultTreeModel(DefaultTreeModel defaultTreeModel) {
        this.defaultTreeModel = defaultTreeModel;
    }

    public Tree getTree() {
        return tree;
    }

    public void setTree(Tree tree) {
        this.tree = tree;
    }

    public static void setInstance(AppCore instance) {
        AppCore.instance = instance;
    }
}
