package PS;

import CPU_basics.process_data;

import java.util.Comparator;

public class PS_process extends process_data {
    public int priority;
    public int waitingTime;

    public PS_process(String name, int arivalTime, int burstTime, int priority) {
        super(name, arivalTime, burstTime);
        this.priority = priority;
        this.waitingTime = 0;
    }

}