package Software;

import java.util.Scanner;

import Hardware.Memory.Word;
import Software.SistemaOperacional.SOs;

public class Shell extends Thread {

    private SOs sos;
    private Scanner sc = new Scanner(System.in);

    public Shell(SOs sos){
        this.sos = sos;
    }

    private void tryLoad(Word[] program){
        if(sos.newProcess(program)){
            System.out.println("Programa Carregado em memoria");
        }else{
            System.out.println("Memória cheia");
        }
    }

    @Override 
    public void run(){
        while(true){
            System.out.println("SHELL:");

            switch(sc.next()){
                case "#1":
                    tryLoad(new Softwares().contadorInOut);
                    break;
                case "#2":
                    tryLoad(new Softwares().ADD);
                    break;
            }
            
/*
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            if(this.so.newProcess(new Softwares().contadorInOut)){
                //System.out.println("Processo Carregado em memoria");
           
            }else{                
                //System.out.println("Memoria Cheia");
            }     
*/
        }
    }
}
