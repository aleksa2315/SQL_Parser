package controller;

import gui.view.MainFrame;
import tree.TreeItem;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.*;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static javax.swing.JOptionPane.showMessageDialog;


public class BulkImportAction extends AbstractAction{
    public BulkImportAction() {
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(
                KeyEvent.VK_I, ActionEvent.ALT_MASK));
        putValue(SMALL_ICON, loadIcon("ss"));
        putValue(NAME, "Import");
        putValue(SHORT_DESCRIPTION, "Import");
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (!MainFrame.getInstance().getjTree().isSelectionEmpty()) {
            JFileChooser jFileChooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV FIles", "csv");
            jFileChooser.setFileFilter(filter);
            jFileChooser.showOpenDialog(MainFrame.getInstance());
            File selektovan = jFileChooser.getSelectedFile();
            String putanja = selektovan.getPath();
            String selektovanaTabela = ((TreeItem<?>) MainFrame.getInstance().getjTree().getLastSelectedPathComponent()).getName();
            String linija = null;
            try {
                BufferedReader reader = new BufferedReader(new FileReader(putanja));

                List<String[]> data = new ArrayList<>();

                while ((linija = reader.readLine()) != null) {
                    String[] temp = (linija.replaceAll("\\s+", "").split(","));
                    data.add(temp);
                }
                List<String[]> correctData = new ArrayList<>();
                correctData = checkCsv(selektovanaTabela,data);
                if (correctData == null )
                    showMessageDialog(null, "Postoje greske u fajlu!");
                else{
                    MainFrame.getInstance().getAppCore().getDatabase().runCsvCode(data, selektovanaTabela);
                    MainFrame.getInstance().getAppCore().fetchData("SELECT * FROM "+selektovanaTabela);
                }
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }else {
            showMessageDialog(null, "Izaberite iz tree-a tabelu!");
        }
    }

    private List<String[]> checkCsv(String selektovanaTabela, List<String[]> data) {
        boolean columns = MainFrame.getInstance().getAppCore().getDatabase().checkColumns(selektovanaTabela, data);
        System.out.println(columns);
        if (columns){
            return data;
        }
        return null;
    }

    private String zagrade(String string){
        return string = string.substring(1,string.length()-1);
    }
}
