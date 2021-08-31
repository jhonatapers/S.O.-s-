package Software.SistemaOperacional;
import java.util.Map;

import Hardware.CPU;
import Hardware.Memory;
import Hardware.CPU.Interrupt;
import Hardware.CPU.Opcode;
import Hardware.Memory.Word;
import Software.SistemaOperacional.Drivers.*;

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
                    cpu.itr = Interrupt.ProgramEnd;
                    break;
                case Trap:
                    switch(cpu.getRegistrator(8)){
                        case 1: 
                            input();
                            break;
                        case 2:
                            output();
                            break;
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
    private Memory memory;
    private KeyboardDriver keyboardDriver;
    private ConsoleOutputDriver consoleOutputDriver;

    public SOs(CPU _cpu, Memory _memory){        
        interruptHandling = new InterruptHandling();
        _cpu.setInterruptHandling(interruptHandling);
        cpu = _cpu;
        memory = _memory;
        loadDrivers();
    }
    
    public void loadProgram(int pc, Word[] program){
        for(int i=0; i < program.length; i++){
            memory.address[pc] = program[i];
            pc++;
        }
    }

    public void runProgram(int pc, int limiteInferior, int limiteSuperior){
        cpu.setContext(pc, limiteInferior, limiteSuperior);
        cpu.run();
    }

    private void loadDrivers(){
        keyboardDriver = new KeyboardDriver();
        consoleOutputDriver = new ConsoleOutputDriver();
    }

    public void input(){
        memory.address[cpu.getRegistrator(9)].opc = Opcode.DATA;
        memory.address[cpu.getRegistrator(9)].r1 = -1;
        memory.address[cpu.getRegistrator(9)].r2 = -1;
        memory.address[cpu.getRegistrator(9)].p = keyboardDriver.readKeyboardInput();
        cpu.itr = Interrupt.NoInterrupt; 
    }

    public void output(){
        consoleOutputDriver.systemOutInt(memory.address[cpu.getRegistrator(9)].p);
        cpu.itr = Interrupt.NoInterrupt; 
    }



    private static final Map<String, Integer> SYS_PROGS_ADDRESS = Map.of(
        "INPUT", 900,
        "OUTPUT", 902
    );

    public void inputCompletao(){
        int pcOld = cpu.getPC();
        int limiteInferiorOld = cpu.getLimiteInferior();
        int limiteSuperiorOld = cpu.getLimiteSuperior();

        int pcNew = SYS_PROGS_ADDRESS.get("INPUT");
        
        loadProgram(pcNew, inputProgram());
        cpu.setContext(pcNew, pcNew, pcNew+1);
        
        cpu.itr = Interrupt.NoInterrupt;
        

        //memory.address[cpu.getRegistrator(9)].
    }

    private Word[] inputProgram(){
        return new Word[]{
            new Word(Opcode.STD, keyboardDriver.readKeyboardInput(), -1, cpu.getRegistrator(9)),
            new Word(Opcode.STOP, -1, -1, -1)
        };
    }




}

