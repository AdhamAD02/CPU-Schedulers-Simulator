package SJF;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import CPU_basics.*;


public class SJF_algorithm implements CPU_Scheduler {
    private ArrayList<SJF_process> processes;

    private Map<String, ArrayList<Interval>> process_intervals = new HashMap<>();

    private int time = 0;
    private int index = 0;
    private int finished_processes = 0;


    public SJF_algorithm(ArrayList<SJF_process> p) {
        p.sort(new Comparator<SJF_process>() {
            @Override
            public int compare(SJF_process o1, SJF_process o2) {
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
        int c = index , mn = Integer.MAX_VALUE;
        for (int i = 0; i < index ; i++) {
            if (processes.get(i).burst_time < mn && !processes.get(i).flag) {
                c = i;
                mn = processes.get(i).burst_time;
            }
        }
        if (c == index) {
            time++;
        } else {
            SJF_process currentProcess = processes.get(c);
            currentProcess.response = time;
            currentProcess.flag = true;
            currentProcess.wait_time = time - processes.get(c).arrival;
            currentProcess.turn_around = currentProcess.wait_time + currentProcess.burst_time + 1;
            Interval newInterval = new Interval(time, time + currentProcess.burst_time );
            process_intervals.computeIfAbsent(currentProcess.name, k -> new ArrayList<>()).add(newInterval);
            time += currentProcess.burst_time + 1;
            currentProcess.complete_time = time;
            finished_processes++;
        }
    }

    @Override
    public void print(float avg_wait, float avg_turn_around) {

        // Print table header
        System.out.printf("%-15s %-15s %-20s%n", "Name", "Wait Time", "Turn Around Time");

        // Print table rows
        for (int i = 0; i < processes.size(); i++) {
            System.out.printf("%-15s %-15d %-20d%n", processes.get(i).name,
                    processes.get(i).wait_time, processes.get(i).turn_around);
        }

        // Print averages
        System.out.printf("%nAverage waiting time ==> %.2f%n", avg_wait);
        System.out.printf("Average turn around time ==> %.2f%n", avg_turn_around);
    }

    @Override
    public void printGanttChart() {
        int maxCompletionTime = time; // Maximum timeline
        processes.sort(new Comparator<SJF_process>() {
            @Override
            public int compare(SJF_process o1, SJF_process o2) {
                return Integer.compare(o1.response, o2.response);
            }
        });

        // Time axis
        System.out.print("Time:  ");
        for (int i = 0; i <= maxCompletionTime; i++) {
            System.out.printf("%-3d", i);
        }
        System.out.println();

        for (SJF_process process : processes) {
            System.out.print(process.name + ":    ");
            int lastEnd = 0;
            for (Interval interval : process_intervals.get(process.name)) {

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


    @Override
    public void run() {
        while (finished_processes != processes.size()) {
            moveIndex();
            execute();
        }
        float avg_wait = 0 , avg_turn_around = 0;
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