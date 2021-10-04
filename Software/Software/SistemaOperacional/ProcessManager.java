package Software.SistemaOperacional;

import java.util.LinkedList;
import java.util.Queue;

import Hardware.CPU;

public class ProcessManager {

    
    public ProcessControlBlock runningProcess; //por aqui
    private Queue<ProcessControlBlock> processQueue;
    private MemoryManager memoryManager;
    private CPU cpu;

    public ProcessManager(CPU cpu, MemoryManager memoryManager){
        this.cpu = cpu;
        processQueue = new LinkedList<ProcessControlBlock>();
        this.memoryManager = memoryManager;
    }

    public void createProcess(ProcessControlBlock processControlBlock){
        processQueue.add(processControlBlock); //Adiciona processo na fila, informando quais os frames alocados na memoria.
    }
    
    public void dispatch(ProcessControlBlock process){
        cpu.setProcess(process);
    }

    public ProcessControlBlock pollNextProcess(){
        return processQueue.poll();
    }

    public ProcessControlBlock peekNextProcess(){
        return processQueue.peek();
    }

    public void terminateProcess(ProcessControlBlock processControlBlock){
        memoryManager.deallocate(processControlBlock.tablePage);
    }


}
