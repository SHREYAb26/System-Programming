import java.io.BufferedReader;
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
public class MacroPassII {
    private final List<String>instructions;
    private Map<String,int[]> MNT;
    private Map<String,Map<Integer,String>>APT;
    private Map<Integer,String>KPIndex;
    private Map<String,String>KPT;
    private Map<Integer,String>MDT;
    public MacroPassII() {
        ReadCode readCode = new ReadCode("Intermediate_code.txt");
        this.instructions = readCode.getInstructions();


        MNT = new HashMap<>();
        ReadCode  mnt = new ReadCode("MNT.txt");
        List<String>mntdef=mnt.getInstructions();
        for (String s:mntdef){
            String[]newS = s.split(":");


            String []res = newS[1].split(",");
            res[0]=res[0].substring(1);
            res[3]=res[3].substring(0,res[3].length()-1);


            int[] nRes = new int[4];
            int index=0;
            for (String re : res) {
                nRes[index++] = Integer.parseInt(re.trim());
            }
            MNT.put(newS[0].trim(),nRes);




        }


        KPT=new HashMap<>();
        KPIndex = new HashMap<>();
        ReadCode kpdt = new ReadCode("KPDT.txt");
        List<String>kpdef = kpdt.getInstructions();
        for (String s: kpdef){
            String[]newS = s.split("\t");


            String[]res = newS[1].split(":");
            res[0]=res[0].substring(1);
            res[1]=res[1].substring(0, res[1].length()-1);
            KPT.put(res[0],res[1]);
            KPIndex.put(Integer.parseInt(newS[0]),res[0]);
        }


        MDT=new HashMap<>();
        ReadCode mdt = new ReadCode("MDT.txt");
        List<String>mdtdef = mdt.getInstructions();
        for (String s:mdtdef){
            String[]newS = s.split(":");
            MDT.put(Integer.parseInt(newS[0]),newS[1]);
        }


        APT=new HashMap<>();


    }


    public void convert(){
        for (String inst:instructions){
            String[]instSp = inst.split(" ",2);


            if (MNT.containsKey(instSp[0])){
                Map<Integer,String>PTab=new HashMap<>();
                int index=1;
                int[]mntpointer = MNT.get(instSp[0]);
                int pos = mntpointer[0];
                int key= mntpointer[1];
                int mdtp=mntpointer[2];
                int kpdtp=mntpointer[3];
                String []para = instSp[1].split(",");
                for(String p:para){
                    if (!p.startsWith("&")){
                        PTab.put(index++,p);


                    }
                }
                List<String>keypara=new ArrayList<>();
                for (String p:para){
                    if (p.startsWith("&")){
                        PTab.put(index++,p.substring(1,p.indexOf("=")));
                        keypara.add(p.substring(1,p.indexOf("=")));


                    }
                }




                while(index<=(pos+key)){
                    String parameter=KPIndex.get(kpdtp++);


                    if (!keypara.contains(parameter)){
                        PTab.put(index++,KPT.get(parameter));


                    }
                }
                APT.put(instSp[0],PTab);




                String DefInst = MDT.get(mdtp++);
                while(!DefInst.equals("MEND")) {


                    System.out.print("+");
                    processMDT(DefInst, instSp[0]);
                    System.out.println();


                    DefInst = MDT.get(mdtp++);
                }


            }else{
                System.out.println(inst);
            }
        }
    }
    public void processMDT(String s,String macroname){
        int i=0;
        while(i<s.length()){
            if (s.charAt(i)=='('){
                String val = s.substring(i+3,i+4);
                String value = APT.get(macroname).get(Integer.parseInt(val));
                System.out.print(value);
                i+=5;
            }else {
                System.out.print(s.charAt(i));
                i++;
            }
        }








    }
}

