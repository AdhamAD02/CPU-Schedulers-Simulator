package AG;


import java.util.ArrayList;
import java.util.Scanner;

public class AG_Main {
    public static void execution(){
        Scanner in = new Scanner(System.in);
        System.out.println("Enter number of processes: ");
        int n = in.nextInt();
        System.out.println("Enter the quantum capacity");
        int quantum = in.nextInt();
        ArrayList<AG_process> p = new ArrayList<>();
        System.out.println("Enter Name, Arrival Time , Burst Time, priority");
        for (int i = 0; i < n; i++) {
            String name = in.next();
            int arrive = in.nextInt();
            int burst = in.nextInt();
            int prio = in.nextInt();
            p.add(new AG_process(name , arrive , burst , prio , quantum ));
        }

        AG_algorithm algo = new AG_algorithm(p);
        algo.run();
    }
}
