package Software.SistemaOperacional;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

import Hardware.CPU;
import Hardware.Memory.Word;
import Software.SistemaOperacional.MemoryManager.AllocatesReturn;
import Software.SistemaOperacional.ProcessControlBlock.ProcessState;

public class ProcessManager {
    
    private Queue<ProcessControlBlock> readyQueue;
    private Queue<ProcessControlBlock> blockedQueue;
    private MemoryManager memoryManager;
    private CPU cpu;

    private Semaphore sReadyQueue;
    private Semaphore sBlokedQueue;

    public ProcessManager(CPU cpu, MemoryManager memoryManager){
        readyQueue = new LinkedList<ProcessControlBlock>();
        blockedQueue = new LinkedList<ProcessControlBlock>();

        sReadyQueue = new Semaphore(1);
        sBlokedQueue = new Semaphore(1);
        
        this.cpu = cpu;
        this.memoryManager = memoryManager;
    }

    public void addReadyQueue(ProcessControlBlock process){

        //Pede acesso a fila de prontos
        try { sReadyQueue.acquire(); } 
        catch(InterruptedException ie) { }

        process.processState = ProcessState.Ready;
        readyQueue.add(process); 

        //Libera acesso
        sReadyQueue.release();
    }

    public void addBlockedQueue(ProcessControlBlock process){
        //Pede acesso a fila de bloqueados
        try { sBlokedQueue.acquire(); } 
        catch(InterruptedException ie) { }

        blockedQueue.add(process); 

        //Libera acesso
        sBlokedQueue.release();
    }

    public ProcessControlBlock polReadyProcess(){
        
        //Pede acesso a fila de prontos
        try { sReadyQueue.acquire(); } 
        catch(InterruptedException ie) { }

        ProcessControlBlock aux = readyQueue.poll();
        
        //Libera acesso
        sReadyQueue.release();

        return aux.clone();
    }

    public ProcessControlBlock peekReadyProcess(){
        
        //Pede acesso a fila de prontos
        try { sReadyQueue.acquire(); } 
        catch(InterruptedException ie) { }

        ProcessControlBlock aux = readyQueue.peek();
        
        //Libera acesso
        sReadyQueue.release();

        return aux;
    }

    public ProcessControlBlock polBlockedProcess(){
        
        //Pede acesso a fila de prontos
        try { sBlokedQueue.acquire(); } 
        catch(InterruptedException ie) { }

        ProcessControlBlock aux = blockedQueue.poll();
        
        //Libera acesso
        sBlokedQueue.release();

        return aux.clone();
    }

    public ProcessControlBlock polBlockedProcess(int id){

        ProcessControlBlock aux = new ProcessControlBlock(); 

        //Pede acesso a fila de prontos
        try { sBlokedQueue.acquire(); } 
        catch(InterruptedException ie) { }

        for (ProcessControlBlock process : blockedQueue) {
            if(process.id == id){
                aux = process;                                              
            }  
        }
        
        blockedQueue.remove(aux);  

        //Libera acesso
        sBlokedQueue.release();

        return aux.clone();
    }

    public ProcessControlBlock peekBlockedProcess(int id){

        ProcessControlBlock aux = new ProcessControlBlock(); 

        //Pede acesso a fila de prontos
        try { sBlokedQueue.acquire(); } 
        catch(InterruptedException ie) { }

        for (ProcessControlBlock process : blockedQueue) {
            if(process.id == id){
                aux = process;                                              
            }  
        }

        //Libera acesso
        sBlokedQueue.release();

        return aux.clone();
    }

    //Cria novo processo e joga na fila de prontos
    public Boolean createProcess(Word[] program){

        return loadProgram(program);
        /*
        boolean result = loadProgram(program);
        
        if(result){

            //Verifica se CPU esta Rodando algum processo
            if(sCPU.availablePermits() == 0){

                //Primeiro processo
                if(sSch.availablePermits() == 0){
                    //Libera escalonador
                    sSch.release();
                }
            }
        }
        

        return result;
        */
    }

    //Verificar e carregar programa na memória
    public boolean loadProgram(Word[] program){
        
        //Verifica se é possível alocar o programa em memória ou não. Se possível, retorna também quais páginas foram alocadas.
        AllocatesReturn allocatesReturn = memoryManager.allocates(program.length);

        if(allocatesReturn.canAlocate){
            memoryManager.carga(program,allocatesReturn.tablePages); //carrega programa nos seus respectivos frames 

            //Cria PCB e adiciona o processo na fila de execução.
            //processManager.createProcess(new ProcessControlBlock(allocatesReturn.tablePages));
            
            //Pede acesso a fila de prontos
            try { sReadyQueue.acquire(); } 
            catch(InterruptedException ie) { }

            readyQueue.add(new ProcessControlBlock(allocatesReturn.tablePages));

            //Libera acesso
            sReadyQueue.release();

            return true;
        }

        return false;
    }

    //Coloca um processo para executar no CPU.
    public void dispatch(ProcessControlBlock process){
        cpu.setProcess(process);
    }

    public void killProcess(ProcessControlBlock processControlBlock){
        memoryManager.deallocate(processControlBlock.tablePage);
    }
}
