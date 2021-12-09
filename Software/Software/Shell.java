package Software;

import java.util.Scanner;
import java.util.concurrent.Semaphore;

import Hardware.Memory.Word;
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
        
        clearScreen();

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
                case "--help":
                    System.out.println(" ------------------------------------------ ");
                    System.out.println(" ------------------PROGRAMAS--------------- ");
                    System.out.println(" ------------------------------------------ ");
                    System.out.println("|  contador                                |");
                    System.out.println("|  fatorial                                |");
                    System.out.println("|  add                                     |");
                    System.out.println("|  sub                                     |");
                    System.out.println("|  mult                                    |");
                    System.out.println(" ------------------------------------------ ");
                    System.out.println(" ------------------------------------------ ");
                break;
                case "clear":
                    clearScreen();
                    break;
                case "contador":
                    tryCreateProcess(new Softwares().contadorInOut, "Contador");
                    break;
                case "fatorial":
                    tryCreateProcess(new Softwares().Fatorial, "Fatorial");
                    break;
                case "add":
                    tryCreateProcess(new Softwares().ADD, "Add");
                    break;
                case "sub":
                    tryCreateProcess(new Softwares().SUB, "Sub");
                    break;
                case "mult":
                    tryCreateProcess(new Softwares().MULT, "Mult");
                    break;
                case default:
                    System.out.println("-!!!!!!!!!!!!!!!!!!!!!-");
                    System.out.println("COMANDO NAO RECONHECIDO");
                    System.out.println("-!!!!!!!!!!!!!!!!!!!!!-");
                break;       
            }
        }

    }

    public static void clearScreen() {  
        for(int i = 0; i < 100; i++){
            System.out.println("");
        } 
    }  

    private void tryCreateProcess(Word[] program, String programName){
        if(sos.newProcess(program)){
            System.out.println(programName + " CARREAGDO EM MEMÓRIA");
        }else{
            System.out.println("SEM ESPAÇO EM MEMÓRIA");
        }
    }
    
}
