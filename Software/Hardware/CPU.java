package Hardware;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

import Hardware.Memory.Word;
import Software.SistemaOperacional.ProcessControlBlock;
import Software.SistemaOperacional.ProcessControlBlock.ProcessState;
import Software.SistemaOperacional.SOs.InterruptHandling;

public class CPU extends Thread {

    // ------ INSTRUCOES CPU ------
    public static enum Opcode {
		DATA, ___,		    // se memoria nesta posicao tem um dado, usa DATA, se nao usada ee NULO ___
		JMP, JMPI, JMPIG, JMPIL, JMPIE,  JMPIM, JMPIGM, JMPILM, JMPIEM, STOP,   // desvios e parada
		ADDI, SUBI,  ADD, SUB, MULT,         // matematicos
		LDI, LDD, STD,LDX, STX, SWAP,        // movimentacao
        TRAP;                                // Chamada de sistema
	}
    // ----------------------------

    // ------ INTERRUPCOES CPU ------
    public static enum Interrupt {
        NoInterrupt, 
        InvalidInstruction, 
        InvalidAdress, 
        Overflow, 
        ProgramEnd,
        Trap,
        ClockInterrupt,
        IO,
        NoProcessRunning;
    }
    // ----------------------------


    
    public Aux aux = new Aux();

    private int pc;
    private int pageSize;
    private Word ir;
    private int[] reg;
    public InterruptHandling ih;
    public Interrupt itr = Interrupt.NoInterrupt;
    public Memory m;
    public Boolean debug = false;
    public ProcessControlBlock process;
    private int[] tablePage;


    private Semaphore sCPU;    

    private Semaphore sClockInterrupt;
    private Boolean clockInterrupt;

    private Semaphore sIOInterrupt;
    private Boolean iOInterrupt;


    private Semaphore sTrapInterrupt;
    private Boolean trapInterrupt;

    private Semaphore sProcessIOQueue;
    //private int idProcessIO;
    private Queue<Integer> processIOQueue;

    private static int MAX_CLOCK = 5;

    private int MAX;

    private int MIN;

    public CPU(Memory memory, int pageSize, int BASE, Semaphore sCPU){

		MAX = (int) (Math.pow(2, BASE) / 2) -1;
		MIN = (int) (Math.pow(2, BASE) / 2) * -1;

        sClockInterrupt = new Semaphore(1);
        clockInterrupt = false;

        sIOInterrupt = new Semaphore(1);
        iOInterrupt = false;

        sTrapInterrupt = new Semaphore(1);
        trapInterrupt = false;

        sProcessIOQueue = new Semaphore(1);
        processIOQueue = new LinkedList<Integer>();
        //idProcessIO = -1;

        m = memory;
        reg = new int[10];
        this.pageSize = pageSize;

        this.sCPU = sCPU;
    }

    //Seta Processo na CPU
    public void setProcess(ProcessControlBlock process) {
        this.process = process.clone();
        this.itr = process.interrupt;        
        this.reg = process.registrators.clone();
        this.pc = process.pc;
        this.tablePage = process.tablePage.clone();
    }

    public int getPC(){
        return pc;
    }

    public int getRegistrator(int r){
        return reg[r];
    }

    public int[] getRegistrators(){
        return reg.clone();
    }

    public void setInterruptHandling(InterruptHandling _ih){
        ih = _ih;
    }

    public void setIoInterrupt(Boolean value){        
        try { sIOInterrupt.acquire(); } 
        catch(InterruptedException ie) { }

        iOInterrupt = value;

        sIOInterrupt.release();
    }

    public Boolean getIoInterrupt(){        
        Boolean value;

        try { sIOInterrupt.acquire(); } 
        catch(InterruptedException ie) { }

        value = iOInterrupt;

        sIOInterrupt.release();

        return value;
    }

    public void setClockInterrupt(Boolean value){                
        try { sClockInterrupt.acquire(); } 
        catch(InterruptedException ie) { }

        clockInterrupt = value;

        sClockInterrupt.release();
    }

    public Boolean getClockInterrupt(){        
        Boolean value;

        try { sClockInterrupt.acquire(); } 
        catch(InterruptedException ie) { }

        value = clockInterrupt;

        sClockInterrupt.release();

        return value;
    }

    public void setTrapInterrupt(Boolean value){                
        try { sTrapInterrupt.acquire(); } 
        catch(InterruptedException ie) { }

        trapInterrupt = value;

        sTrapInterrupt.release();
    }

    public Boolean getTrapInterrupt(){            
        Boolean value;

        try { sTrapInterrupt.acquire(); } 
        catch(InterruptedException ie) { }

        value = trapInterrupt;

        sTrapInterrupt.release();

        return value;
    }

    public void setIDProcessIO(int idProcess) {
        try { sProcessIOQueue.acquire(); } 
        catch(InterruptedException ie) { }

        processIOQueue.add(idProcess);

        sProcessIOQueue.release();
    }

    public int getIDProcessIO() {

        int aux;

        try { sProcessIOQueue.acquire(); } 
        catch(InterruptedException ie) { }

        aux = processIOQueue.poll();

        sProcessIOQueue.release();

        return aux;
    }

