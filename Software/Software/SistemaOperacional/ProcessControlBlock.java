package Software.SistemaOperacional;

import Hardware.CPU.Interrupt;

public class ProcessControlBlock {

    public static int[] processList; //0.1.2.3
    public int id;
    public Interrupt interrupt;
    public int[] tablePage; //Frames onde o programa foi alocado.
    public int pc;
    public int[] registrators;        

    public ProcessControlBlock(int[] _tablePage){
            this.interrupt = Interrupt.NoInterrupt;
            tablePage = _tablePage;
            pc = 0;
            registrators = new int[10];
    }
}
