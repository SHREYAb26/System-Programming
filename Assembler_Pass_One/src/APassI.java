import com.sun.source.tree.IfTree;


import javax.swing.text.html.parser.Entity;
import java.io.*;
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




public class APassI {
    final private static Map<String, String> AD=new HashMap<>();;
    final private static Map<String, String> IS = new HashMap<>();;
    final private static Map<String, String> DL = new HashMap<>();;
    final private static Map<String, String> Reg = new HashMap<>();
    final private static Map<String, String> BC = new HashMap<>();
    final private static Map<String, Integer> SymbolTable = new HashMap<>();
    final private static Map<String,Integer> SymbolIndex=new HashMap<>();
    final private static Map<String, Integer> LiteralTable = new HashMap<>();
    final private static Map<String,Integer> LiteralIndex=new HashMap<>();
    private int LC;
    private int SIndex=1;
    private int LIndex =1;




    private final List<String> instructions;
    final private static List<String> AssemblerInst;
    final private static List<String> MustLabel;








    static {
        AD.put("START", "(AD,01)");
        AD.put("END", "(AD,02)");
        AD.put("ORIGIN", "(AD,03)");
        AD.put("EQU", "(AD,04)");
        AD.put("LTORG", "(AD,05)");




        IS.put("STOP", "(IS,00)");
        IS.put("ADD", "(IS,01)");
        IS.put("SUB", "(IS,02)");
        IS.put("MULT", "(IS,03)");
        IS.put("MOVER", "(IS,04)");
        IS.put("MOVEM", "(IS,05)");
        IS.put("COMP", "(IS,06)");
        IS.put("BC", "(IS,07)");
        IS.put("DIV", "(IS,08)");
        IS.put("READ", "(IS,09)");
        IS.put("PRINT", "(IS,10)");




        DL.put("DC", "(DL,01)");
        DL.put("DS", "(DL,02)");




        Reg.put("AREG","(1)");
        Reg.put("BREG","(2)");
        Reg.put("CREG","(3)");
        Reg.put("DREG","(4)");




        BC.put("LT","(1)");
        BC.put("LE","(2)");
        BC.put("EQ","(3)");
        BC.put("GT","(4)");
        BC.put("GE","(5)");
        BC.put("ANY","(6)");




        AssemblerInst=new ArrayList<>(IS.keySet());
        AssemblerInst.addAll(AD.keySet());
        AssemblerInst.remove("EQU");


        MustLabel=new ArrayList<>(DL.keySet());
        MustLabel.add("EQU");








    }




