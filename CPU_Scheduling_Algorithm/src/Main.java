import java.util.*;
public class Main {
    public static void main(String[] args) {
        SchedulingAlgo s1 = new SchedulingAlgo();
        Scanner print = new Scanner(System.in);
        Map<String, ArrayList<Integer>> mymap = new HashMap<>();
        while (true){
            while (true){
                System.out.print ("Enter PID: ");
                String pid=print.next();
                if (pid.equals("-1")){break;}
                System.out.print("Enter AT:");
                int AT=print.nextInt();
                System.out.print("Enter BT:");
                int BT=print.nextInt();
                mymap.put(pid,new ArrayList<>(Arrays.asList(AT,BT)));
            }




            s1.FCFS(mymap);
            s1.SJF(mymap);
            s1.RoundRobin(mymap);


        }
    }


}
