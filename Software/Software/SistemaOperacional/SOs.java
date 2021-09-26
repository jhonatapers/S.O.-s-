package Software.SistemaOperacional;

import java.util.LinkedList;
import java.util.Queue;

import Hardware.CPU;
import Hardware.Memory;
import Hardware.CPU.Interrupt;
import Hardware.CPU.Opcode;
import Hardware.Memory.Word;
import Software.SistemaOperacional.Drivers.*;
import Software.SistemaOperacional.MemoryManager.AllocatesReturn;

public class SOs {
    
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
                    System.out.println(itr);
                    processQueue.poll();
                    
                    runProcess(processQueue.poll());
                    cpu.itr =Interrupt.ProgramEnd;
                    break;
                case Trap:
                    if(cpu.validAdress(cpu.translateAddress(cpu.getRegistrator(9)))){
                        switch(cpu.getRegistrator(8)){
                            case 1: 
                                input();
                                break;
                            case 2:
                                output();
                                break;
                        }
                    }else{
                        cpu.itr = Interrupt.InvalidAdress;
                    }
                    break;
                case ClockInterrupt:

                    //Adiciona no final da fila
                    ProcessControlBlock process = cpu.process;
                    process.pc = cpu.getPC();
                    process.registrators = cpu.getRegistrators();
                    processQueue.add(process);

                    //Busca o primeiro da fila
                    process = processQueue.poll();
                    if(process != null){
                        cpu.itr = Interrupt.NoInterrupt;
                        runProcess(process);
                    }

                    break;
                default:
                    System.out.println(itr);
                    cpu.itr = Interrupt.ProgramEnd;
                    break;
            }

        }        
    }

    public class ProcessControlBlock{

        public int[] tablePage;
        public int pc;
        public int[] registrators;        

        public ProcessControlBlock(int[] _tablePage){
            tablePage = _tablePage;
            pc = 0;
            registrators = new int[10];
        }

    }

    public InterruptHandling interruptHandling;
    private CPU cpu;
    private MemoryManager memoryManager;
    //private Memory memory;
    private KeyboardDriver keyboardDriver;
    private ConsoleOutputDriver consoleOutputDriver;
    private Queue<ProcessControlBlock> processQueue;

    public SOs(CPU _cpu, Memory _memory, int _pageLength){      
        processQueue = new LinkedList<ProcessControlBlock>();
        interruptHandling = new InterruptHandling();
        memoryManager = new MemoryManager(_memory, _pageLength);
        _cpu.setInterruptHandling(interruptHandling);
        cpu = _cpu;
        loadDrivers();
    }
    
    //vai ser igual o newProcess no gerente de processo (nao mais utilizar como esta agr... utilizar no gerente de processo)
    public boolean loadProgram(Word[] program){
        
        AllocatesReturn allocatesReturn = memoryManager.allocates(program.length);

        if(allocatesReturn.canAlocate){
            memoryManager.carga(program,allocatesReturn.tablePages); //carrega porgrama nos frames 
            processQueue.add(new ProcessControlBlock(allocatesReturn.tablePages)); //Adiciona processo na fila
            return true;
        }

        return false;
    }

    //(RETIRAR DPS)
    public void runProgram(int pc, int limiteInferior, int limiteSuperior){
        cpu.setProcess(processQueue.peek());
        //cpu.setContext(pc, limiteInferior, limiteSuperior);
        cpu.run();
    }

    public void runNextProcess(){
        cpu.setProcess(processQueue.poll());
        cpu.run();
    }

    public void setProcess(ProcessControlBlock process){
        cpu.setProcess(process);
    }

    public void runProcess(ProcessControlBlock process){
        cpu.setProcess(process);
        cpu.run();
    }

    private void loadDrivers(){
        keyboardDriver = new KeyboardDriver();
        consoleOutputDriver = new ConsoleOutputDriver();
    }

    public void input(){
        memoryManager.memory.address[cpu.translateAddress(cpu.getRegistrator(9))].opc = Opcode.DATA;
        memoryManager.memory.address[cpu.translateAddress(cpu.getRegistrator(9))].r1 = -1;
        memoryManager.memory.address[cpu.translateAddress(cpu.getRegistrator(9))].r2 = -1;
        memoryManager.memory.address[cpu.translateAddress(cpu.getRegistrator(9))].p = keyboardDriver.readKeyboardInput();
        cpu.itr = Interrupt.NoInterrupt; 
    }

    public void output(){
        consoleOutputDriver.systemOutInt(memoryManager.memory.address[cpu.translateAddress(cpu.getRegistrator(9))].p);
        cpu.itr = Interrupt.NoInterrupt; 
    }

}