    public boolean validAdress(int _pc){
        int page = _pc / pageSize;

        //Valida se o pc atual está dentro do conjunto de páginas pertencentes ao processo.
        for(int i = 0; i < process.tablePage.length; i++){
            if(process.tablePage[i] == page){
                return true;
            }
        }
        itr = Interrupt.InvalidAdress;
        return false;
    }

    private boolean overflow(int n){
        if(n < MIN || n > MAX){
            itr = Interrupt.Overflow;
            return true;
        }

        return false;
    }

    @Override
    public void run() {

        process = new ProcessControlBlock(-1, Interrupt.NoProcessRunning , new int[0], -1, new int[0], ProcessState.Blocked);

        int clock = 0;
        
        while(true){
        
            try { sCPU.acquire(); } 
            catch(InterruptedException ie) { }
            
            while (true) { 

                if(getIoInterrupt()){
                    process = new ProcessControlBlock(process.id, this.itr, process.tablePage, this.pc, this.reg.clone(), process.processState);            
                    setIoInterrupt(false); 
                    ih.handle(Interrupt.IO);
                }
                
                if(process.processState == ProcessState.Running){

                    if(validAdress(translateAddress(pc,tablePage))) 
                        ir = m.address[translateAddress(pc,tablePage)]; 
                
                    if(debug){ showState(); }
                
                
                    switch (ir.opc) {
                        case JMP: //  PC <- k       
                            if(validAdress(translateAddress(ir.p,tablePage)))                    
                                pc = ir.p;			                    
                            break;
        
                        case JMPI: // PC <- Rs
                            if(validAdress(translateAddress(reg[ir.r1],tablePage)))
                                pc = reg[ir.r1];
                            break;					
                            
                        case JMPIG: // If Rc > 0 Then PC <- Rs Else PC <- PC +1
                            if (reg[ir.r2] > 0) {
                                if(validAdress(translateAddress(reg[ir.r1],tablePage)))
                                    pc = reg[ir.r1];                      
                            } else {
                                pc++;
                            }
                            break;
        
                        case JMPIL: //f Rc < 0 then PC <- Rs Else PC <- PC +1 
                            if (reg[ir.r2] < 0) {
                                if(validAdress(translateAddress(reg[ir.r1],tablePage)))
                                    pc = reg[ir.r1];
                            } else {
                                pc++;
                            }
                            break;
        
                        case JMPIE: // If Rc = 0 Then PC <- Rs Else PC <- PC +1
                            if (reg[ir.r2] == 0) {
                                if(validAdress(translateAddress(reg[ir.r1],tablePage)))
                                    pc = reg[ir.r1];
                            } else {
                                pc++;
                            }
                            break;
                            
                        case JMPIM: // PC <- [A]
                            if(validAdress(translateAddress(m.address[translateAddress(ir.p,tablePage)].p,tablePage)))
                                pc = m.address[translateAddress(ir.p,tablePage)].p;
                            break;
                            
                        case JMPIGM: //if Rc > 0 then PC <- [A] Else PC <- PC +1 
                                if (reg[ir.r2] > 0) {
                                    if(validAdress(translateAddress(m.address[translateAddress(ir.p,tablePage)].p,tablePage)))
                                        pc = m.address[translateAddress(ir.p,tablePage)].p;
                                } else {
                                    pc++;
                                }
                                break;
                            
                        case JMPILM: //f Rc < 0 then PC <- [A] Else PC <- PC +1
                                if (reg[ir.r2] < 0) {
                                    if(validAdress(translateAddress(m.address[translateAddress(ir.p,tablePage)].p,tablePage))){
                                        pc = m.address[translateAddress(ir.p,tablePage)].p;
                                    }
                                } else {
                                    pc++;
                                }
                                break;
                            
                        case JMPIEM: //f Rc = 0 then PC <- [A] Else PC <- PC +1
                                if (reg[ir.r2] == 0) {
                                    if(validAdress(translateAddress(m.address[translateAddress(ir.p,tablePage)].p,tablePage)))
                                        pc = m.address[translateAddress(ir.p,tablePage)].p;
                                } else {
                                    pc++;
                                }
                                break;						
        
                        case ADDI: // Rd <- Rd + 
                                if(!overflow(reg[ir.r1] + ir.p)){
                                    reg[ir.r1] = reg[ir.r1] + ir.p;
                                    pc++;
                                }
                                break;
        
                        case SUBI: // Rd <- Rd - k 
                                if(!overflow(reg[ir.r1] - ir.p)){
                                    reg[ir.r1] = reg[ir.r1] - ir.p;
                                    pc++;
                                }
                                break;
        
                        case ADD: // Rd <- Rd + Rs
                                if(!overflow(reg[ir.r1] + reg[ir.r2])){
                                    reg[ir.r1] = reg[ir.r1] + reg[ir.r2];
                                    pc++;
                                }
                                break;
        
                        case SUB: // Rd <- Rd - Rs
                                if(!overflow(reg[ir.r1] - reg[ir.r2])){
                                    reg[ir.r1] = reg[ir.r1] - reg[ir.r2];
                                    pc++;
                                }
                                break;
        
                        case MULT: // Rd <- Rd * Rs
                                if(!overflow(reg[ir.r1] * reg[ir.r2])){
                                    reg[ir.r1] = reg[ir.r1] * reg[ir.r2];
                                    pc++;
                                }
                                break;
        
                        case LDI: // Rd <- k
                                if(!overflow(ir.p)){
                                    reg[ir.r1] = ir.p;
                                    pc++;
                                }                        
                                break;
        
                        case LDD: // Rd <- [A] 
                                if(validAdress(translateAddress(ir.p,tablePage))){
                                    reg[ir.r1] = m.address[translateAddress(ir.p,tablePage)].p;
                                    pc++;
                                }
                                break;
                                                    
                        case STD: // [A] <- Rs     
                                if(validAdress(translateAddress(ir.p,tablePage))){
                                    m.address[translateAddress(ir.p,tablePage)].opc = Opcode.DATA;
                                    m.address[translateAddress(ir.p,tablePage)].p = reg[ir.r1];
                                    pc++;
                                }
                                break;
        
                        case LDX: // Rd <- [Rs] 
                                if(validAdress(translateAddress(reg[ir.r2],tablePage))){
                                    reg[ir.r1] = m.address[translateAddress(reg[ir.r2],tablePage)].p;
                                    pc++;
                                }
                                break;
        
                        case STX: // [Rd] <-Rs
                                if(validAdress(translateAddress(reg[ir.r1],tablePage))){
                                    m.address[translateAddress(reg[ir.r1],tablePage)].opc = Opcode.DATA;      
                                    m.address[translateAddress(reg[ir.r1],tablePage)].p = reg[ir.r2];          
                                    pc++;
                                }
                                break;
        
                        case SWAP: // T <- Ra | Ra <- Rb | Rb <-T
                                int t = reg[ir.r1];
                                reg[ir.r1] = reg[ir.r2];
                                reg[ir.r2] = t;
                                pc++;
                                break;
                            
                        case TRAP:
                                //itr = Interrupt.Trap;                
                                setTrapInterrupt(true);
                                pc++;
                                break;
                            
                        case STOP:
                                itr = Interrupt.ProgramEnd;
                                break;	
        
                        case DATA:
                                itr = Interrupt.InvalidInstruction;
                                break;
        
                        case ___:
                                itr = Interrupt.InvalidInstruction;
                                break;
        
                        default:
                                itr = Interrupt.InvalidInstruction;
                                break;
        
                    }

                    //clock++;
                    if(clock++ == MAX_CLOCK){ 
                        this.setClockInterrupt(true); 
                        clock = 0;
                    }

                    if (getTrapInterrupt()){
                        process = new ProcessControlBlock(process.id, Interrupt.NoInterrupt, process.tablePage, this.pc, this.reg.clone(), process.processState);
                        setTrapInterrupt(false);
                        ih.handle(Interrupt.Trap);
                        break;
                    }

                    if(getClockInterrupt()){
                        process = new ProcessControlBlock(process.id, this.itr, process.tablePage, this.pc, this.reg.clone(), process.processState);                
                        setClockInterrupt(false);
                        itr = Interrupt.ClockInterrupt;
                        ih.handle(Interrupt.ClockInterrupt);
                        break;
                    }
                    
                    if(itr != Interrupt.NoInterrupt){     
                        ih.handle(itr);
                        break; 
                    }

                }else{
                    ih.handle(Interrupt.NoProcessRunning);
                    break;
                }

            }
        }


    }

