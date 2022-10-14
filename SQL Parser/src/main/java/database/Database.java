package database;

import resource.DBNode;
import resource.data.Row;

import java.util.List;

public interface Database {
    DBNode loadResource();

    List<Row> readDataFromTable(String tableName);

    List<Row> fetchDataFromDatabase(String tableName);

    void runCsvCode(List<String[]> sql, String selektovanaTabela);

    boolean checkColumns(String selektovanaTabela, List<String[]> strings);
}
