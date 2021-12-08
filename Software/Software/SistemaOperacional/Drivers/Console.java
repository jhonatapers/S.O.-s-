package Software.SistemaOperacional.Drivers;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

import Hardware.CPU;
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

            if(!requestQueueisEmpty()){

                try { sRequestQueue.acquire(); } 
                catch(InterruptedException ie) { }

                Request request = requestQueue.poll();

                sRequestQueue.release();


                ProcessControlBlock process = processManager.peekBlockedProcess(request.processId);
    
                switch(request.iORequest){                        
                    case READ:
                        input(process);
                    break;
                    
                    case WRITE:
                        output(process);
                    break;
                }
    
                //processManager.addBlockedQueue(process.clone());
                cpu.setIDProcessIO(process.id);
                cpu.setIoInterrupt(true);
    
                /* gambiarra
                if(sCPU.availablePermits() == 0){
                    sCPU.release();
                }
                */

            }
        }
    }
    
    public void newRequest(int processId, int type){
        try { sRequestQueue.acquire(); } 
        catch(InterruptedException ie) { }

        requestQueue.add(new Request(processId, type));

        sRequestQueue.release();
    }

    private Boolean requestQueueisEmpty(){
        Boolean value;

        try { sRequestQueue.acquire(); } 
        catch(InterruptedException ie) { }

        value = requestQueue.isEmpty();

        sRequestQueue.release();

        return value;
    }

    private void loadDrivers(){
        keyboardDriver = new KeyboardDriver();
        consoleOutputDriver = new ConsoleOutputDriver();
    }

    public void input(ProcessControlBlock process){
        int address = cpu.translateAddress(process.registrators[9], process.tablePage);
        memoryManager.memory.address[address].opc = Opcode.DATA;
        memoryManager.memory.address[address].r1 = -1;
        memoryManager.memory.address[address].r2 = -1;
        memoryManager.memory.address[address].p = keyboardDriver.readKeyboardInput();
    }

    public void output(ProcessControlBlock process){
        //System.out.println("\nSAIDA Process ID [" + cpu.process.id+ "]");
        int address = cpu.translateAddress(process.registrators[9], process.tablePage);
        consoleOutputDriver.systemOutInt(memoryManager.memory.address[address].p);
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
