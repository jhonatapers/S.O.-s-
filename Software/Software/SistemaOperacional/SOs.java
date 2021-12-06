package Software.SistemaOperacional;

import java.util.concurrent.Semaphore;

import Hardware.CPU;
import Hardware.Memory;
import Hardware.CPU.Interrupt;
//import Hardware.CPU.Opcode;
import Hardware.Memory.Word;
import Software.SistemaOperacional.Drivers.*;
import Software.SistemaOperacional.Drivers.Console.IORequest;
//import Software.SistemaOperacional.Drivers.Console.Request;
//import Software.SistemaOperacional.MemoryManager.AllocatesReturn;
import Software.SistemaOperacional.ProcessControlBlock.ProcessState;

public class SOs {

    public InterruptHandling interruptHandling;
    private CPU cpu;
    private MemoryManager memoryManager;
    //private Memory memory;
    //private KeyboardDriver keyboardDriver;
    private ConsoleOutputDriver consoleOutputDriver;
    private ProcessManager processManager;
    private Semaphore semaSch;
    private Semaphore semaCPU;
    private Console console;
    private Scheduler scheduler;

    private Semaphore semaProcessQueue;

    public SOs(CPU _cpu, Memory _memory, int _pageLength, Semaphore semaSch, Semaphore semaCPU, Semaphore semaProcessQueue){      
        
        this.semaSch = semaSch;
        this.semaCPU = semaCPU;
        this.semaProcessQueue = semaProcessQueue;
        interruptHandling = new InterruptHandling();
        memoryManager = new MemoryManager(_memory, _pageLength);
        _cpu.setInterruptHandling(interruptHandling);
        cpu = _cpu;
        processManager = new ProcessManager(cpu, memoryManager, semaSch, semaCPU);
        scheduler = new Scheduler(processManager, semaSch, semaCPU);
        console = new Console(this.processManager, this.memoryManager, this.cpu, semaCPU, semaProcessQueue);
        console.start();
        //scheduler.run();
        //loadDrivers();
    }

    public void runScheduler(){
        scheduler.start();
    }

    public class InterruptHandling{
        
        //Tratando interrupcoes
        public void handle(Interrupt itr){
            switch (itr) {
                case InvalidInstruction:
                    System.out.println(itr);
                    cpu.itr = Interrupt.ProgramEnd;
                    break;
                case InvalidAdress:
                    System.out.println(itr);
                    cpu.itr = Interrupt.ProgramEnd;
                    break;
                case Overflow:
                    System.out.println(itr);
                    cpu.itr = Interrupt.ProgramEnd;
                    break;
                case ProgramEnd:
                    //System.out.println("\n\t\t###### "+ itr + " => PROCESS ID["+cpu.process.id+"] ######");

                    //Finaliza processo
                    processManager.finishProcess(cpu.process);

                    //Libera escalonador
                    semaSch.release();

                    break;
                case Trap:

                    ProcessControlBlock processTrap = cpu.process.clone();
                    processTrap.procesState = ProcessState.Block;

                    try { semaProcessQueue.acquire(); } 
                    catch(InterruptedException ie) { }

                    processManager.blockedQueueProcess(processTrap);

                    semaProcessQueue.release();
                    

                    if(cpu.validAdress(cpu.translateAddress(cpu.getRegistrator(9)))){


                        console.newRequest(processTrap.id, cpu.getRegistrator(8));

                        semaSch.release();

                    }
                    else{
                        cpu.itr = Interrupt.InvalidAdress;
                    }

                    break;
                case ClockInterrupt:
                    
                    //Adiciona processo atual no final da fila
                    processManager.addQueueProcess(cpu.process.clone());

                    //Libera escalonador
                    semaSch.release();

                    break;
                case IO:
                    ProcessControlBlock processIO = cpu.process;
                    processIO.procesState = ProcessState.Ready;

                    try { semaProcessQueue.acquire(); } 
                    catch(InterruptedException ie) { }

                    //Pega Processo Bloqueado por IO que esta pronto e joga na fila de prontos
                    processManager.readyQueueProcess(processManager.polBlockedProcess(cpu.getIdProcessIo()));

                    semaProcessQueue.release();

                    processManager.dispatch(processIO);
                    semaCPU.release();

                    break;

                default:
                    System.out.println(itr);
                    cpu.itr = Interrupt.ProgramEnd;
                    break;
            }

        }        
    }

    //GESIEL (depois que tiver o shell depois tirar ... vai pro SHELL)
    public void newProcess(Word[] program){
        processManager.createProcess(program);
    }
    
    /*
    //Verificar e carregar programa na memória
    public boolean loadProgram(Word[] program){
        
        //Verifica se é possível alocar o programa em memória ou não. Se possível, retorna também quais páginas foram alocadas.
        AllocatesReturn allocatesReturn = memoryManager.allocates(program.length);

        if(allocatesReturn.canAlocate){
            memoryManager.carga(program,allocatesReturn.tablePages); //carrega programa nos seus respectivos frames 

            //Cria PCB e adiciona o processo na fila de execução.
            processManager.createProcess(new ProcessControlBlock(allocatesReturn.tablePages));

            return true;
        }

        return false;
    }
    */


    /*
    public void input(){
        memoryManager.memory.address[cpu.translateAddress(cpu.getRegistrator(9))].opc = Opcode.DATA;
        memoryManager.memory.address[cpu.translateAddress(cpu.getRegistrator(9))].r1 = -1;
        memoryManager.memory.address[cpu.translateAddress(cpu.getRegistrator(9))].r2 = -1;
        memoryManager.memory.address[cpu.translateAddress(cpu.getRegistrator(9))].p = keyboardDriver.readKeyboardInput();
        cpu.itr = Interrupt.NoInterrupt; 
    }
    */

    public void output(){
        consoleOutputDriver.systemOutInt(memoryManager.memory.address[cpu.translateAddress(cpu.getRegistrator(9))].p);
        cpu.itr = Interrupt.NoInterrupt; 
    }

    //Recupera o primeiro processo da fila e coloca para executar na CPU.
    public void loadNextProcess(){
        processManager.dispatch(processManager.pollNextProcess());
    }

}

