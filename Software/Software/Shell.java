package Software;

import java.util.concurrent.Semaphore;

import Software.SistemaOperacional.SOs;

public class Shell extends Thread {

    private SOs so;
    private Semaphore sAviso;
    private Boolean aviso;

    public Shell(SOs so){
        this.so = so;
        sAviso = new Semaphore(0);
    }

    @Override 
    public void run(){
        while(true){

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            if(this.so.newProcess(new Softwares().contadorInOut)){
                System.out.println("Processo Carregado em memoria");
           
            }else{                
                System.out.println("Memoria Cheia");
            }     

        }
    }
}
