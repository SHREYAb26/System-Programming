import java.util.*;


public class SchedulingAlgo {


    public void FCFS(Map<String, ArrayList<Integer>> NewMap) {
        List<Map.Entry<String,ArrayList<Integer>>> list = new ArrayList<>(NewMap.entrySet());


        list.sort(Comparator.comparing(e -> e.getValue().get(0)));


        Map<String,ArrayList<Integer>> sortedmap=new LinkedHashMap<>();


        for (Map.Entry<String,ArrayList<Integer>>entry:list){
            sortedmap.put(entry.getKey(),entry.getValue());
        }


        int time=0,AT,BT,CT,TAT,WT;
        float avgTAT=0,avgWT=0;
        System.out.println("PID\t\tAT\t\tBT\t\tCT\t\tTAT\t\tWT");
        for (Map.Entry<String,ArrayList<Integer>>entry:sortedmap.entrySet()){
            ArrayList<Integer>newlist=entry.getValue();
            AT=entry.getValue().get(0);
            BT=entry.getValue().get(1);
            if(time<AT)time=AT;


            time+=BT;
            CT=time;
            TAT=CT-AT;
            WT=TAT-BT;
            avgTAT+=TAT;
            avgWT+=WT;


            System.out.println(entry.getKey()+"\t\t"+AT+"\t\t"+BT+"\t\t"+CT+"\t\t"+TAT+"\t\t"+WT);


            newlist.add(CT);
            newlist.add(TAT);
            newlist.add(WT);
            sortedmap.put(entry.getKey(), newlist);


        }
        avgWT/=sortedmap.size();
        avgTAT/=sortedmap.size();


        System.out.println("Avg TAT: "+avgTAT);
        System.out.println("Avg WT: "+avgWT);






    }
    public void SJF(Map<String,ArrayList<Integer>> map) {
        int time = 0;
        float avgTAT = 0, avgWT = 0;
        int completed = 0;
        int n = map.size();
        int[] remainingBT = new int[n];
        boolean[] isCompleted = new boolean[n];
        Map<String, Integer> indexMap = new HashMap<>();


        List<Map.Entry<String, ArrayList<Integer>>> processList = new ArrayList<>(map.entrySet());
        for (int i = 0; i < processList.size(); i++) {
            indexMap.put(processList.get(i).getKey(), i);
            remainingBT[i] = processList.get(i).getValue().get(1);
        }


        System.out.println("PID\t\tAT\t\tBT\t\tCT\t\tTAT\t\tWT");


        while (completed != n) {
            int shortest = -1;
            int minRemainingTime = Integer.MAX_VALUE;


            for (int i = 0; i < n; i++) {
                int AT = processList.get(i).getValue().get(0);
                if (AT <= time && !isCompleted[i] && remainingBT[i] < minRemainingTime) {
                    minRemainingTime = remainingBT[i];
                    shortest = i;
                }
            }


            if (shortest == -1) {
                time++;
                continue;
            }


            remainingBT[shortest]--;
            time++;


            if (remainingBT[shortest] == 0) {
                isCompleted[shortest] = true;
                completed++;


                int AT = processList.get(shortest).getValue().get(0);
                int BT = processList.get(shortest).getValue().get(1);
                int CT = time;
                int TAT = CT - AT;
                int WT = TAT - BT;
                avgTAT += TAT;
                avgWT += WT;


                System.out.println(processList.get(shortest).getKey() + "\t\t" + AT + "\t\t" + BT + "\t\t" + CT + "\t\t" + TAT + "\t\t" + WT);


                ArrayList<Integer> updatedList = map.get(processList.get(shortest).getKey());
                updatedList.add(CT);
                updatedList.add(TAT);
                updatedList.add(WT);
            }
        }


        avgWT /= n;
        avgTAT /= n;


        System.out.println("Avg TAT: " + avgTAT);
        System.out.println("Avg WT: " + avgWT);
    }
    public void RoundRobin(Map<String,ArrayList<Integer>>map){
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter time quantum: ");
        int timeQuantum = scanner.nextInt();


        int time = 0;
        float avgTAT = 0, avgWT = 0;
        int n = map.size();
        int[] remainingBT = new int[n];
        int[] CT = new int[n];
        boolean[] isCompleted = new boolean[n];
        Map<String, Integer> indexMap = new HashMap<>();
        Queue<Integer> queue = new LinkedList<>();


        List<Map.Entry<String, ArrayList<Integer>>> processList = new ArrayList<>(map.entrySet());
        for (int i = 0; i < processList.size(); i++) {
            indexMap.put(processList.get(i).getKey(), i);
            remainingBT[i] = processList.get(i).getValue().get(1);
        }


        System.out.println("PID\t\tAT\t\tBT\t\tCT\t\tTAT\t\tWT");


        int completed = 0;
        while (completed < n) {
            for (int i = 0; i < n; i++) {
                int AT = processList.get(i).getValue().get(0);
                if (AT <= time && !isCompleted[i] && !queue.contains(i)) {
                    queue.add(i);
                }
            }


            if (queue.isEmpty()) {
                time++;
                continue;
            }


            int current = queue.poll();
            if (remainingBT[current] <= timeQuantum) {
                time += remainingBT[current];
                remainingBT[current] = 0;
                isCompleted[current] = true;
                CT[current] = time;
                completed++;
            } else {
                time += timeQuantum;
                remainingBT[current] -= timeQuantum;
                for (int i = 0; i < n; i++) {
                    int AT = processList.get(i).getValue().get(0);
                    if (AT <= time && !isCompleted[i] && !queue.contains(i) && i != current) {
                        queue.add(i);
                    }
                }
                queue.add(current);
            }
        }


        for (int i = 0; i < n; i++) {
            int AT = processList.get(i).getValue().get(0);
            int BT = processList.get(i).getValue().get(1);
            int TAT = CT[i] - AT;
            int WT = TAT - BT;
            avgTAT += TAT;
            avgWT += WT;


            System.out.println(processList.get(i).getKey() + "\t\t" + AT + "\t\t" + BT + "\t\t" + CT[i] + "\t\t" + TAT + "\t\t" + WT);


            ArrayList<Integer> updatedList = map.get(processList.get(i).getKey());
            updatedList.add(CT[i]);
            updatedList.add(TAT);
            updatedList.add(WT);
        }


        avgWT /= n;
        avgTAT /= n;


        System.out.println("Avg TAT: " + avgTAT);
        System.out.println("Avg WT: " + avgWT);

    }




}
