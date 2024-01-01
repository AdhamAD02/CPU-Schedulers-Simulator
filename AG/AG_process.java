package AG;

import CPU_basics.process_data;

import java.util.Objects;

import static java.lang.Math.random;

public class AG_process extends process_data{
    public int quantum;
    public int priority;

    public int burst_time_original;
    public int AG_factor;
    AG_process(String name , int arrival , int burst , int priority , int quantum ){
        super(name , arrival , burst);
        burst_time_original = burst;
        this.priority = priority;
        this.quantum = quantum;
        int p = (int) (random() % 20);
        if(p>10) AG_factor = 10 + arrival + burst_time;
        else if(p < 10) AG_factor = p + arrival + burst_time;
        else AG_factor = priority + arrival + burst_time;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        AG_process other = (AG_process) obj;
        // Assuming 'name' is a unique identifier for AG_Process
        return Objects.equals(name, other.name);
    }


}
