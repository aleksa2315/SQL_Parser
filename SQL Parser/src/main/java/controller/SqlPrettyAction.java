package controller;

import com.github.vertical_blank.sqlformatter.SqlFormatter;
import com.github.vertical_blank.sqlformatter.core.FormatConfig;
import gui.view.MainFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;

public class SqlPrettyAction extends AbstractAction{
    private final ArrayList<String> keyWords = new ArrayList<String>(
            Arrays.asList("ADD", "ADD CONSTRAINT", "ALL", "ALTER", "ALTER COLUMN", "ALTER TABLE", "AND", "ANY", "AS", "ASC", "BACKUP DATABASE", "BETWEEN", "CASE", "CHECK", "COLUMN", "CONSTRAINT", "CREATE", "CREATE DATABASE", "CREATE INDEX", "CREATE OR REPLACE VIEW", "CREATE TABLE", "CREATE PROCEDURE", "CREATE UNIQUE INDEX", "CREATE VIEW", "DATABASE", "DEFAULT", "A", "DELETE", "DESC", "DISTINCT", "DROP", "DROP COLUMN", "DROP CONSTRAINT", "DROP DATABASE", "DROP DEFAULT", "DROP INDEX", "DROP TABLE", "DROP VIEW", "EXEC", "EXISTS", "FOREIGN KEY", "A", "FROM", "FULL OUTER JOIN", "GROUP BY", "HAVING", "IN", "INDEX", "INNER JOIN", "INSERT INTO", "INSERT INTO SELECT", "IS NULL", "IS NOT NULL", "JOIN", "LEFT JOIN", "LIKE", "LIMIT", "NOT", "NOT NULL", "OR", "ORDER BY", "OUTER JOIN", "PRIMARY KEY", "PROCEDURE", "RIGHT JOIN", "ROWNUM", "SELECT", "SELECT DISTINCT", "SELECT INTO", "SELECT TOP", "SET", "TABLE", "TOP", "TRUNCATE TABLE", "UNION", "UNION ALL", "UNIQUE", "UPDATE", "VALUES", "VIEW", "WHERE"));

    public SqlPrettyAction() {
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(
                KeyEvent.VK_P, ActionEvent.ALT_MASK));
        putValue(SMALL_ICON, loadIcon("ss"));
        putValue(NAME, "Sql Pretty");
        putValue(SHORT_DESCRIPTION, "Sql Pretty");
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        String text = MainFrame.getInstance().getTextPane().getText().toLowerCase();
//        for (String s:
//             keyWords) {
//            String str = s.toLowerCase();
//            System.out.println(text.contains(str));
//            if (text.contains(str)){
//                System.out.println(text.indexOf(str));
//                if (text.indexOf(str) != 0){
//                String before = text.substring(0,text.indexOf(str));
//                String after = text.substring(text.indexOf(str),text.length());
//                text = before + "\n" + after;
//                }
//            }
//        }

        String str = SqlFormatter.format(text,
                FormatConfig.builder()
                        .indent("") // Defaults to two spaces
                        .uppercase(true) // Defaults to false (not safe to use when SQL dialect has case-sensitive identifiers)
                        .linesBetweenQueries(2) // Defaults to 1
                        .maxColumnLength(100) // Defaults to 50
                        .params(Arrays.asList("a", "b", "c")) // Map or List. See Placeholders replacement.
                        .build()
        );

        MainFrame.getInstance().setTextPane(str);
    }

}

