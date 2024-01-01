package PS;

import java.util.PriorityQueue;
import CPU_basics.*;

import java.util.*;


public class PS_algorithm implements CPU_Scheduler{
    private ArrayList<PS_process> processes;
    private PriorityQueue<PS_process> readyQueue = new PriorityQueue<>(Comparator.comparingInt(o -> o.priority));
    private ArrayList<PS_process> finishedProcesses = new ArrayList<>();
    private Map<PS_process, ArrayList<Interval>> processIntervals = new HashMap<>();
    private int currentTime = 0;
    public double averageWaitingTime;
    public double averageTurnaroundTime;

    public PS_algorithm(ArrayList<PS_process> processes) {
        this.processes = processes;
    }

    @Override
    public void execute() {

        processes.sort(new Comparator<PS_process>() {
            @Override
            public int compare(PS_process o1, PS_process o2) {
                if (o1.arrival == o2.arrival) {
                    return Integer.compare(o1.priority, o2.priority);
                }
                return Integer.compare(o1.arrival, o2.arrival);
            }
        });


        readyQueue.offer(processes.get(0));
        currentTime = processes.get(0).arrival;
        processes.remove(0);
        while (!readyQueue.isEmpty()) {
            PS_process currentProcess = readyQueue.poll();

            currentProcess.waitingTime = currentTime - currentProcess.arrival;
            currentProcess.turn_around = currentProcess.waitingTime + currentProcess.burst_time;
            currentProcess.complete_time = currentTime + currentProcess.burst_time;
            currentTime = currentProcess.complete_time;

            if (!processIntervals.containsKey(currentProcess)) {
                processIntervals.put(currentProcess, new ArrayList<>());
            }
            processIntervals.get(currentProcess).add(new Interval(currentTime - currentProcess.burst_time, currentTime));
            finishedProcesses.add(currentProcess);

            for (int i = 0; i < processes.size(); i++) {
                if (processes.get(i).arrival <= currentTime) {
                    readyQueue.offer(processes.get(i));
                    processes.remove(i);
                    i--;
                }
            }

            fixStarvation();
        }
    }

    public void run() {
        execute();
        int totalWaitingTime = 0;
        for (PS_process process : finishedProcesses) {
            totalWaitingTime += process.waitingTime;
        }
        averageWaitingTime = (double) totalWaitingTime / finishedProcesses.size();

        int totalTurnaroundTime = 0;
        for (PS_process process : finishedProcesses) {
            totalTurnaroundTime += process.turn_around;
        }
        averageTurnaroundTime = (double) totalTurnaroundTime / finishedProcesses.size();
        printGanttChart();
        print((float) averageWaitingTime, (float) averageTurnaroundTime);

    }

    @Override
    public void print(float a, float b) {

        System.out.printf("%-15s %-15s %-20s%n", "Name", "Wait Time", "Turn Around Time");


        for (int i = 0; i < finishedProcesses.size(); i++) {
            System.out.printf("%-15s %-15d %-20d%n", finishedProcesses.get(i).name,
                    finishedProcesses.get(i).waitingTime, finishedProcesses.get(i).turn_around);
        }


        System.out.printf("%nAverage waiting time ==> %.2f%n", a);
        System.out.printf("Average turn around time ==> %.2f%n", b);

    }

    @Override
    public void printGanttChart() {
        System.out.println("\nGantt Chart");
        int maxCompletionTime = currentTime; // Maximum timeline
        finishedProcesses.sort(new Comparator<PS_process>() {
            @Override
            public int compare(PS_process o1, PS_process o2) {
                return Integer.compare(o1.arrival, o2.arrival);
            }
        });

        // Time Axis
        System.out.print("Time:  ");
        for (int i = 0; i <= maxCompletionTime; i++) {
            System.out.printf("%-3d", i);
        }
        System.out.println();

        for (PS_process process : finishedProcesses) {
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


    private void fixStarvation() {
        if (readyQueue.isEmpty()) {
            return;
        }
        PriorityQueue<PS_process> updatedQueue = new PriorityQueue<>(Comparator.comparingInt(o -> o.priority));
        while (!readyQueue.isEmpty()) {
            PS_process process = readyQueue.poll();
            if (currentTime - process.arrival > 7) {
                process.priority--;
            }
            updatedQueue.offer(process);
        }
        readyQueue = updatedQueue;
    }
}