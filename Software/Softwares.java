package Software;

import Hardware.CPU.Opcode;
import Hardware.Memory.Word;

public class Softwares {

    public static Word[] progMinimo = new Word[] {
        new Word(Opcode.LDI, 0, -1, 999),
        new Word(Opcode.STD, 0, -1, 10),
        new Word(Opcode.STD, 0, -1, 11),
        new Word(Opcode.STD, 0, -1, 12),
        new Word(Opcode.STD, 0, -1, 13),
        new Word(Opcode.STD, 0, -1, 14),
        new Word(Opcode.STOP, -1, -1, -1) 
    };
        
    public static Word[] fibonacci10 = new Word[] { // mesmo que prog exemplo, so que usa r0 no lugar de r8
        new Word(Opcode.LDI, 1, -1, 0), 
		new Word(Opcode.STD, 1, -1, 20),    // 20 posicao de memoria onde inicia a serie de fibonacci gerada  
		new Word(Opcode.LDI, 2, -1, 1),
		new Word(Opcode.STD, 2, -1, 21),      
		new Word(Opcode.LDI, 0, -1, 22),       
		new Word(Opcode.LDI, 6, -1, 6),
		new Word(Opcode.LDI, 7, -1, 30),       
		new Word(Opcode.LDI, 3, -1, 0), 
		new Word(Opcode.ADD, 3, 1, -1),
		new Word(Opcode.LDI, 1, -1, 0), 
		new Word(Opcode.ADD, 1, 2, -1), 
		new Word(Opcode.ADD, 2, 3, -1),
		new Word(Opcode.STX, 0, 2, -1), 
		new Word(Opcode.ADDI, 0, -1, 1), 
		new Word(Opcode.SUB, 7, 0, -1),
		new Word(Opcode.JMPIG, 6, 7, -1), 
		new Word(Opcode.STOP, -1, -1, -1),   // POS 16
		new Word(Opcode.DATA, -1, -1, -1),
		new Word(Opcode.DATA, -1, -1, -1),
		new Word(Opcode.DATA, -1, -1, -1),
		new Word(Opcode.DATA, -1, -1, -1),   // POS 20
		new Word(Opcode.DATA, -1, -1, -1),
		new Word(Opcode.DATA, -1, -1, -1),
		new Word(Opcode.DATA, -1, -1, -1),
		new Word(Opcode.DATA, -1, -1, -1),
		new Word(Opcode.DATA, -1, -1, -1),
		new Word(Opcode.DATA, -1, -1, -1),
		new Word(Opcode.DATA, -1, -1, -1),
		new Word(Opcode.DATA, -1, -1, -1),
		new Word(Opcode.DATA, -1, -1, -1)  // ate aqui - serie de fibonacci ficara armazenada
	};   

    public static Word[] fatorial = new Word[] { 	 // este fatorial so aceita valores positivos.   nao pode ser zero
        new Word(Opcode.LDI, 0, -1, 6),      // 0   	r0 é valor a calcular fatorial
        new Word(Opcode.LDI, 1, -1, 1),      // 1   	r1 é 1 para multiplicar (por r0)
        new Word(Opcode.LDI, 6, -1, 1),      // 2   	r6 é 1 para ser o decremento
        new Word(Opcode.LDI, 7, -1, 8),      // 3   	r7 tem posicao de stop do programa = 8
        new Word(Opcode.JMPIE, 7, 0, 0),     // 4   	se r0=0 pula para r7(=8)
        new Word(Opcode.MULT, 1, 0, -1),     // 5   	r1 = r1 * r0
        new Word(Opcode.SUB, 0, 6, -1),      // 6   	decrementa r0 1 
        new Word(Opcode.JMP, -1, -1, 4),     // 7   	vai p posicao 4
        new Word(Opcode.STD, 1, -1, 10),     // 8   	coloca valor de r1 na posição 10
        new Word(Opcode.STOP, -1, -1, -1),   // 9   	stop
        new Word(Opcode.DATA, -1, -1, -1)    // 10      ao final o valor do fatorial estará na posição 10 da memória      
    };  

    public static Word[] contador = new Word[] {		
        new Word(Opcode.LDI, 0, -1, 0),     //r0 <- 0
        new Word(Opcode.STD, 0, -1, 14),    //A <- r0 

        new Word(Opcode.LDI, 0, -1, 50),    //r0 <- 50
        new Word(Opcode.STD, 0, -1, 15),    //B <- r0 

        new Word(Opcode.LDI, 0, -1, 6), 	//r0 <- 6
        new Word(Opcode.STD, 0, -1, 16),    //Loop <- r0


        new Word(Opcode.LDD, 0, -1, 14),    //r0 <- A 
        new Word(Opcode.LDD, 1, -1, 15),    //r1 <- B 
        new Word(Opcode.SUB, 0, 1, -1),     //r0 <- r0 - r1
     
        new Word(Opcode.LDD, 2, -1, 14),    //r2 <- A 
        new Word(Opcode.ADDI, 2, -1, 1),    //r2 <- r2 + 1
        new Word(Opcode.STD, 2, -1, 14),    //A <- r2 

        new Word(Opcode.JMPILM, -1, 0, 16), 

        new Word(Opcode.STOP, -1, -1, -1),

        new Word(Opcode.DATA, -1, -1, -1),  //A 0     14
        new Word(Opcode.DATA, -1, -1, -1),  //B 50    15
        new Word(Opcode.DATA, -1, -1, -1)   //Loop    16        
    };

}
