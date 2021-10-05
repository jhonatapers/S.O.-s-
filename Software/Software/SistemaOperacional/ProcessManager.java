package Software.SistemaOperacional;

import java.util.LinkedList;
import java.util.Queue;

import Hardware.CPU;

public class ProcessManager {
    
    public ProcessControlBlock runningProcess;
    private Queue<ProcessControlBlock> processQueue;
    private MemoryManager memoryManager;
    private CPU cpu;

    public ProcessManager(CPU cpu, MemoryManager memoryManager){
        this.cpu = cpu;
        processQueue = new LinkedList<ProcessControlBlock>();
        this.memoryManager = memoryManager;
    }

    //Adiciona processo na fila de execução.
    public void createProcess(ProcessControlBlock processControlBlock){
        processQueue.add(processControlBlock); 
    }
    
    //Coloca um processo para executar no CPU.
    public void dispatch(ProcessControlBlock process){
        cpu.setProcess(process);
    }

    //Resgata o primeiro processo da fila, removendo.
    public ProcessControlBlock pollNextProcess(){
        return processQueue.poll();
    }

    //Resgata o primeiro processo da fila, sem remover.
    public ProcessControlBlock peekNextProcess(){
        return processQueue.peek();
    }

    public void terminateProcess(ProcessControlBlock processControlBlock){
        memoryManager.deallocate(processControlBlock.tablePage);
    }
}
