package queryChecker;

import com.github.vertical_blank.sqlformatter.SqlFormatter;
import com.github.vertical_blank.sqlformatter.core.FormatConfig;
import database.Database;
import database.DatabaseImpl;
import database.MYSQLRepository;
import gui.view.MainFrame;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;


import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QueryCheckerImpl implements QueryChecker{
    private DatabaseImpl database = (DatabaseImpl) MainFrame.getInstance().getAppCore().getDatabase();
    private DatabaseMetaData metaData = ((MYSQLRepository)database.getRepository()).getMetaData();
    private MYSQLRepository repository = (MYSQLRepository) database.getRepository();
    private String proba = "select first_name,last_name as 'initials dsdsd' from employees";
    private String queryText = MainFrame.getInstance().getTextPane().getText();
    private String[] statements = null;

    private ArrayList<String> listaReci = new ArrayList<>();
    private JSONObject jsonObject = new JSONObject();
    private ArrayList lista = new ArrayList();
    @Override
    public boolean colAndTableExsist() {
        ArrayList tabeleUBazi = new ArrayList();
        String[] trenutnaListaReci = null;
            for (int i = 0; i < statements.length; i++) {
                String trenutniStatement = statements[i];

                deliNaReci(trenutniStatement);

                trenutnaListaReci = listaReci.get(i).trim().split("\\s+");
                //Proveravanje za kolone
                String[] zeljeneKolone = trenutnaListaReci[1].split(",");
                for (int j = 0; j < zeljeneKolone.length; j++) {
                    System.out.println( zeljeneKolone[j]);
                    String izmenjen = zeljeneKolone[j];
                    if(zeljeneKolone[j].contains("(")){
                        izmenjen = izmenjen.substring(izmenjen.indexOf("("),izmenjen.indexOf(")"));
                    }
                    boolean postoji = repository.daLiPostojiKolona(trenutnaListaReci[3].substring(0,trenutnaListaReci[3].length()-1), izmenjen);

                    if (!postoji) {
                        System.out.println("NE POSTOJI KOLONA");
                        jsonObject.put("Kolona ne postoji","Kolona " +izmenjen + " ne postoji u tabeli " + trenutnaListaReci[3].substring(0,trenutnaListaReci[3].length()-1));
                        return false;
                    }
                }

                //Proveravanje za tebele

                if (!repository.getListaTabela().contains(trenutnaListaReci[3].substring(0,trenutnaListaReci[3].length()-1))) {
                    System.out.println("NE POSTOJI TABELA");
                    jsonObject.put("Tabela ne postoji","Tabela " + trenutnaListaReci[3].substring(0,trenutnaListaReci[3].length()-1) + " ne postoji u bazi " + repository.getSchema().getName());
                    return false;
                }

            }
        listaReci = null;
            return true;
    }

    @Override
    public boolean isJoinForeignKey() {
        for (int i = 0 ; i < statements.length ; i++){
            String[] trenutnaListaReci = null;
            String trenutniStatement = statements[i];
            deliNaReci(trenutniStatement);

            trenutnaListaReci = listaReci.get(i).trim().split("\\s+");


            String zagrada = null;
            Matcher m = Pattern.compile("\\((.*?)\\)").matcher(trenutniStatement);
            while (m.find()){
                zagrada = m.group(1);
            }


            if (zagrada.contains("=")) {
                String[] reciZagrade = zagrada.split("=");
                String straniKljuc = reciZagrade[1].substring(1);
                boolean postoji = repository.daLiJeStraniKljuc(trenutnaListaReci[3].substring(0,trenutnaListaReci[3].length()-1), straniKljuc);

                System.out.println(trenutnaListaReci[3].substring(0,trenutnaListaReci[3].length()-1));
                if (postoji) {
                    System.out.println("POSTOJI STRANI KLJUC");
                    return true;
                } else {
                    System.out.println("NE POSTOJI STRANI KLJUC");
                    jsonObject.put("Strani kljuc ne postoji","Strani kljuc " + straniKljuc + " u tabeli " +trenutnaListaReci[3].substring(0,trenutnaListaReci[3].length()-1));
                    return false;
                }
                //select employee_id, department_name from employees join departments using (department_id)
            }else {
                String straniKljuc = zagrada;
                boolean postoji = repository.daLiJeStraniKljuc(trenutnaListaReci[3].substring(0,trenutnaListaReci[3].length()-1), straniKljuc);

                if (postoji) {
                    System.out.println("POSTOJI STRANI KLJUC");
                    return true;
                } else {
                    System.out.println("NE POSTOJI STRANI KLJUC");
                    jsonObject.put("Strani kljuc ne postoji","Strani kljuc " + straniKljuc + " u tabeli " +trenutnaListaReci[3].substring(0,trenutnaListaReci[3].length()-1));
                    return false;
                }
            }
        }
        listaReci = null;
        return true;
    }

    @Override
    public boolean mandatoryParts() {
        for (int i = 0 ; i < statements.length; i++){
            String trenutniStatement = statements[i];


        }
        return true;
    }

    @Override
    public boolean isValidOrder() {
        return true;
    }

    @Override
    public boolean aliasCheck() {

        for (int i = 0 ; i < statements.length; i++) {
            String[] trenutnaListaReci = null;
            String[] listareciTmp = null;
            String trenutniStatement = statements[i];
            deliNaReci(trenutniStatement);

//            System.out.println(Arrays.toString((String[])lista.get(0)));
            listareciTmp = (String[]) lista.get(i);
            System.out.println(Arrays.toString(listareciTmp));

            int indexAs = 0;
            for (int j = 0; j < listareciTmp.length; j++) {
                if (listareciTmp[j].equalsIgnoreCase("as")) {
                    indexAs = j;
                }
            }

            System.out.println(listareciTmp[indexAs]);
            int indexFrom = 0;
            for (int k = 0; k < listareciTmp.length; k++) {
                if (listareciTmp[k].equalsIgnoreCase("from")) {
                    indexFrom = k;
                }
            }

            if (listareciTmp[indexAs + 1].startsWith("'") && listareciTmp[indexFrom - 1].endsWith("'")) {
                System.out.println("ALIAS OK");
                return true;
            } else {
                System.out.println("ALIAS NOT OK");
                jsonObject.put("Alias nije u redu", "Na pocetku reci " + listareciTmp[indexAs + 1] + " fali '" + " i na kraju reci " + listareciTmp[indexFrom - 1] + " fali '");

                return false;
//            int counter = indexFrom-indexAs+1;
//            StringBuilder sb = new StringBuilder();
//            sb.append("'");
//            int trenutniIndex = indexAs+1;
//            while (counter > 2){
//                sb.append(listareciTmp[trenutniIndex++] + " ");
//                counter--;
//            }
//              sb.append("'");
//
            }
        }
            return true;
    }

    public void deliNaStatements(){
       statements = queryText.split(";");
     //   statements = proba.split(";");
    }

    /////select first_name,last_name as initials from employees

    public ArrayList deliNaReci(String statement){
        listaReci = new ArrayList<>();
        for (int i = 0; i < statements.length; i++){
            String[] reci = null;
            reci = statements[i].trim().split("\\s+");
            listaReci.add(Arrays.toString(reci));
            lista.add(reci);
        }


        return listaReci;
    }

    public boolean uradi() {
        boolean greska = true;
        deliNaStatements();
        greska = colAndTableExsist();
        if (greska) {
        System.out.println(greska);
            for (String value : statements) {
                if (value.contains("join")) {
                    greska = isJoinForeignKey();

                    break;
                }
            }
        if (greska) {

            greska = mandatoryParts();

                for (String s : statements) {
                    String statement = asProvera(s);
                    if (statement.contains("AS")) {
                        System.out.println("USO SAM");
                        greska = aliasCheck();
                        break;
                    }
                }
            }
        }

        if (!greska){
            try {
                Files.write(Paths.get("output.json"), jsonObject.toJSONString().getBytes(StandardCharsets.UTF_8));
            }catch (IOException e){
                e.printStackTrace();
            }
            System.out.println("Napravljen" + jsonObject);
            return false;
        }
        return true;
    }
    public void pisiUJSON(){

    }

    private String asProvera(String string){
        String str = SqlFormatter.format(string,
                FormatConfig.builder()
                        .indent("") // Defaults to two spaces
                        .uppercase(true) // Defaults to false (not safe to use when SQL dialect has case-sensitive identifiers)
                        .linesBetweenQueries(2) // Defaults to 1
                        .maxColumnLength(100) // Defaults to 50
                        .params(Arrays.asList("a", "b", "c")) // Map or List. See Placeholders replacement.
                        .build()
        );
        return str;

    }
}
