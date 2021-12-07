package Hardware;
import java.util.concurrent.Semaphore;

import Hardware.Memory.Word;
import Software.SistemaOperacional.ProcessControlBlock;
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
        IO;
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

    private Semaphore sIdProcessIO;
    private int idProcessIO;

    private static int MAX_CLOCK = 500;

    public CPU(Memory memory, int pageSize, Semaphore sCPU){

        sClockInterrupt = new Semaphore(1);
        clockInterrupt = false;

        sIOInterrupt = new Semaphore(1);
        iOInterrupt = false;

        sIdProcessIO = new Semaphore(1);
        idProcessIO = -1;

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

    public void setIoInterrupt(){        
        try { sIOInterrupt.acquire(); } 
        catch(InterruptedException ie) { }

        iOInterrupt = true;

        sIOInterrupt.release();
    }

    public void setIDProcessIO(int idProcess) {
        try { sIdProcessIO.acquire(); } 
        catch(InterruptedException ie) { }

        idProcessIO = idProcess;

        sIdProcessIO.release();
    }

    public int getIDProcessIO() {

        int aux;

        try { sIdProcessIO.acquire(); } 
        catch(InterruptedException ie) { }

        aux = idProcessIO;

        sIdProcessIO.release();

        return aux;
    }

    public void setClockInterrupt(){        
        try { sClockInterrupt.acquire(); } 
        catch(InterruptedException ie) { }

        clockInterrupt = true;

        sClockInterrupt.release();
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
        if(n < -32768 || n > 32767){
            itr = Interrupt.Overflow;
            return true;
        }

        return false;
    }

    @Override
    public void run() {

        int clock = 0;

        while(true){

            try { sCPU.acquire(); } 
            catch(InterruptedException ie) { }
            
            while (true) {  
                
                clock++;
                if(clock == MAX_CLOCK){
                    this.setClockInterrupt();
                }

                if(validAdress(translateAddress(pc))) 
                    ir = m.address[translateAddress(pc)]; 
                
                if(debug){ showState(); }
                
                switch (ir.opc) {
                    case JMP: //  PC <- k       
                        if(validAdress(translateAddress(ir.p)))                    
                            pc = ir.p;			                    
                        break;
    
                    case JMPI: // PC <- Rs
                        if(validAdress(translateAddress(reg[ir.r1])))
                            pc = reg[ir.r1];
                        break;					
                        
                    case JMPIG: // If Rc > 0 Then PC <- Rs Else PC <- PC +1
                        if (reg[ir.r2] > 0) {
                            if(validAdress(translateAddress(reg[ir.r1])))
                                pc = reg[ir.r1];                      
                        } else {
                            pc++;
                        }
                        break;
    
                    case JMPIL: //f Rc < 0 then PC <- Rs Else PC <- PC +1 
                        if (reg[ir.r2] < 0) {
                            if(validAdress(translateAddress(reg[ir.r1])))
                                pc = reg[ir.r1];
                        } else {
                            pc++;
                        }
                        break;
    
                    case JMPIE: // If Rc = 0 Then PC <- Rs Else PC <- PC +1
                        if (reg[ir.r2] == 0) {
                            if(validAdress(translateAddress(reg[ir.r1])))
                                pc = reg[ir.r1];
                        } else {
                            pc++;
                        }
                        break;
                        
                    case JMPIM: // PC <- [A]
                        if(validAdress(translateAddress(m.address[translateAddress(ir.p)].p)))
                            pc = m.address[translateAddress(ir.p)].p;
                        break;
                        
                    case JMPIGM: //if Rc > 0 then PC <- [A] Else PC <- PC +1 
                            if (reg[ir.r2] > 0) {
                                if(validAdress(translateAddress(m.address[translateAddress(ir.p)].p)))
                                    pc = m.address[translateAddress(ir.p)].p;
                            } else {
                                pc++;
                            }
                            break;
                        
                    case JMPILM: //f Rc < 0 then PC <- [A] Else PC <- PC +1
                            if (reg[ir.r2] < 0) {
                                if(validAdress(translateAddress(m.address[translateAddress(ir.p)].p))){
                                    pc = m.address[translateAddress(ir.p)].p;
                                }
                            } else {
                                pc++;
                            }
                            break;
                        
                    case JMPIEM: //f Rc = 0 then PC <- [A] Else PC <- PC +1
                            if (reg[ir.r2] == 0) {
                                if(validAdress(translateAddress(m.address[translateAddress(ir.p)].p)))
                                    pc = m.address[translateAddress(ir.p)].p;
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
                            if(validAdress(translateAddress(ir.p))){
                                reg[ir.r1] = m.address[translateAddress(ir.p)].p;
                                pc++;
                            }
                            break;
                                                
                    case STD: // [A] <- Rs     
                            if(validAdress(translateAddress(ir.p))){
                                m.address[translateAddress(ir.p)].opc = Opcode.DATA;
                                m.address[translateAddress(ir.p)].p = reg[ir.r1];
                                pc++;
                            }
                            break;
    
                    case LDX: // Rd <- [Rs] 
                            if(validAdress(translateAddress(reg[ir.r2]))){
                                reg[ir.r1] = m.address[translateAddress(reg[ir.r2])].p;
                                pc++;
                            }
                            break;
    
                    case STX: // [Rd] <-Rs
                            if(validAdress(translateAddress(reg[ir.r1]))){
                                m.address[translateAddress(reg[ir.r1])].opc = Opcode.DATA;      
                                m.address[translateAddress(reg[ir.r1])].p = reg[ir.r2];          
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
                            itr = Interrupt.Trap;                
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
    
                //////////// Verificar essa parte///////////////////
                boolean interrupted = false;

                try { sIOInterrupt.acquire(); } 
                catch(InterruptedException ie) { }

                if(iOInterrupt){
                    process = new ProcessControlBlock(process.id, this.itr, process.tablePage, this.pc, this.reg.clone());            
                    iOInterrupt = false;
                    //interrupted = true;
                    //itr = Interrupt.IO;
                    ih.handle(Interrupt.IO);
                }

                sIOInterrupt.release();

                try { sClockInterrupt.acquire(); } 
                catch(InterruptedException ie) { }

                if(clockInterrupt){
                    process = new ProcessControlBlock(process.id, this.itr, process.tablePage, this.pc, this.reg.clone());
                    clock = 0;
                    clockInterrupt = false;
                    interrupted = true;
                    itr = Interrupt.ClockInterrupt;
                    //ih.handle(Interrupt.ClockInterrupt);
                }

                sClockInterrupt.release();

                if(itr != Interrupt.NoInterrupt){                                
                    System.out.println("PROCESS ID ["+process.id+"]" +" PC ["+this.pc+"]" +" INTERRUPT ["+this.itr+"]" );
                    interrupted = true;
                    ih.handle(itr);
                }

                if(interrupted){
                    break;                  
                }
                
                ///////////////////////////
            }
        }


    }

    //Traduz um dado PC para a posição exata da memória onde a Word se encontra.
    public int translateAddress(int pc){
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


