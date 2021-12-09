package Software;

import java.util.Scanner;
import java.util.concurrent.Semaphore;

import Software.SistemaOperacional.SOs;

public class Shell extends Thread {

    private SOs sos;
    private Scanner sc = new Scanner(System.in);

    private Semaphore sNeedInput;
    public static volatile Boolean needInput = false;

    private Semaphore sInput;
    public static volatile int input = 0;

    private Semaphore sInputed;
    public static volatile boolean inputed = false;         

    public Shell(SOs sos, Semaphore sNeedInput, Semaphore sInput, Semaphore sInputed){
        this.sos = sos;

        this.sNeedInput = sNeedInput;
        this.sInput = sInput;
        this.sInputed = sInputed;
    }

    @Override
    public void run(){

        while(true){

            String in = sc.nextLine();

            try { sNeedInput.acquire(); } 
            catch(InterruptedException ie) { }
            
            if(needInput){

                try{ 
                    int aux = Integer.parseInt(in);      
                    
                    try { sInput.acquire(); } 
                    catch(InterruptedException ie) { }

                    input = aux;

                    sInput.release();

                    needInput = false;  
                    
                       
                    try { sInputed.acquire(); } 
                    catch(InterruptedException ie) { }

                    inputed = true;

                    sInputed.release();
                    
                }
                catch(NumberFormatException e) { }
            }

            sNeedInput.release();

            switch(in){
                case "help":
                    System.out.println(" ------------------------------------------ ");
                    System.out.println(" ------------------PROGRAMS---------------- ");
                    System.out.println(" ------------------------------------------ ");
                    System.out.println("|contador                                  |");
                    System.out.println("|fatorial                                  |");
                    System.out.println("|add                                       |");
                    System.out.println("|sub                                       |");
                    System.out.println("|mult                                      |");
                    System.out.println(" ------------------------------------------ ");
                    System.out.println(" ------------------------------------------ ");
                break;
                case "contador":
                    break;
                case "fatorial":
                    break;
                case "add":
                    break;
                case "sub":
                    break;
                case "mult":
                    break;
                case default:
                break;       
            }
        }

    }

    
}
