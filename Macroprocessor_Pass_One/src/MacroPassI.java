
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;


class ReadCode {
    private final List<String> instructions;




    public ReadCode(String filename) {
        instructions = new ArrayList<>();
        readFile(filename);
    }




    private void readFile(String filename) {
        try (BufferedReader bf = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = bf.readLine()) != null) {
                instructions.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    public List<String> getInstructions() {
        return instructions;
    }
}
public class MacroPassI {
    private Map<String,int[]>MNT;
    private Map<String,Map<Integer,String>>PNT;
    private Map<Integer,String>KPIndex;
    private Map<String,String>KPT;
    private Map<Integer,String>MDT;
    private final List<String>instructions;
    private int mdtp;
    private int kptp;
    private String curr;


    public MacroPassI() {
        MNT=new HashMap<>();
        PNT = new HashMap<>();
        KPIndex =new HashMap<>();
        KPT = new HashMap<>();
        MDT = new HashMap<>();
        ReadCode readCode=new ReadCode("Prog.txt");
        instructions=readCode.getInstructions();
        mdtp=1;
        kptp=101;






    }
    public void convert() {
        boolean flag = false;
        for (int i = 0; i < instructions.size(); i++) {
            if (instructions.get(i).equals("MACRO")) {
                String[] macrodef = instructions.get(i + 1).split(",\\s*|(?<=\\s)(?=&)");
                int pos = 0, keyword = 0;
                int index = 1;
                Map<Integer, String> PNTab = new HashMap<>();
                int ogkptp = kptp;
                for (int j = 1; j < macrodef.length; j++) {
                    if (!macrodef[j].contains("=")) {
                        // Remove the ampersand and add to PNTab
                        String parameter = macrodef[j].substring(1); // Remove the '&'
                        PNTab.put(index++, parameter);
                        pos++;
                    } else {
                        String para = macrodef[j].substring(1, macrodef[j].indexOf("=")); // Remove the '&'
                        PNTab.put(index++, para);
                        if (macrodef[j].endsWith("=")) {
                            KPT.put(para, null);
                            KPIndex.put(kptp++, para);
                        } else {
                            KPT.put(para, macrodef[j].substring(macrodef[j].indexOf("=") + 1));
                            KPIndex.put(kptp++, para);
                        }
                        keyword++;
                    }
                }
                curr = macrodef[0];
                MNT.put(macrodef[0], new int[]{pos, keyword, mdtp, ogkptp});
                PNT.put(macrodef[0], PNTab);


                i++;
                flag = true;
            } else if (instructions.get(i).equals("MEND")) {
                MDT.put(mdtp++, instructions.get(i));
                flag = false;
            } else {
                if (flag) {
                    String[] inst = instructions.get(i).split("[ ,]");
                    StringBuilder sb = new StringBuilder();
                    Map<Integer, String> PNTab = PNT.get(curr);
                    for (String I : inst) {
                        // Remove the ampersand before processing
                        if (I.startsWith("&")) {
                            I = I.substring(1); // Remove the '&'
                        }
                        // Check if the current token is a parameter
                        boolean found = false;
                        for (Map.Entry<Integer, String> entry : PNTab.entrySet()) {
                            if (entry.getValue().equals(I)) {
                                sb.append("(P,").append(entry.getKey()).append(") ");
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            sb.append(I).append(" ");
                        }
                    }
                    MDT.put(mdtp++, sb.toString().trim());
                } else {
                    if (!instructions.get(i).startsWith("&")) {
                        System.out.println(instructions.get(i));
                    }
                }
            }
        }
    }


    public void printMNT(){
        System.out.println();
        System.out.println("Macro Name Table");
        for (Map.Entry<String,int[]>entry:MNT.entrySet()){
            System.out.println(entry.getKey()+":"+ Arrays.toString(entry.getValue()));
        }
    }
    public void printPNT(){
        System.out.println();
        System.out.println("Parameter Name Table");
        for (Map.Entry<String,Map<Integer,String>>entry:PNT.entrySet()){
            System.out.println(entry.getKey()+":"+entry.getValue());
        }
    }
    public void printKPT(){
        System.out.println();
        System.out.println("Keyword parameter default table");
        for (Map.Entry<Integer,String>entry:KPIndex.entrySet()){
            System.out.println(entry.getKey()+"\t"+"["+entry.getValue()+":"+KPT.get(entry.getValue())+"]");
        }
    }
    public void printMDT(){
        System.out.println();
        System.out.println("Macro Definition Table");
        for (Map.Entry<Integer,String>entry:MDT.entrySet()){
            System.out.println(entry.getKey()+":"+entry.getValue());
        }
    }
}

