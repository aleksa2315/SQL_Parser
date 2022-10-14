package database;

import database.settings.Settings;
import resource.DBNode;
import resource.data.Row;
import resource.enums.AttributeType;
import resource.implementation.Attribute;
import resource.implementation.Entity;
import resource.implementation.InformationResource;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class MYSQLRepository implements Repository {
    private Settings settings;
    private Connection connection;
    private DatabaseMetaData metaData;

    private ArrayList<String> listaTabela = new ArrayList<>();

    public MYSQLRepository(Settings settings) {
        this.settings = settings;
    }

    private void initConnection() throws SQLException, ClassNotFoundException{
        String ip = (String) settings.getParameter("mysql_ip");
        String database = (String) settings.getParameter("mysql_database");
        String username = (String) settings.getParameter("mysql_username");
        String password = (String) settings.getParameter("mysql_password");
        connection = DriverManager.getConnection("jdbc:mysql://"+ip+"/"+database,username,password);
        metaData = connection.getMetaData();
    }

    private void closeConnection(){
        try{
            connection.close();
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        finally {
            connection = null;
        }
    }


    @Override
    public DBNode getSchema() {

        try{
            this.initConnection();

            DatabaseMetaData metaData = connection.getMetaData();
            InformationResource ir = new InformationResource("d");

            String tableType[] = {"TABLE"};
            ResultSet tables = metaData.getTables(connection.getCatalog(), null, null, tableType);

            while (tables.next()){

                String tableName = tables.getString("TABLE_NAME");
                listaTabela.add(tableName);
                if(tableName.contains("trace"))continue;
                Entity newTable = new Entity(tableName, ir);
                ir.addChild(newTable);

                //Koje atribute imaja ova tabela?

                ResultSet columns = metaData.getColumns(connection.getCatalog(), null, tableName, null);

                while (columns.next()){

                    // COLUMN_NAME TYPE_NAME COLUMN_SIZE ....

                    String columnName = columns.getString("COLUMN_NAME");
                    String columnType = columns.getString("TYPE_NAME");

                    System.out.println(columnType);

                    int columnSize = Integer.parseInt(columns.getString("COLUMN_SIZE"));

//                    ResultSet pkeys = metaData.getPrimaryKeys(connection.getCatalog(), null, tableName);
//
//                    while (pkeys.next()){
//                        String pkColumnName = pkeys.getString("COLUMN_NAME");
//                    }


                    Attribute attribute = new Attribute(columnName, newTable,
                            AttributeType.valueOf(
                                    Arrays.stream(columnType.toUpperCase().split(" "))
                                            .collect(Collectors.joining("_"))),
                            columnSize);
                    newTable.addChild(attribute);

                }




            }



            return ir;
            //String isNullable = columns.getString("IS_NULLABLE");
            // ResultSet foreignKeys = metaData.getImportedKeys(connection.getCatalog(), null, table.getName());
            // ResultSet primaryKeys = metaData.getPrimaryKeys(connection.getCatalog(), null, table.getName());

        }
        catch (SQLException e1) {
            e1.printStackTrace();
        }
        catch (ClassNotFoundException e2){ e2.printStackTrace();}
        finally {
            this.closeConnection();
        }

        return null;
    }

    @Override
    public List<Row> get(String from) {

        List<Row> rows = new ArrayList<>();


        try{
            this.initConnection();

            String query = "SELECT * FROM " + from;
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet rs = preparedStatement.executeQuery();
            ResultSetMetaData resultSetMetaData = rs.getMetaData();

            populateRowList(from, rows, rs, resultSetMetaData);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            this.closeConnection();
        }

        return rows;
    }

    private void populateRowList(String from, List<Row> rows, ResultSet rs, ResultSetMetaData resultSetMetaData) throws SQLException {
        while (rs.next()){

            Row row = new Row();
            row.setName(from);

            for (int i = 1; i<= resultSetMetaData.getColumnCount(); i++){
                row.addField(resultSetMetaData.getColumnName(i), rs.getString(i));
            }
            rows.add(row);


        }
    }

    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }
    public void runCsvCode(List<String[]> data, String selektovanaTabela){
        try {
            this.initConnection();
            Statement stmt = connection.createStatement();
            connection.setAutoCommit(false);

            StringBuilder query = new StringBuilder("INSERT INTO ");
            for(int i = 0; i < data.size(); i++){
                if(i == 0){
                    query.append(selektovanaTabela).append(" (");
                    for (String str:
                            data.get(i)) {
                        query.append(str.replaceAll("\"","")).append(",");
                    }
                    query = new StringBuilder(query.substring(0, query.length() - 1) + ") values (");

                }else{
                    StringBuilder qry = new StringBuilder(query.toString());
                    for (String str:
                            data.get(i)) {
                        qry.append(str).append(",");
                    }
                    qry = new StringBuilder(qry.substring(0, qry.length() - 1) + ")");
                    stmt.addBatch(qry.toString());
                    System.out.println(qry);
                }
                int[] result = stmt.executeBatch();
                System.out.println("The number of rows inserted: "+ result.length);

                connection.commit();
            }
        }catch (SQLException | ClassNotFoundException e ){
            e.printStackTrace();
        }finally {
            this.closeConnection();
        }
    }

    @Override
    public boolean checkColumns(String tableName, List<String[]> csvColumns) {
        try {
            this.initConnection();
            DatabaseMetaData databaseMetaData = connection.getMetaData();

            ResultSet columns =
                    databaseMetaData.getColumns(null, null, tableName, null);
            List<String> columnTypes = new ArrayList<>();
            List<String> columnNames = new ArrayList<>();
            List<Integer> columnSizes = new ArrayList<>();

            while (columns.next()) {
                columnNames.add(columns.getString("COLUMN_NAME"));
                columnTypes.add(columns.getString("TYPE_NAME"));
                columnSizes.add(columns.getInt("COLUMN_SIZE"));
            }
            int br = 0;
            for (String[] str :
                    csvColumns) {
                if (br == 0){
                    boolean duplicates = checkDuplicates(str);
                    if (duplicates) return false;

                    for (String csv :
                            str) {
                            boolean bool = false;

                            for (String table:
                                 columnNames) {
                                if (table.equalsIgnoreCase(csv.replaceAll("\"", ""))){
                                    bool = true;
                                    break;
                                }
                            }

                            if (!bool){
                                System.out.println("Kolona se ne nalazi u tabeli! Kolona:" + csv);
                                return false;
                            }
                    }
                    br++;
                }else {
                    if (str.length != columnNames.size()){
                        System.out.println("Fale podaci za red! Red broj:"+csvColumns.indexOf(str));
                        return false;
                    }

                    for(int i = 0; i < str.length;i++){
                        switch (columnTypes.get(i).toLowerCase()){
                            case "int": case "int unsigned":
                                try {
                                    int intValue = Integer.parseInt(str[i].replaceAll("\"", ""));
                                } catch (NumberFormatException e) {
                                    System.out.println("Polje nije dobrog tipa! Polje: " +str[i]);
                                    return false;
                                }
                                break;
                            case "date":
                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss:ms");
                                dateFormat.setLenient(false);
                                try {
                                    dateFormat.parse(str[i].replaceAll("\"", "").trim());
                                } catch (ParseException pe) {
                                    System.out.println("Polje nije dobrog tipa! Polje: " +str[i]);
                                    return false;
                                }
                                break;
                            case "decimal":
                                try {
                                    Double intValue = Double.parseDouble(str[i].replaceAll("\"", ""));
                                } catch (NumberFormatException e) {
                                    System.out.println("Polje nije dobrog tipa! Polje: " +str[i]);
                                    return false;
                                }
                                break;
                        }
                        if (columnSizes.get(i) < str[i].replaceAll("\"", "").length()){
                            System.out.println("Polje je vece nego sto je potrebno! Polje:"+ str[i]);
                            return false;
                        }
                    }
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return true;
    }

    private boolean checkDuplicates(String[] str) {
        final Set<String> setToReturn = new HashSet<String>();
        final Set<String> set1 = new HashSet<String>();

        for (String yourInt : str) {
            if (!set1.add(yourInt)) {
                setToReturn.add(yourInt);
            }
        }
        if (setToReturn.size() > 0){
            System.out.println("duplikat u nazivu kolone");

            return true;
        }
        return false;
    }

    public List<Row> fetchDataFromDatabase(String sql) {
        List<Row> rows = new ArrayList<>();


        try{
            this.initConnection();

            String query = sql;
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet rs = preparedStatement.executeQuery();
            ResultSetMetaData resultSetMetaData = rs.getMetaData();

            populateRowList("export", rows, rs, resultSetMetaData);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            this.closeConnection();
        }

        return rows;
    }

    public boolean daLiPostojiKolona(String tabela,String kolona){

        try {
            this.initConnection();
            ResultSet rs = metaData.getColumns(null,null,tabela,kolona);
            if (rs.next()){
                System.out.println(rs.getString(4));
                if (rs.getString(4).equalsIgnoreCase(kolona)){
                    return true;
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }finally {
            this.closeConnection();
        }
        return false;
    }

    public boolean daLiJeStraniKljuc(String tabela, String kolona){
        try {
            this.initConnection();
           ResultSet rs = metaData.getImportedKeys(null,null,tabela);
           while (rs.next()){
               if (rs.getString(4).equalsIgnoreCase(kolona)){
                   return true;
               }
           }
        }catch (SQLException e){
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }finally {
            this.closeConnection();
        }

        return false;
    }

    public ArrayList<String> getListaTabela(){
        return listaTabela;
    }

    public void setListaTabela(ArrayList<String> listaTabela) {
        this.listaTabela = listaTabela;
    }

    public DatabaseMetaData getMetaData() {
        return metaData;
    }

    public void setMetaData(DatabaseMetaData metaData) {
        this.metaData = metaData;
    }
    public static class UniqueConstraint {
        public String table;
        public String name;
        public List<String> columns = new ArrayList<>();
        public String toString() {
            return String.format("[%s] %s: %s", table, name, columns);
        }
    }

    public List<UniqueConstraint> getUniqueConstraints(String table) throws SQLException {
        Map<String, UniqueConstraint> constraints = new HashMap<>();
        try {
            this.initConnection();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        DatabaseMetaData dm = this.connection.getMetaData();
        ResultSet rs = dm.getIndexInfo(null, null, table, true, true);
        while(rs.next()) {
            String indexName = rs.getString("index_name");
            String columnName = rs.getString("column_name");

            UniqueConstraint constraint = new UniqueConstraint();
            constraint.table = table;
            constraint.name = indexName;
            constraint.columns.add(columnName);

            constraints.compute(indexName, (key, value) -> {
                if (value == null) { return constraint; }
                value.columns.add(columnName);
                return value;
            });
        }

        return new ArrayList<>(constraints.values());
    }
}
