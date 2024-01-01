package SRTF;

import CPU_basics.process_data;

import java.util.Objects;

public class SRTF_process extends process_data{

    public int burst_time_original;

    SRTF_process(String name , int arrival , int burst){
        super(name, arrival, burst);
        this.burst_time_original = burst;
    }

}
