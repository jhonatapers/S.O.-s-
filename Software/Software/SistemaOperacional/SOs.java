package Software.SistemaOperacional;

import java.util.ArrayList;

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
                    if(cpu.validAdress(cpu.getRegistrator(9))){
                        switch(cpu.getRegistrator(8)){
                            case 1: 
                                input();
                                break;
                            case 2:
                                output();
                                break;
                        }
                    }
                    break;
                default:
                    System.out.println(itr);
                    cpu.itr = Interrupt.ProgramEnd;
                    break;
            }

        }        
    }

    public class MemoryManager{

        private Memory memory;
        private int pageLength;
        private int frameLength;
        private int nFrames;
        private boolean freeFrames[];

        public MemoryManager(Memory _m, int _pLength){
            memory = _m;
            pageLength = _pLength;
            frameLength = pageLength;
            nFrames = memory.address.length/pageLength;

            freeFrames = new boolean[nFrames];
            for(int i = 0; i < freeFrames.length; i++){
                freeFrames[i] = true;
            }
        }

        public boolean allocates(int nWords, int[] tablePages){

            ArrayList<Integer> aux = new ArrayList<Integer>();


            //int qntdFramesNescesarios
            
            for(int i = 0; i < freeFrames.length; i++){
                if(freeFrames[i]){
                    aux.add(i);
                    freeFrames[i] = false;
                }
            }

            return true;
        }

        public void dislocate(int[] tablePages){
            for (int page : tablePages) {
                freeFrames[page] = true;
            }
        }

    }

    public InterruptHandling interruptHandling;
    private CPU cpu;
    private MemoryManager memoryManager;
    //private Memory memory;
    private KeyboardDriver keyboardDriver;
    private ConsoleOutputDriver consoleOutputDriver;

    public SOs(CPU _cpu, Memory _memory, int _pageLength){        
        interruptHandling = new InterruptHandling();
        memoryManager = new MemoryManager(_memory, _pageLength);
        _cpu.setInterruptHandling(interruptHandling);
        cpu = _cpu;
        loadDrivers();
    }
    
    public void loadProgram(int pc, Word[] program){

        int[] tablePages = new int[0];

        if(memoryManager.allocates(program.length, tablePages)){
           //carrega porgrama nos frames 
        }else{
            memoryManager.dislocate(tablePages);
        }

        /*
        for(int i=0; i < program.length; i++){
            memoryManager.memory.address[pc] = program[i];
            pc++;
        }
        */
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
        memoryManager.memory.address[cpu.getRegistrator(9)].opc = Opcode.DATA;
        memoryManager.memory.address[cpu.getRegistrator(9)].r1 = -1;
        memoryManager.memory.address[cpu.getRegistrator(9)].r2 = -1;
        memoryManager.memory.address[cpu.getRegistrator(9)].p = keyboardDriver.readKeyboardInput();
        cpu.itr = Interrupt.NoInterrupt; 
    }

    public void output(){
        consoleOutputDriver.systemOutInt(memoryManager.memory.address[cpu.getRegistrator(9)].p);
        cpu.itr = Interrupt.NoInterrupt; 
    }


/*

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

*/


}

