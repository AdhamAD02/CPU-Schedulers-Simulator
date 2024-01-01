package CPU_basics;
public class process_data {
    public String name;
    public int arrival = 0;
    public int burst_time = 0;
    public int complete_time = 0;
    public boolean flag = false;
    public int response = 0 ;
    public int wait_time = 0;
    public int turn_around = 0;
    public process_data(String name , int arrival , int burst){
        this.name = name;
        this.arrival = arrival;
        this.burst_time = burst;
    }
}