    public APassI() {
        ReadCode readCode = new ReadCode("Source_prog.txt");
        instructions = readCode.getInstructions();
        String[] FirstInstruction = instructions.get(0).split(" ");
        if (FirstInstruction.length>1){
            LC = Integer.parseInt(FirstInstruction[1]);
        }else{
            LC=0;
        }










    }
    public void convert(){
        boolean endEncountered = false;


        if (!instructions.get(0).contains("START")) {
            System.out.println("Error! Invalid syntax: Missing statement 'START'.");
        } else {
            for (String inst : instructions) {


                String[] instP = inst.split("[ ,]+");
                if (AssemblerInst.contains(instP[0])){
                    //instruction for IS,AD(except EQU)


                    if (AD.containsKey(instP[0])){
                        //instruction is from START,END, LTORG,ORIGIN
                        String ad = AD.get(instP[0]);
                        if (ad.equals("(AD,03)")){
                            if (isNumber(instP[1])){
                                LC=Integer.parseInt(instP[1]);
                            } else if (SymbolTable.containsKey(instP[1]) && SymbolTable.get(instP[1])!=null) {
                                LC = SymbolTable.get(instP[1])+Integer.parseInt(instP[3]);
                            }else{
                                System.out.println("ERROR! Cannot find symbol "+instP[1]);
                            }
                        } else if (ad.equals("(AD,02)")||ad.equals("(AD,05)")) {
                            for (Map.Entry<String,Integer>entry:LiteralTable.entrySet()){
                                if (entry.getValue()==null){
                                    entry.setValue(Integer.valueOf(LC++));
                                }
                            }
                            if (ad.equals("(AD,02)")){
                                endEncountered=true;
                            }
                        }
                        System.out.println("-x-\t"+ad);




                    }else{
                        //instruction is from IS
                        if (instP.length == 2){
                            if (instP[0].equals("READ")||instP[0].equals("PRINT")){
                                if (SymbolTable.containsKey(instP[1])){
                                    System.out.println(LC+"\t"+IS.get(instP[0])+ SymbolTable.get(instP[1]));
                                }else{
                                    SymbolTable.put(instP[1],null);
                                    SymbolIndex.put(instP[1], Integer.valueOf(SIndex++));
                                }


                            }else{
                                System.out.println("ERROR! Invalid syntax");
                            }
                            LC++;
                        }else if (instP.length==3){
                            if (IS.containsKey(instP[0])){
                                if (!Reg.containsKey(instP[1]) || Reg.containsKey(instP[2])){
                                    System.out.println("ERROR! Invalid syntax of operands");
                                }else if(instP[0].equals("BC")){
                                    System.out.println(LC +"\t"+IS.get(instP[0])+"\t"+BC.get(instP[1])+", (S,"+SymbolIndex.get(instP[2])+")");
                                }else{
                                    if (SymbolTable.containsKey(instP[2])){
                                        System.out.println(LC +"\t"+IS.get(instP[0])+"\t"+Reg.get(instP[1])+", (S,"+SymbolIndex.get(instP[2])+")");
                                    } else if (LiteralTable.containsKey(instP[2])) {
                                        System.out.println(LC +"\t"+IS.get(instP[0])+"\t"+Reg.get(instP[1])+", (L,"+LiteralIndex.get(instP[2])+")");
                                    }else{
                                        if (isLiteral(instP[2])){
                                            LiteralTable.put(instP[2],null);
                                            LiteralIndex.put(instP[2], Integer.valueOf(LIndex++));
                                            System.out.println(LC +"\t"+IS.get(instP[0])+"\t"+Reg.get(instP[1])+",(L,"+LiteralIndex.get(instP[2])+")");
                                        }else{
                                            SymbolTable.put(instP[2],null);
                                            SymbolIndex.put(instP[2], Integer.valueOf(SIndex++));
                                            System.out.println(LC +"\t"+IS.get(instP[0])+"\t"+Reg.get(instP[1])+",(S,"+SymbolIndex.get(instP[2])+")");
                                        }
                                    }


                                }


                            }else{
                                System.out.println("ERROR! Invalid syntax");
                            }
                            LC++;
                        }else if (instP.length==1){
                            if (instP[0].equals("STOP")){
                                System.out.println(LC+"\t"+IS.get(instP[0]));
                            }else{
                                System.out.println("ERROR! Invalid syntax");
                            }
                            LC++;


                        }else{
                            System.out.println("ERROR! Invalid syntax");
                        }
                    }


                }else{
                    //symbol(or label)
                    if(MustLabel.contains(instP[1])){
                        if (DL.containsKey(instP[1])){
                            //instruction from DL
                            String dl = DL.get(instP[1]);
                            SymbolTable.put(instP[0], Integer.valueOf(LC));
                            SymbolIndex.put(instP[0], Integer.valueOf(SIndex++));
                            System.out.println(LC+"\t"+dl+"(C,"+instP[2]+")");
                            if (dl.equals("DC")) LC++;
                            else LC+=Integer.parseInt(instP[2]);






                        }else{
                            //instruction is EQU
                            if (SymbolTable.containsKey(instP[2])){
                                SymbolTable.put(instP[0],SymbolTable.get(instP[2]));
                                SymbolIndex.put(instP[0], Integer.valueOf(SIndex++));
                                System.out.println("-x-\t"+AD.get(instP[1]));
                            }else{
                                System.out.println("Cannot resolve "+instP[2]);
                            }


                        }
                    }else{
                        //instruction is from IS with label
                        SymbolTable.put(instP[0], Integer.valueOf(LC));
                        SymbolIndex.put(instP[0], Integer.valueOf(SIndex++));
                        if (instP.length == 3){
                            if (instP[1].equals("READ")||instP[1].equals("PRINT")){
                                if (SymbolTable.containsKey(instP[2])){
                                    System.out.println(LC+"\t"+IS.get(instP[1])+ SymbolTable.get(instP[1]));
                                }else{
                                    SymbolTable.put(instP[2],null);
                                    SymbolIndex.put(instP[2], Integer.valueOf(SIndex++));
                                }


                            }else{
                                System.out.println("ERROR! Invalid syntax");
                            }
                            LC++;
                        }else if (instP.length==4){
                            if (IS.containsKey(instP[1])){
                                if (!Reg.containsKey(instP[2]) || Reg.containsKey(instP[3])){
                                    System.out.println("ERROR! Invalid syntax of operands");
                                }else{
                                    if (SymbolTable.containsKey(instP[3])){
                                        System.out.println(LC +"\t"+IS.get(instP[1])+"\t"+Reg.get(instP[2])+", (S,"+SymbolIndex.get(instP[3])+")");
                                    } else if (LiteralTable.containsKey(instP[3])) {
                                        System.out.println(LC +"\t"+IS.get(instP[1])+"\t"+Reg.get(instP[2])+", (L,"+LiteralIndex.get(instP[3])+")");
                                    }else{
                                        if (isLiteral(instP[3])){
                                            LiteralTable.put(instP[3],null);
                                            LiteralIndex.put(instP[3], Integer.valueOf(LIndex++));
                                            System.out.println(LC +"\t"+IS.get(instP[1])+"\t"+Reg.get(instP[2])+",(L,"+LiteralIndex.get(instP[3])+")");
                                        }else{
                                            SymbolTable.put(instP[3],null);
                                            SymbolIndex.put(instP[3], Integer.valueOf(SIndex++));
                                            System.out.println(LC +"\t"+IS.get(instP[1])+"\t"+Reg.get(instP[2])+",(S,"+SymbolIndex.get(instP[3])+")");
                                        }
                                    }


                                }


                            }else{
                                System.out.println("ERROR! Invalid syntax");
                            }
                            LC++;
                        }else{
                            System.out.println("ERROR! Invalid syntax");
                        }


                    }


                }






            }


        }
    }
    private boolean isNumber(String part){
        try {
            Integer.parseInt(part);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }




    }
    private boolean isLiteral(String part){
        if (part.length() > 1 && part.charAt(0) == '=') {
            // Check if the rest of the string consists of digits
            for (int i = 1; i < part.length(); i++) {
                if (!Character.isDigit(part.charAt(i))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }




    public void printSymbolTable(){
        System.out.println("Symbol\tAddress");
        for (Map.Entry<String,Integer>symbol:SymbolTable.entrySet()){
            System.out.println(symbol.getKey()+"\t"+symbol.getValue());
        }
    }
    public void printLiteralTable(){
        System.out.println("Literal\tAddress");
        for (Map.Entry<String,Integer>symbol:LiteralTable.entrySet()){
            System.out.println(symbol.getKey()+"\t"+symbol.getValue());
        }
    }




}

