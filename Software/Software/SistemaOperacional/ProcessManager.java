package Software.SistemaOperacional;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

import Hardware.CPU;

import Hardware.Memory.Word;
import Software.SistemaOperacional.MemoryManager.AllocatesReturn;

public class ProcessManager extends Thread{
    
    public ProcessControlBlock runningProcess;
    private Queue<ProcessControlBlock> readyQueue;
    private Queue<ProcessControlBlock> blockedQueue;
    private MemoryManager memoryManager;
    private CPU cpu;
    private Semaphore semaSch;
    private Semaphore semaCPU;


    public ProcessManager(CPU cpu, MemoryManager memoryManager){
        this.cpu = cpu;
        readyQueue = new LinkedList<ProcessControlBlock>();
        this.memoryManager = memoryManager;
    }

    /*
    //Adiciona processo na fila de execução.
    public void createProcess(ProcessControlBlock processControlBlock){
        readyQueue.add(processControlBlock); 
    }
    */

    public void readyQueueProcess(ProcessControlBlock process){
        readyQueue.add(process); 
    }

    public void blockedQueueProcess(ProcessControlBlock process){
        blockedQueue.add(process); 
    }

    public ProcessControlBlock getProcess(int id){
        //ProcessControlBlock process = new ProcessControlBlock();

        for (ProcessControlBlock process : readyQueue) {
            if(process.id == id){
                return process;
            }  
        }

        for (ProcessControlBlock process : blockedQueue) {
            if(process.id == id){
                return process;
            }  
        }

        return new ProcessControlBlock();

        //return process;
    }

    public void createProcess(Word[] program){
        if(loadProgram(program)){

            //Verifica se CPU esta Rodando algum processo
            if(semaCPU.availablePermits() > 0){

                //Libera escalonador
                semaSch.release();
            }

        }
    }
    
    //Verificar e carregar programa na memória
    public boolean loadProgram(Word[] program){
        
        //Verifica se é possível alocar o programa em memória ou não. Se possível, retorna também quais páginas foram alocadas.
        AllocatesReturn allocatesReturn = memoryManager.allocates(program.length);

        if(allocatesReturn.canAlocate){
            memoryManager.carga(program,allocatesReturn.tablePages); //carrega programa nos seus respectivos frames 

            //Cria PCB e adiciona o processo na fila de execução.
            //processManager.createProcess(new ProcessControlBlock(allocatesReturn.tablePages));

            readyQueue.add(new ProcessControlBlock(allocatesReturn.tablePages));

            return true;
        }

        return false;
    }
    
    //Coloca um processo para executar no CPU.
    public void dispatch(ProcessControlBlock process){
        cpu.setProcess(process);
    }

    //Resgata o primeiro processo da fila, removendo.
    public ProcessControlBlock pollNextProcess(){
        return readyQueue.poll();
    }

    //Resgata o primeiro processo da fila, sem remover.
    public ProcessControlBlock peekNextProcess(){
        return readyQueue.peek();
    }

    public void finishProcess(ProcessControlBlock processControlBlock){
        memoryManager.deallocate(processControlBlock.tablePage);
    }
}
