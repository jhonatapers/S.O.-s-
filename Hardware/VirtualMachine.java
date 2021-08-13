package Hardware;

import Software.SOs.InterruptHandling;

public class VirtualMachine {

    private static int MEM_SIZE = 1024;
    private Memory memory;
    private CPU cpu;
    
    public VirtualMachine(InterruptHandling interruptHandling){  
        memory = new Memory(MEM_SIZE);
        cpu = new CPU(memory, interruptHandling);
    }

}
