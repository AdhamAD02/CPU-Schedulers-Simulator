package SRTF;
import CPU_basics.CPU_Scheduler;
import CPU_basics.Interval;
import PS.PS_process;


import java.util.*;

public class SRTF_algorithm implements CPU_Scheduler {
    private ArrayList<SRTF_process> processes = new ArrayList<>();
    Map<SRTF_process, ArrayList<Interval>> processIntervals = new HashMap<>();
    private ArrayList<Integer> aging_time ;

    private int time = 0;
    private int index = 0;
    private int finished_processes = 0;

    SRTF_algorithm(ArrayList<SRTF_process> p){
        aging_time = new ArrayList<>();
        for (int i = 0; i < p.size(); i++) {
            aging_time.add(0);
            processIntervals.put(p.get(i) , new ArrayList<>());
        }
        p.sort(new Comparator<SRTF_process>() {
            @Override
            public int compare(SRTF_process o1, SRTF_process o2) {
                if (o1.arrival == o2.arrival) {
                    return Integer.compare(o1.burst_time, o2.burst_time);
                }
                return Integer.compare(o1.arrival, o2.arrival);
            }
        });
        processes = p;
    }
    private void moveIndex(){
        while(index < processes.size() && processes.get(index).arrival <= time) index++;
    }
    @Override
    public void execute() {
        int  c = index, mn = Integer.MAX_VALUE;
        for (int i = 0; i < index; i++) {
            if (!processes.get(i).flag) {
                aging_time.set(i, aging_time.get(i)+1);
            }
        }
        for (int i = 0; i < index; i++) {
            int new_burst = processes.get(i).burst_time - (aging_time.get(i));
            if (processes.get(i).arrival <= time && new_burst < mn && !processes.get(i).flag) {
                c = i;
                mn = new_burst;
            }
        }
        if (c == index) {
            time++;
        } else {
            SRTF_process currentProcess = processes.get(c);
            if (currentProcess.response == 0) {
                currentProcess.response = time;
            }
            currentProcess.burst_time--;
            if (currentProcess.burst_time == 0) {
                currentProcess.flag = true;
                currentProcess.complete_time = time + 1;
                currentProcess.turn_around = currentProcess.complete_time   - currentProcess.arrival;
                currentProcess.wait_time = currentProcess.turn_around - currentProcess.burst_time_original;
                finished_processes++;
            }
            time++;
            Interval newInterval = new Interval(time - 1, time);
            processIntervals.get(currentProcess).add(newInterval);
            int currentAging = aging_time.get(c);
            aging_time.set(c, currentAging /4);
        }
    }
    @Override
    public void print(float avg_wait, float avg_turn_around) {

        System.out.printf("%-15s %-15s %-20s%n", "Name","Wait Time", "Turn Around Time");
        for (SRTF_process process : processes) {
            System.out.printf("%-15s %-15d %-20d%n", process.name, process.wait_time, process.turn_around);
        }

        System.out.printf("%nAverage waiting time ==> %.2f%n", avg_wait);
        System.out.printf("Average turn around time ==> %.2f%n", avg_turn_around);
    }

    @Override
    public void printGanttChart() {
        int maxCompletionTime = time; // Maximum timeline
        processes.sort(new Comparator<SRTF_process>() {
            @Override
            public int compare(SRTF_process o1, SRTF_process o2) {
                return Integer.compare(o1.response, o2.response);
            }
        });

        // Time Axis
        System.out.print("Time:  ");
        for (int i = 0; i <= maxCompletionTime; i++) {
            System.out.printf("%-3d", i);
        }
        System.out.println();

        for (SRTF_process process : processes) {
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

    public void run(){
        while(finished_processes != processes.size()) {
            moveIndex();
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

    }

}
