package Software.SistemaOperacional.Drivers;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

import Hardware.CPU;
import Hardware.CPU.Interrupt;
import Hardware.CPU.Opcode;
import Software.SistemaOperacional.MemoryManager;
import Software.SistemaOperacional.ProcessControlBlock;
import Software.SistemaOperacional.ProcessManager;

public class Console extends Thread {

    private KeyboardDriver keyboardDriver;
    private ConsoleOutputDriver consoleOutputDriver;
    private Semaphore sRequestQueue;
    private Queue<Request> requestQueue;

    private CPU cpu;
    private ProcessManager processManager;
    private MemoryManager memoryManager;

    private Semaphore sConsole;

    private Semaphore sCPU;

    public Console(CPU cpu, ProcessManager processManager, MemoryManager memoryManager, Semaphore sConsole, Semaphore sCPU){
        sRequestQueue = new Semaphore(1);
        requestQueue = new LinkedList<Request>(); 
        loadDrivers();

        this.cpu = cpu;
        this.processManager = processManager;
        this.memoryManager = memoryManager;

        this.sConsole = sConsole;
        this.sCPU = sCPU;
    }

    @Override
    public void run(){
        while(true){

            try { sConsole.acquire(); } 
            catch(InterruptedException ie) { }

            Request request = requestQueue.poll();

            ProcessControlBlock process = processManager.polBlockedProcess(request.processId);

            switch(request.iORequest){                        
                case READ:
                    input(process);
                break;
                
                case WRITE:
                    output(process);
                break;
            }

            processManager.addBlockedQueue(process.clone());
            cpu.setIDProcessIO(process.id);
            cpu.setIoInterrupt();

            if(sCPU.availablePermits() == 0){
                sCPU.release();
            }
        }
    }
    
    public void newRequest(int processId, int type){
        try { sRequestQueue.acquire(); } 
        catch(InterruptedException ie) { }

        requestQueue.add(new Request(processId, type));

        sRequestQueue.release();
    }

    private void loadDrivers(){
        keyboardDriver = new KeyboardDriver();
        consoleOutputDriver = new ConsoleOutputDriver();
    }

    public void input(ProcessControlBlock process){
        memoryManager.memory.address[cpu.translateAddress(process.registrators[9])].opc = Opcode.DATA;
        memoryManager.memory.address[cpu.translateAddress(process.registrators[9])].r1 = -1;
        memoryManager.memory.address[cpu.translateAddress(process.registrators[9])].r2 = -1;
        memoryManager.memory.address[cpu.translateAddress(process.registrators[9])].p = keyboardDriver.readKeyboardInput();
        process.interrupt = Interrupt.NoInterrupt;
    }

    public void output(ProcessControlBlock process){
        //System.out.println("\nSAIDA Process ID [" + cpu.process.id+ "]");
        consoleOutputDriver.systemOutInt(memoryManager.memory.address[cpu.translateAddress(process.registrators[9])].p);
        process.interrupt = Interrupt.NoInterrupt;
    }

    public static enum IORequest {
        READ,
        WRITE;
    }

    public class Request{
        private int processId;
        private IORequest iORequest;

        public Request(int processId, int type){
            this.processId = processId;
            switch(type){
                case 1: 
                    iORequest = IORequest.READ;
                    break;
                case 2:
                    iORequest = IORequest.WRITE;
                    break;
            }
        }
    }

    public class ConsoleOutputDriver {
        
        public void systemOutInt(int conteudo) {
            System.out.println("\t"+conteudo);
        }

    }
    
    public class KeyboardDriver {
        
        private Scanner sc = new Scanner(System.in);
        int input;
        
        public int readKeyboardInput() {
            System.out.println("Digite um int:");
            input = sc.nextInt();
            return input;
        }
        
        //Utilizado para "limpar" o conteudo do leitor.
        public void flushReaderBuffer() { 
            sc.nextLine();
        }   
    }
    
}
