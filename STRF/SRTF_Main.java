package SRTF;

import java.util.ArrayList;
import java.util.Scanner;

public class SRTF_Main {
    public static void execution(){
        Scanner in = new Scanner(System.in);
        System.out.println("Enter number of processes: ");
        int n = in.nextInt();
        ArrayList<SRTF_process> p = new ArrayList<>();
        System.out.println("Enter Name, Arrival Time , Burst Time");
        for (int i = 0; i < n; i++) {
            String name = in.next();
            int arrive = in.nextInt();
            int burst = in.nextInt();
            p.add(new SRTF_process(name , arrive , burst));
        }

        SRTF_algorithm algo = new SRTF_algorithm(p);
        algo.run();
    }
}

