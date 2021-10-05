package Software.SistemaOperacional;

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
                    System.out.println("\n\t\t###### "+ itr + " => PROCESS ID["+cpu.process.id+"] ######");
                    processManager.terminateProcess(cpu.process);

                    //Caso ainda exista programas na fila, execute o primeiro da fila.
                    if(processManager.peekNextProcess() != null){
                        processManager.dispatch(processManager.pollNextProcess());
                    }else{
                        cpu.itr = Interrupt.ProgramEnd;
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
                                System.out.println("\nSAIDA Process ID [" + cpu.process.id+ "]");
                                output();
                                break;
                        }
                    }else{
                        cpu.itr = Interrupt.InvalidAdress;
                    }
                    break;
                case ClockInterrupt:
                    
                    //Adiciona processo atual no final da fila
                    processManager.createProcess(cpu.process.clone());

                    //Busca o primeiro da fila
                    if(processManager.peekNextProcess() != null){
                        //Seta o primeiro da fila para executar.
                        processManager.dispatch(processManager.pollNextProcess());
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
        processManager = new ProcessManager(cpu,memoryManager);
        loadDrivers();
    }
    
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

    //Recupera o primeiro processo da fila e coloca para executar na CPU.
    public void loadNextProcess(){
        processManager.dispatch(processManager.pollNextProcess());
    }

}

