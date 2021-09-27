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

                    if(processManager.peekNextProcess() != null){
                        processManager.dispatch(processManager.pollNextProcess());
                    }else{
                        cpu.itr =Interrupt.ProgramEnd;
                        break;
                    }
                                                            
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
                    processManager.createProcess(cpu.process);

                    //Busca o primeiro da fila
                    if(processManager.peekNextProcess() != null){
                        processManager.createProcess(processManager.pollNextProcess());
                    }

                    break;
                default:
                    System.out.println(itr);
                    cpu.itr = Interrupt.ProgramEnd;
                    break;
            }

        }        
    }

    public InterruptHandling interruptHandling;
    private CPU cpu;
    private MemoryManager memoryManager;
    //private Memory memory;
    private KeyboardDriver keyboardDriver;
    private ConsoleOutputDriver consoleOutputDriver;
    private ProcessManager processManager;

    public SOs(CPU _cpu, Memory _memory, int _pageLength){      
        interruptHandling = new InterruptHandling();
        memoryManager = new MemoryManager(_memory, _pageLength);
        _cpu.setInterruptHandling(interruptHandling);
        cpu = _cpu;
        processManager = new ProcessManager(cpu);
        loadDrivers();
    }
    
    //vai ser igual o newProcess no gerente de processo (nao mais utilizar como esta agr... utilizar no gerente de processo)
    public boolean loadProgram(Word[] program){
        
        //Verifica se é possível alocar o programa em memória ou não. Se possível, retorna também quais páginas foram alocadas.
        AllocatesReturn allocatesReturn = memoryManager.allocates(program.length);

        if(allocatesReturn.canAlocate){
            memoryManager.carga(program,allocatesReturn.tablePages); //carrega porgrama nos respectivos frames 

            processManager.createProcess(new ProcessControlBlock(allocatesReturn.tablePages));

            return true;
        }

        return false;
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

    public void loadNextProcess(){
        processManager.dispatch(processManager.pollNextProcess());
    }

}