    //Traduz um dado PC para a posição exata da memória onde a Word se encontra.
    public int translateAddress(int pc, int[] tablePage){
        int page = pc / pageSize; 
        int offset = pc % pageSize;
        int frame  = tablePage[page] * pageSize; //Converte a pagina do pc atual na posição real da memoria.

        int position = frame + offset; //Posição exata no frame da memoria.

        return position;
    }

    //#region AUx
    public void showState(){
        System.out.println("       "+ pc); 
          System.out.print("           ");
        for (int i=0; i<10; i++) { System.out.print("r"+i);   System.out.print(": "+reg[i]+"     "); };  
        System.out.println("");
        System.out.print("           ");  aux.dump(ir);
    }

    public class Aux {
        public void dump(Word w) {
            System.out.print("[ "); 
            System.out.print(w.opc); System.out.print(", ");
            System.out.print(w.r1);  System.out.print(", ");
            System.out.print(w.r2);  System.out.print(", ");
            System.out.print(w.p);  System.out.println("  ] ");
        }
        public void dump(Word[] m, int ini, int fim) {
            for (int i = ini; i < fim; i++) {
                System.out.print(i); System.out.print(":  ");  dump(m[i]);
            }
        }
        public void carga(Word[] p, Word[] m) {
            for (int i = 0; i < p.length; i++) {
                m[i].opc = p[i].opc;     m[i].r1 = p[i].r1;     m[i].r2 = p[i].r2;     m[i].p = p[i].p;
            }
        }
    }
    //#endregion

}


