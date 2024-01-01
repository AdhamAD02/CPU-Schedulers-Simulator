package PS;

import java.util.ArrayList;
import java.util.Scanner;


public class PS_Main {
    public static void execution() {
        Scanner in = new Scanner(System.in);
        System.out.println("Enter number of processes: ");
        int n = in.nextInt();
        ArrayList<PS_process> p = new ArrayList<>();
        System.out.println("Enter Name, Arrival Time , Burst Time , Priority");
        for (int i = 0; i < n; i++) {
            String name = in.next();
            int arrive = in.nextInt();
            int burst = in.nextInt();
            int priority = in.nextInt();
            p.add(new PS_process(name, arrive, burst, priority));
        }

        PS_algorithm algo = new PS_algorithm(p);
        algo.run();


    }
}