package Software.SistemaOperacional;

import java.util.concurrent.Semaphore;

import Hardware.CPU;
import Hardware.Memory;
import Hardware.CPU.Interrupt;
import Hardware.Memory.Word;
import Software.SistemaOperacional.Drivers.*;
import Software.SistemaOperacional.ProcessControlBlock.ProcessState;

public class SOs {

    public InterruptHandling interruptHandling;

    private MemoryManager memoryManager;
    private CPU cpu;
    private ProcessManager processManager;

    private Console console;

    //Semaforo Escalonador
    private Semaphore sSch;
    //Escalonador
    private Scheduler scheduler;

    public SOs(CPU _cpu, Memory _memory, int _pageLength, Semaphore sSch, Semaphore sCPU){      
        interruptHandling = new InterruptHandling();

        memoryManager = new MemoryManager(_memory, _pageLength);
        _cpu.setInterruptHandling(interruptHandling);
        cpu = _cpu;
        processManager = new ProcessManager(cpu, memoryManager);

        this.sSch = sSch;

        this.console = new Console(cpu, processManager, memoryManager);        

        scheduler = new Scheduler(processManager, sSch, sCPU);
    }
    
    public class InterruptHandling{
        
        //Tratando interrupcoes
        public void handle(Interrupt itr){
            switch (itr) {
                case InvalidInstruction:
                System.out.println("\n\t\t###### "+ itr + " => PROCESS ID["+cpu.process.id+"] ######");
                    cpu.process.processState = ProcessState.Blocked;
                    processManager.killProcess(cpu.process);                    
                    //Libera Escalonador para pegar proximo processo
                    sSch.release();  
                    break;
                case InvalidAdress:
                System.out.println("\n\t\t###### "+ itr + " => PROCESS ID["+cpu.process.id+"] ######");
                    cpu.process.processState = ProcessState.Blocked;
                    processManager.killProcess(cpu.process);                    
                    //Libera Escalonador para pegar proximo processo
                    sSch.release();  
                    break;
                case Overflow:
                System.out.println("\n\t\t###### "+ itr + " => PROCESS ID["+cpu.process.id+"] ######");
                    cpu.process.processState = ProcessState.Blocked;
                    processManager.killProcess(cpu.process);                    
                    //Libera Escalonador para pegar proximo processo
                    sSch.release();  
                    break;
                case ProgramEnd:                
                    System.out.println("\n\t\t###### "+ itr + " => PROCESS ID["+cpu.process.id+"] ######");
                    cpu.process.processState = ProcessState.Blocked;
                    processManager.killProcess(cpu.process);                    
                    //Libera Escalonador para pegar proximo processo
                    sSch.release();       
                    break;
                case Trap:
                    ProcessControlBlock trapProcess = cpu.process.clone();
                    if(cpu.validAdress(cpu.translateAddress(cpu.getRegistrator(9),trapProcess.tablePage))){
                        cpu.process.processState = ProcessState.Blocked;
                        
                        //Adiciona na fila de bloqueados
                        processManager.addBlockedQueue(trapProcess.clone());                        
                        
                        //Empacota pedido ao Console
                        console.newRequest(trapProcess.id, trapProcess.registrators[8]);

                        //Libera escalonador
                        sSch.release();

                    }else{
                        cpu.itr = Interrupt.InvalidAdress;
                    }
                    break;
                case ClockInterrupt:
                    
                    //Adiciona processo atual no final da fila
                    processManager.addReadyQueue(cpu.process.clone());
                    
                    //Libera escalonador
                    sSch.release();
                    break;
                case IO:
                    //Pega Processo Bloqueado por IO que esta pronto e joga na fila de prontos
                    ProcessControlBlock processIO = processManager.polBlockedProcess(cpu.getIDProcessIO()).clone();
                    processIO.processState = ProcessState.Ready;

                    processManager.addReadyQueue(processIO);

                    break;
                case NoProcessRunning:
                    sSch.release();
                    break;
                default:
                    System.out.println(itr);
                    cpu.itr = Interrupt.ProgramEnd;
                    break;
            }

        }        
    }

    public Boolean newProcess(Word[] program){
        return processManager.createProcess(program);
    }

    public void runScheduler(){
        scheduler.start();
    }

    public void runConsole(Semaphore sNeedInput, Semaphore sInput, Semaphore sInputed){
        console.setSemaShell(sNeedInput, sInput, sInputed);
        console.start();
    }

}

