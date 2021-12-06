package Software.SistemaOperacional.Drivers;

import java.util.LinkedList;
//import java.io.IOError;
import java.util.Queue;
import java.util.concurrent.Semaphore;

import Hardware.CPU;
import Hardware.CPU.Interrupt;
import Hardware.CPU.Opcode;
import Software.SistemaOperacional.MemoryManager;
import Software.SistemaOperacional.ProcessControlBlock;
import Software.SistemaOperacional.ProcessManager;
import Software.SistemaOperacional.ProcessControlBlock.ProcessState;

public class Console extends Thread {

    private KeyboardDriver keyboardDriver;
    private ConsoleOutputDriver consoleOutputDriver;
    private Queue<Request> requestQueue;
    private ProcessManager processManager;
    private MemoryManager memoryManager;
    private CPU cpu;
    private Semaphore semaCPU;
    private Semaphore semaProcessQueue;

    public static enum IORequest {
        READ,
        WRITE;
    }

    public class Request{
        private int processId;
        private IORequest iORequest;
        //private int value;

        public Request(int processId, IORequest iORequest){
            this.processId = processId;
            this.iORequest = iORequest;
            //this.value = value;
        }
    }

    public Console(ProcessManager processManager, MemoryManager memoryManager, CPU cpu, Semaphore semaCPU, Semaphore semaProcessQueue){
        requestQueue = new LinkedList<Request>();        
        this.processManager = processManager;
        this.memoryManager = memoryManager;
        this.cpu = cpu;
        this.semaCPU = semaCPU;
        this.semaProcessQueue = semaProcessQueue;
        loadDrivers();
    }

    public void newRequest(int processId, int iORequest){
        switch(iORequest){
            case 1:
                requestQueue.add(new Request(processId, IORequest.READ));
                break;
            case 2:
                requestQueue.add(new Request(processId, IORequest.WRITE));
                break;
        }
    }
    
    @Override
    public void run(){
        while(true){

            if(!requestQueue.isEmpty()){

                Request request = requestQueue.poll();

                try { semaProcessQueue.acquire(); } 
                catch(InterruptedException ie) { }

                ProcessControlBlock process = processManager.peekBlockedProcess(request.processId);

                switch(request.iORequest){                        
                    case READ:
                        input(process);
                    break;
                    
                    case WRITE:
                        output(process);
                    break;
                }

                cpu.setIdProcessIo(process.id);

                semaProcessQueue.release();


                cpu.itr = Interrupt.IO;


                //GAMBIARRA ???
                if(semaCPU.availablePermits() == 0){
                    semaCPU.release();
                }
            }

        }
    }

    public void input(ProcessControlBlock process){
        memoryManager.memory.address[cpu.translateAddress(process.registrators[9])].opc = Opcode.DATA;
        memoryManager.memory.address[cpu.translateAddress(process.registrators[9])].r1 = -1;
        memoryManager.memory.address[cpu.translateAddress(process.registrators[9])].r2 = -1;
        memoryManager.memory.address[cpu.translateAddress(process.registrators[9])].p = keyboardDriver.readKeyboardInput();
        //cpu.itr = Interrupt.NoInterrupt; 
    }

    public void output(ProcessControlBlock process){
        System.out.println("\nSAIDA Process ID [" + cpu.process.id+ "]");
        consoleOutputDriver.systemOutInt(memoryManager.memory.address[cpu.translateAddress(process.registrators[9])].p);
        //cpu.itr = Interrupt.NoInterrupt; 
    }

    private void loadDrivers(){
        keyboardDriver = new KeyboardDriver();
        consoleOutputDriver = new ConsoleOutputDriver();
    }
}
