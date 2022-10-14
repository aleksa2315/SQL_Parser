package database;

import resource.DBNode;
import resource.data.Row;

import java.sql.DatabaseMetaData;
import java.util.List;

public interface Repository {
    DBNode getSchema();
    DatabaseMetaData getMetaData();

    List<Row> get(String from);

    List<Row> fetchDataFromDatabase(String sql);

    void runCsvCode(List<String[]> sql, String selektovanaTabela);

    boolean checkColumns(String tableName, List<String[]> csvColumns);
}
