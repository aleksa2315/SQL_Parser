package controller;

import gui.view.MainFrame;
import queryChecker.QueryCheckerImpl;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class RunQuery extends AbstractAction{
    private List<String> resultSetArray=new ArrayList<>();
    private QueryCheckerImpl queryChecker;
    public RunQuery(){
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(
                KeyEvent.VK_R, ActionEvent.ALT_MASK));
        putValue(SMALL_ICON, loadIcon("ss"));
        putValue(NAME, "Run Query");
        putValue(SHORT_DESCRIPTION, "Run Query");
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        queryChecker = new QueryCheckerImpl();
        if(queryChecker.uradi()== true){
            MainFrame.getInstance().getAppCore().fetchData(MainFrame.getInstance().getTextPane().getText());
        }else{
            System.out.println("postoji greska");
        }
    }

}
