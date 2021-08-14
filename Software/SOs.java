package Software;
import Hardware.CPU;
import Hardware.Memory;
import Hardware.CPU.Interrupt;
import Hardware.Memory.Word;

public class SOs {
    
    public static class InterruptHandling{
        
        public void handle(Interrupt itr){
            System.out.println(itr);
        }        

    }

    public InterruptHandling interruptHandling;
    private CPU cpu;
    private Memory memory;

    public SOs(CPU _cpu, Memory _memory){        
        interruptHandling = new InterruptHandling();
        cpu = _cpu;
        memory = _memory;
    }

    public void loadProgram(int pc, Word[] program){
        for(int i=0; i < program.length; i++){
            memory.address[pc] = program[i];
            pc++;
        }
    }

    public void runProgram(int pc){
        cpu.setContext(pc);
        cpu.run();
    }

}

