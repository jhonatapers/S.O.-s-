package Software.SistemaOperacional;

import java.util.concurrent.Semaphore;

import Software.SistemaOperacional.ProcessControlBlock.ProcessState;

public class Scheduler extends Thread {
    
    private ProcessManager processManager;
    
    private Semaphore sSch;
    private Semaphore sCPU;
    
    public Scheduler(ProcessManager processManager, Semaphore sSch, Semaphore sCPU){
        this.processManager = processManager;

        this.sSch = sSch;
        this.sCPU = sCPU;
    }

    @Override
    public void run(){
        
        while(true){
            
            try { sSch.acquire(); } 
            catch(InterruptedException ie) { }

 
            //Busca o primeiro da fila
            if(processManager.peekReadyProcess() != null){
                //Seta o primeiro da fila para executar.
                ProcessControlBlock process = processManager.polReadyProcess();
                process.processState = ProcessState.Running;
                processManager.dispatch(process);
                //sCPU.release();
            }

            sCPU.release();
        }
    }
}
