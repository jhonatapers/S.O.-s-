package Software.SistemaOperacional;

import Hardware.CPU.Interrupt;

public class ProcessControlBlock {

    // ------ States Process ------
    public static enum State {
        Running, 
        Ready, 
        Block;
    }
    // ----------------------------

    public static int processCount = 0; //0.1.2.3
    public int id;
    public Interrupt interrupt;
    public int[] tablePage; //Frames onde o programa foi alocado.
    public int pc;
    public int[] registrators;      
    public State state;

    public ProcessControlBlock(int[] _tablePage){
            this.interrupt = Interrupt.NoInterrupt;
            tablePage = _tablePage;
            pc = 0;
            registrators = new int[10];
            id = processCount++;
    }

    public ProcessControlBlock(int id, Interrupt interrupt, int[] tablePage, int pc, int[] registrators){
        this.id = id;
        this.interrupt = interrupt;
        this.tablePage = tablePage;
        this.pc= pc;
        this.registrators = registrators;
     }

    //Retorna estado atual do PCB em nova instancia.
    public ProcessControlBlock clone(){
        ProcessControlBlock process = new ProcessControlBlock(this.id, this.interrupt, this.tablePage.clone(), this.pc, this.registrators.clone());
        return process;
    }
}
