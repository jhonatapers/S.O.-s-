package Hardware;
import Hardware.Memory.Word;
import Software.SOs.InterruptHandling;

public class CPU {

    // ------ INSTRUCOES CPU ------
    public static enum Opcode {
		DATA, ___,		    // se memoria nesta posicao tem um dado, usa DATA, se nao usada ee NULO ___
		JMP, JMPI, JMPIG, JMPIL, JMPIE,  JMPIM, JMPIGM, JMPILM, JMPIEM, STOP,   // desvios e parada
		ADDI, SUBI,  ADD, SUB, MULT,         // matematicos
		LDI, LDD, STD,LDX, STX, SWAP;        // movimentacao
	}
    // ----------------------------

    // ------ INTERRUPCOES CPU ------
    public static enum Interrupt {
        NoInterrupt, InvalidAdress, InvalidInstruction, Overflow, ProgramEnd;
    }
    // ----------------------------

    private int pc;
    private Word ir;
    private int[] reg;
    private InterruptHandling ih;
    private Interrupt itr;

    private Memory m;
    //private Word[] m;

    public CPU(Memory memory, InterruptHandling interruptHandling){
        ih = interruptHandling;
        m = memory;
        reg = new int[8];
    }

    public void setContext(int _pc) {
        pc = _pc;                
    }

    public void run() { 		// execucao da CPU supoe que o contexto da CPU, vide acima, esta devidamente setado
        while (true) { 			// ciclo de instrucoes. acaba cfe instrucao, veja cada caso.
            itr = Interrupt.NoInterrupt;
            // FETCH
                ir = m.mem[pc]; 	// busca posicao da memoria apontada por pc, guarda em ir
                //if debug
                    //showState();
            // EXECUTA INSTRUCAO NO ir
                switch (ir.opc) { // para cada opcode, sua execução

                    case JMP: //  PC ← k
                            pc = ir.p;							
                         break;

                    case JMPI: // PC ← Rs
                            pc = reg[ir.r1];
                        break;					
                    
                    case JMPIG: // If Rc > 0 Then PC ← Rs Else PC ← PC +1
                        if (reg[ir.r2] > 0) {
                            pc = reg[ir.r1];
                        } else {
                            pc++;
                        }
                        break;

                    case JMPIL: //f Rc < 0 then PC ← Rs Else PC ← PC +1 
                        if (reg[ir.r2] < 0) {
                            pc = reg[ir.r1];
                        } else {
                            pc++;
                        }
                    break;

                    case JMPIE: // If Rc = 0 Then PC ← Rs Else PC ← PC +1
                        if (reg[ir.r2] == 0) {
                            pc = reg[ir.r1];
                        } else {
                            pc++;
                        }
                        break;
                    
                    case JMPIM: // PC ← [A]
                        pc = m.mem[ir.p].p;
                        break;
                    
                    case JMPIGM: //if Rc > 0 then PC ← [A] Else PC ← PC +1 
                            if (reg[ir.r2] > 0) {
                                pc = m.mem[ir.p].p;
                            } else {
                                pc++;
                            }
                        break;
                    
                    case JMPILM: //f Rc < 0 then PC ← [A] Else PC ← PC +1
                            if (reg[ir.r2] < 0) {
                                pc = m.mem[ir.p].p;
                            } else {
                                pc++;
                            }
                        break;
                    
                    case JMPIEM: //f Rc = 0 then PC ← [A] Else PC ← PC +1
                            if (reg[ir.r2] == 0) {
                                pc = m.mem[ir.p].p;
                            } else {
                                pc++;
                            }
                        break;						

                    case ADDI: // Rd ← Rd + k
                            reg[ir.r1] = reg[ir.r1] + ir.p;
                            pc++;
                        break;

                    case SUBI: // Rd ← Rd – k
                            reg[ir.r1] = reg[ir.r1] - ir.p;
                            pc++;
                        break;

                    case ADD: // Rd ← Rd + Rs
                            reg[ir.r1] = reg[ir.r1] + reg[ir.r2];
                            pc++;
                        break;

                    case SUB: // Rd ← Rd - Rs
                            reg[ir.r1] = reg[ir.r1] - reg[ir.r2];
                            pc++;
                        break;

                    case MULT: // Rd ← Rd * Rs
                            reg[ir.r1] = reg[ir.r1] * reg[ir.r2];
                            pc++;
                        break;

                    case LDI: // Rd ← k
                        reg[ir.r1] = ir.p;
                        pc++;
                        break;

                    case LDD: // Rd ← [A] //Conferir
                        reg[ir.r1] = m.mem[ir.p].p;
                        pc++;
                        break;
                                            
                    case STD: // [A] ← Rs     
                            m.mem[ir.p].opc = Opcode.DATA;
                            m.mem[ir.p].p = reg[ir.r1];
                            pc++;
                        break;

                    case LDX: // Rd ← [Rs] 
                            reg[ir.r1] = m.mem[reg[ir.r2]].p;
                            pc++;
                        break;

                    case STX: // [Rd] ←Rs
                            m.mem[reg[ir.r1]].opc = Opcode.DATA;      
                            m.mem[reg[ir.r1]].p = reg[ir.r2];          
                            pc++;
                        break;

                    case SWAP: // T ← Ra | Ra ← Rb | Rb ←T
                            int t = reg[ir.r1];
                            reg[ir.r1] = reg[ir.r2];
                            reg[ir.r2] = t;
                        break;
                    
                    case STOP: // por enquanto, para execucao
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
            
            // VERIFICA INTERRUPÇÃO
            if(itr != Interrupt.NoInterrupt){
                ih.handle(itr);
                break;
            }

        }
    }

}
