package database;

import resource.DBNode;
import resource.data.Row;

import java.util.List;

public class DatabaseImpl implements Database{

    private Repository repository;

    public DatabaseImpl(Repository repository) {
        this.repository = repository;
    }

    @Override
    public DBNode loadResource() {
        return repository.getSchema();
    }

    @Override
    public List<Row> readDataFromTable(String tableName) {
        return repository.get(tableName);
    }

    @Override
    public List<Row> fetchDataFromDatabase(String sql) {
        return repository.fetchDataFromDatabase(sql);
    }

    public void runCsvCode(List<String[]> sql, String selektovanaTabela){ this.repository.runCsvCode(sql,selektovanaTabela);}

    public Repository getRepository() {
        return repository;
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }
    @Override
    public boolean checkColumns(String tableName, List<String[]> csvColumns){ return repository.checkColumns(tableName,csvColumns); }
}
