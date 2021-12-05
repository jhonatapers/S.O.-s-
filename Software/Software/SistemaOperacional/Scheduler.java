package Software.SistemaOperacional;

import java.util.concurrent.Semaphore;

public class Scheduler extends Thread {
    
    private ProcessManager processManager;
    private Semaphore semaSch;
    private Semaphore semaCPU;
    

    public Scheduler(ProcessManager processManager, Semaphore semaSch, Semaphore semaCPU){
        this.processManager = processManager;
        this.semaSch = semaSch;
        this.semaCPU = semaCPU;
    }

    @Override
    public void run(){
        
        while(true){
            
            try { semaSch.acquire(); } 
            catch(InterruptedException ie) { }

            processManager.dispatch(processManager.pollNextProcess()); 

            semaCPU.notify();
        }
    }

}
