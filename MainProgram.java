import AG.*;
import SJF.*;
import SRTF.*;
import PS.*;
import CPU_basics.*;
import java.util.ArrayList;
import java.util.Scanner;
public class MainProgram {
    public static void main(String[] args){
        Scanner in = new Scanner(System.in);
        System.out.println("What Algorithm do you want to work with? (write the number of choice)" +
                "\n1- SJF" +
                "\n2- SRTF" +
                "\n3- PS" +
                "\n4- AG");
        int choice = in.nextInt();
        switch (choice){
            case 1:
                SJF_Main SJF_main = new SJF_Main();
                SJF_main.execution();
                break;
            case 2:
                SRTF_Main  SRTF_main = new SRTF_Main();
                SRTF_main.execution();
                break;
            case 3:
                PS_Main PS_main = new PS_Main();
                PS_main.execution();
                break;
            case 4:
                AG_Main  AG_main = new AG_Main();
                AG_main.execution();
                break;
            default:
                System.out.println("You entered invalid choice");
        }
    }
}

