package Software.SistemaOperacional.Drivers;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

import Hardware.CPU;
import Hardware.CPU.Opcode;
import Software.Shell;
import Software.SistemaOperacional.MemoryManager;
import Software.SistemaOperacional.ProcessControlBlock;
import Software.SistemaOperacional.ProcessManager;

public class Console extends Thread {

    private Semaphore sRequestQueue;
    private Queue<Request> requestQueue;

    private CPU cpu;
    private ProcessManager processManager;
    private MemoryManager memoryManager;

    //Semaforos para sincronização com o Shell
    private Semaphore sNeedInput;
    private Semaphore sInput;
    private Semaphore sInputed;

    public Console(CPU cpu, ProcessManager processManager, MemoryManager memoryManager){
        sRequestQueue = new Semaphore(1);
        requestQueue = new LinkedList<Request>(); 

        this.cpu = cpu;
        this.processManager = processManager;
        this.memoryManager = memoryManager;
    }

    @Override
    public void run(){
        while(true){

            //Simula atraso do Console
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

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

                cpu.setIDProcessIO(process.id);
                cpu.setIoInterrupt(true);
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

    
    public void setSemaShell(Semaphore sNeedInput, Semaphore sInput, Semaphore sInputed){        
        this.sNeedInput = sNeedInput;
        this.sInput = sInput;
        this.sInputed = sInputed;
    }

    private int requestInput(){

        try { sNeedInput.acquire(); } 
        catch(InterruptedException ie) { }

        Shell.needInput = true;

        sNeedInput.release();

        System.out.println("Digite um inteiro:");

        boolean loop = true;
        while(loop){

            try { sInputed.acquire(); } 
            catch(InterruptedException ie) { }

            loop = !Shell.inputed;

            sInputed.release();
        }

        try { sInputed.acquire(); } 
        catch(InterruptedException ie) { }

        Shell.inputed = false;

        sInputed.release();

        try { sInput.acquire(); } 
        catch(InterruptedException ie) { }

        return Shell.input;
    }

    public void input(ProcessControlBlock process){
        int address = cpu.translateAddress(process.registrators[9], process.tablePage);
        memoryManager.memory.address[address].opc = Opcode.DATA;
        memoryManager.memory.address[address].r1 = -1;
        memoryManager.memory.address[address].r2 = -1;
        memoryManager.memory.address[address].p = requestInput();
        sInput.release();
    }

    public void output(ProcessControlBlock process){
        //System.out.println("\nSAIDA Process ID [" + cpu.process.id+ "]");
        int address = cpu.translateAddress(process.registrators[9], process.tablePage);
        int output = memoryManager.memory.address[address].p;

        System.out.println("\t"+output);
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
    
}
