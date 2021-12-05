package Software.SistemaOperacional;

import java.util.LinkedList;
import java.util.Queue;

import Hardware.CPU;

public class ProcessManager extends Thread{
    
    public ProcessControlBlock runningProcess;
    private Queue<ProcessControlBlock> readyQueue;
    private Queue<ProcessControlBlock> blockedQueue;
    private MemoryManager memoryManager;
    private CPU cpu;

    public ProcessManager(CPU cpu, MemoryManager memoryManager){
        this.cpu = cpu;
        readyQueue = new LinkedList<ProcessControlBlock>();
        this.memoryManager = memoryManager;
    }

    //Adiciona processo na fila de execução.
    public void createProcess(ProcessControlBlock processControlBlock){
        readyQueue.add(processControlBlock); 
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

    public void terminateProcess(ProcessControlBlock processControlBlock){
        memoryManager.deallocate(processControlBlock.tablePage);
    }
}
