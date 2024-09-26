import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.List;




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
public class APass_II {
    final private List<String> instructions;
    final private Map<Integer,String> symTab;
    final private Map<Integer,String> litTab;




    public APass_II() {
        ReadCode readCode=new ReadCode("IntermediateCode.txt");
        this.instructions =readCode.getInstructions();
        ReadCode readSym = new ReadCode("Symbol Table.txt");
        List<String>readSymInstructions =readSym.getInstructions();
        ReadCode readLit = new ReadCode("Literal Table.txt");
        List<String>readLitInstructions = readLit.getInstructions();
        symTab = new HashMap<>();
        litTab = new HashMap<>();
        int index=1;


        for (String instruction:readSymInstructions){
            String[] symAdd = instruction.split("\\s+");
            symTab.put(index++,symAdd[1]);
        }
        index=1;
        for (String instruct:readLitInstructions){
            String[]litAdd = instruct.split("\\s+");
            litTab.put(index++,litAdd[1]);
        }


    }
    public void Opcode(){
        for (String inst : instructions) {
            String[] instP = inst.split("\\s");


            if (!instP[0].equals("-x-")){
                if (instP[1].startsWith("IS", 1)){
                    StringBuilder tableInd = new StringBuilder(instP[2].substring(7));
                    tableInd.deleteCharAt(tableInd.length()-1);




                    if (instP[2].charAt(5)=='L'){
                        System.out.println("+\t"+instP[1].substring(4,6)+"\t"+instP[2].charAt(1)+"\t"+ litTab.get(Integer.parseInt(tableInd.toString())));
                    }else{
                        System.out.println("+\t"+instP[1].substring(4,6)+"\t"+instP[2].charAt(1)+"\t"+symTab.get(Integer.parseInt(tableInd.toString())));
                    }
                } else if (instP[1].startsWith("DL", 1)) {
                    StringBuilder constantV = new StringBuilder(instP[2].substring(3));


                    constantV.deleteCharAt(constantV.length()-1);
                    while (constantV.length()!=3){
                        constantV.insert(0,"0");
                    }










                    System.out.println("+\t00\t0\t"+constantV);
                }
            }else{
                System.out.println();
            }
        }
    }


}

