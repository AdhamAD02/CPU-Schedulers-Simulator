package AG;
import CPU_basics.*;



import java.util.*;

public class AG_algorithm implements CPU_Scheduler {
    private ArrayList<AG_process> processes = new ArrayList<>();
    private Map<AG_process, ArrayList<Interval>> processIntervals = new HashMap<>();

    private Map<AG_process, ArrayList<Integer>> processQuantums = new HashMap<>();
    private ArrayList<Boolean> inQueue = new ArrayList<>() ;
    private ArrayList<AG_process> readyQueue = new ArrayList<>();


    private int time = 0;
    private int index = 0 ;

    private int finished_processes = 0;

    private AG_process currentProcess = null;

    public AG_algorithm(ArrayList<AG_process> p ){
        p.sort(new Comparator<AG_process>() {
            @Override
            public int compare(AG_process o1, AG_process o2) {
                if (o1.arrival == o2.arrival) {
                    return Integer.compare(o1.AG_factor, o2.AG_factor);
                }
                return Integer.compare(o1.arrival, o2.arrival);
            }
        });
        processes = p;
        for (int i = 0; i < p.size(); i++) {
            inQueue.add(false);
            processQuantums.put(p.get(i) , new ArrayList<>());
            processQuantums.get(p.get(i)).add(p.get(i).quantum);
            processIntervals.put(p.get(i) , new ArrayList<>());
        }
    }

    private void moveIndex(){
        while(index < processes.size() && processes.get(index).arrival <= time) index++;
    }

    @Override
    public void execute() {
        if(currentProcess == null){
            addProcesses();
            if(!readyQueue.isEmpty()){
                currentProcess = readyQueue.get(0);
            }
            else{
                time++;
            }
        }
        else{
            while(readyQueue.contains(currentProcess)) {
                readyQueue.remove(currentProcess);
            }
            inQueue.set(processes.indexOf(currentProcess) , false);
            if(!nonPreEmptive(currentProcess)){
                currentProcess = preEmptive(currentProcess);
            }
        }
    }

    @Override
    public void run() {
        while (finished_processes != processes.size()) {
            execute();
        }
        float avg_wait = 0, avg_turn_around = 0;
        for (int i = 0; i < processes.size(); i++) {
            avg_wait += processes.get(i).wait_time;
            avg_turn_around += processes.get(i).turn_around;
        }
        avg_wait /= processes.size();
        avg_turn_around /= processes.size();
        printGanttChart();
        print(avg_wait, avg_turn_around);
        printHistory();
    }


    private AG_process checkProcesses(AG_process p){
        AG_process cur = p;
        for(AG_process c : readyQueue){
            if(c.AG_factor < cur.AG_factor){
                cur = c;
            }
        }
        return cur == p ? null : cur;
    }

    private void addProcesses(){
        moveIndex();
        for (int i = 0; i < index; i++) {
            AG_process cur = processes.get(i);
            if(!inQueue.get(i) && !cur.flag && cur != currentProcess){
                inQueue.set(i,true);
                readyQueue.add(cur);
            }
        }
    }

    private boolean nonPreEmptive(AG_process p){
        int halfQuantum = (int)Math.ceil(p.quantum/2.0) , start = time , constant = time;
        for (; start < halfQuantum + constant ;) {
            p.burst_time--;
            time++;
            start++;
            if(p.burst_time ==0){
                update(p , constant , start);
                return true;
            }
        }
        Interval newInterval = new Interval(constant , start);
        processIntervals.get(p).add(newInterval);
        return false;
    }
    private AG_process preEmptive(AG_process p){
        int halfQuantum = p.quantum - (int)Math.ceil(p.quantum/2.0) , start = time , constant = time;
        for (; start < constant + halfQuantum ; ) {
            addProcesses();
            AG_process next = checkProcesses(p);
            if(next == null){
                p.burst_time--;
                time++;
                start++;
                if(p.burst_time ==0){
                    update(p , constant , start);
                    return null;
                }
            }
            else{
                p.quantum += p.quantum - ((int)Math.ceil(p.quantum/2.0) + (start - constant ));
                processQuantums.get(p).add(p.quantum);
                Interval newInterval = new Interval(constant , start);
                processIntervals.get(p).add(newInterval);
                readyQueue.add(p);
                inQueue.set(processes.indexOf(p) , true);
                return next;
            }
        }

        float mean = p.quantum;
        int cnt = 1;
        for(int i = 0; i < processes.size(); i++) {
            if(inQueue.get(i)) {
                mean += processes.get(i).quantum;
                cnt++;
            }
        }
        mean /= cnt;

        p.quantum += Math.ceil(0.1 * mean);
        processQuantums.get(p).add(p.quantum);
        Interval newInterval = new Interval(constant , start);
        processIntervals.get(p).add(newInterval);
        readyQueue.add(p);
        inQueue.set(processes.indexOf(p) , true);
        return null;
    }
    private void update(AG_process p , int start , int end){

        p.flag = true;
        p.complete_time = time ;
        p.turn_around = p.complete_time   - p.arrival;
        p.wait_time = p.turn_around - p.burst_time_original;
        p.quantum = 0;
        finished_processes++;

        processQuantums.get(p).add(p.quantum);
        Interval newInterval = new Interval(start, end);
        processIntervals.get(p).add(newInterval);
        while(readyQueue.contains(p)) {
            readyQueue.remove(p);
        }
        inQueue.set(processes.indexOf(p) , false);
        currentProcess = null;
    }

    @Override
    public void printGanttChart() {
        int maxCompletionTime = time; // Maximum timeline
        processes.sort(new Comparator<AG_process>() {
            @Override
            public int compare(AG_process o1, AG_process o2) {
                return Integer.compare(o1.response, o2.response);
            }
        });

        // Time Axis
        System.out.print("Time:  ");
        for (int i = 0; i <= maxCompletionTime; i++) {
            System.out.printf("%-3d", i);
        }
        System.out.println();

        for (AG_process process : processes) {
            System.out.print(process.name + ":    ");
            int lastEnd = 0;
            for (Interval interval : processIntervals.get(process)) {

                for (int i = lastEnd; i < interval.start; i++) {
                    System.out.print("   ");
                }
                for (int i = interval.start; i < interval.end; i++) {
                    System.out.print("---");
                }
                lastEnd = interval.end;
            }
            System.out.println("|"); // End of the process
        }
    }

    private void printHistory(){
        System.out.println("\nProcess Name ==> quantum history");
        for (AG_process process : processes) {
            System.out.print(process.name + " ==>  [");
            for (int i = 0; i < processQuantums.get(process).size() ; i++) {
                int quantum = processQuantums.get(process).get(i);
                System.out.print(quantum );
                if(i != processQuantums.get(process).size() - 1){
                    System.out.print(", ");
                }
            }
            System.out.print("]");
            System.out.println();
        }
    }

    @Override
    public void print(float avg_wait, float avg_turn_around) {

        System.out.printf("%-15s %-15s %-20s %-15s%n", "Name","Wait Time", "Turn Around Time", "AG Factor");
        for (AG_process process : processes) {
            System.out.printf("%-15s %-15d %-20d %-15d%n", process.name, process.wait_time, process.turn_around , process.AG_factor);
        }

        System.out.printf("%nAverage waiting time ==> %.2f%n", avg_wait);
        System.out.printf("Average turn around time ==> %.2f%n", avg_turn_around);
    }

}
