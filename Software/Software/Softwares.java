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
    
    //PA
	public static Word[] PA = new Word[] { // mesmo que prog exemplo, so que usa r0 no lugar de r8
           
        new Word(Opcode.LDI, 3, -1, 22),    //r3 <- -10
        new Word(Opcode.STD, 3, -1, 36),     //A <- r0 (Definir EndereÃ§o)

        new Word(Opcode.LDI, 3, -1, 22),    //r3 <- 22
        new Word(Opcode.STD, 3, -1, 37),     //Stop <- r0 (Definir EndereÃ§o)
     
        new Word(Opcode.LDD, 3, -1, 36), //r3 = -10
     
        new Word(Opcode.JMPILM, -1, 3 ,37),
     
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
        new Word(Opcode.STOP, -1, -1, -1),   // POS 22
        new Word(Opcode.DATA, -1, -1, -1),   // -1
        new Word(Opcode.DATA, -1, -1, -1),
        new Word(Opcode.DATA, -1, -1, -1),
        new Word(Opcode.DATA, -1, -1, -1),   // POS 26
        new Word(Opcode.DATA, -1, -1, -1),
        new Word(Opcode.DATA, -1, -1, -1),
        new Word(Opcode.DATA, -1, -1, -1),
        new Word(Opcode.DATA, -1, -1, -1),
        new Word(Opcode.DATA, -1, -1, -1),
        new Word(Opcode.DATA, -1, -1, -1),
        new Word(Opcode.DATA, -1, -1, -1),
        new Word(Opcode.DATA, -1, -1, -1),
        new Word(Opcode.DATA, -1, -1, -1),  // ate aqui - serie de fibonacci ficara armazenada
                 
        new Word(Opcode.DATA, -1, -1, -10), //A 10
        new Word(Opcode.DATA, -1, -1, 22)  //Stop
             
    };
    
    /**
     * Pede um Int16 de entrada, se maior ou igual a zero calcula e imprime seu fatorial
    */
    public static Word[] PB = new Word[] { 
        new Word(Opcode.LDI, 8, -1, 1),      //0 Input
        new Word(Opcode.TRAP, -1, -1, -1),   //1 Input
        new Word(Opcode.SWAP, 9, 0, -1),     //2    
        new Word(Opcode.LDI, 1, -1, -1),     //3 r1 é -1 
        new Word(Opcode.LDI, 7, -1, 13),     //4 r7 tem posicao de stop do programa = 8
        new Word(Opcode.JMPIL, 7, 0, 0),     //5 Se r0<0 pula para r7(=8)
        new Word(Opcode.LDI, 1, -1, 1),      //6 r1 é 1 para multiplicar (por r0)
        new Word(Opcode.LDI, 6, -1, 1),      //7 r6 é 1 para ser o decremento
        new Word(Opcode.LDI, 7, -1, 13),     //8 r7 tem posicao de stop do programa = 8
        new Word(Opcode.JMPIE, 7, 0, 0),     //9 Se r0=0 pula para r7(=8)
        new Word(Opcode.MULT, 1, 0, -1),     //10 r1 = r1 * r0
        new Word(Opcode.SUB, 0, 6, -1),      //11 Decrementa r0 1 
        new Word(Opcode.JMP, -1, -1, 9),     //12 Vai p posicao 4        
        new Word(Opcode.STD, 1, -1, 18),     //13 Coloca valor de r1 na posição 10
        new Word(Opcode.LDD, 9, -1, 18),     //14 
        new Word(Opcode.LDI, 8, -1, 2),      //15 Output
        new Word(Opcode.TRAP, -1, -1, -1),   //16 Output
        new Word(Opcode.STOP, -1, -1, -1),   //17 Stop
        new Word(Opcode.DATA, -1, -1, -1)    //18 ao final o valor do fatorial estará na posição 18 da memória                                    
    };  

    /**
     * Recebe um Int16 e faz um for printando até ele +1
     */
    public  static  Word [] contadorInOut =  new  Word [] {
        new Word(Opcode.LDI, 0, -1, 0),       //0 r0 <- 0      
        new Word(Opcode.STD, 0, -1, 18),      //1 A <- r0
        new Word(Opcode.LDI, 8, -1, 1),       //2 Input
        new Word(Opcode.TRAP, -1, -1, -1),    //3 Input
        new Word(Opcode.STD, 9, -1, 19),      //4 B <- r0
        new Word(Opcode.LDI, 0, -1 , 7),      //5 r0 <- 6
        new Word(Opcode.STD, 0, -1, 20),      //6 Loop <- r0
        new Word(Opcode.LDD, 0, -1, 18),      //7 r0 <- A
        new Word(Opcode.LDD, 1, -1 , 19),     //8 r1 <- B
        new Word(Opcode.SUB, 0, 1, -1),       //9 r0 <- r0 - r1
        new Word(Opcode.LDD, 2, - 1, 18),     //10 r2 <- A
        new Word(Opcode.ADDI, 2, -1, 1),      //11 r2 <- r2 + 1
        new Word(Opcode.STD, 2, -1, 18),      //12 A <- r2
        new Word(Opcode.LDD, 9, -1, 18),      //13
        new Word(Opcode.LDI, 8, -1, 2),       //14 Output
        new Word(Opcode.TRAP, -1, -1, -1),    //15 Output
        new Word(Opcode.JMPILM, -1, 0, 20),   //16
        new Word(Opcode.STOP, -1, -1, -1),    //17
        new Word(Opcode.DATA, -1, -1, -1),    //18 A 0 18
        new Word(Opcode.DATA, -1, -1, -1),    //19 B 50 19
        new Word(Opcode.DATA, -1, -1, -1)     //20 Loop 20
    };

    /** 
     * Recebe um int16 
     * Se INPUT < 0 
     *  STOP 
     * else 
     *  OUTPUT Entrada
     */
    public static Word[] E1 = new Word[]{
        new Word(Opcode.LDI, 8, -1, 1),       //0  Input
        new Word(Opcode.TRAP, -1, -1, -1),    //1  Input
        new Word(Opcode.STD, 9, -1, 11),      //2  A <- r9
        new Word(Opcode.LDI, 3, -1, 10),      //3  r3   <- 15
        new Word(Opcode.STD, 3, -1, 12),      //4  Stop <- r3 
        new Word(Opcode.LDD, 2, -1, 11),      //5  r2 <- A
        new Word(Opcode.JMPILM, -1, 2, 12),   //6  se for menor que 0 então Stop
        new Word(Opcode.LDI, 8, -1, 2),       //7  Output
        new Word(Opcode.LDD, 9, -1, 11),      //8  r9 <- A
        new Word(Opcode.TRAP, -1, -1, -1),    //9  Output
        new Word(Opcode.STOP, -1, -1, -1),    //10 
        new Word(Opcode.DATA, -1, -1, 10),    //11 A
        new Word(Opcode.DATA, -1, -1, 6)      //12 STOP
    };
 
    /**
     * Pede dois Int16 de entrada e imprime a soma
     */
    public static Word[] ADD = new Word[]{
        new Word(Opcode.LDI, 8, -1, 1),      //0 Input
        new Word(Opcode.TRAP, -1, -1, -1),   //1 Input
        new Word(Opcode.STD, 9, -1, 14),     //2 A <- r9
        new Word(Opcode.LDI, 8, -1, 1),      //3 Input
        new Word(Opcode.TRAP, -1, -1, -1),   //4 Input
        new Word(Opcode.STD, 9, -1, 15),     //5 A <- r9
        new Word(Opcode.LDD, 1, -1, 14),     //6 r1 <- A
        new Word(Opcode.LDD, 2, -1, 15),     //7 r2 <- B
        new Word(Opcode.ADD, 1, 2, -1),      //8 r1 <- r1 + r2
        new Word(Opcode.STD, 1, -1, 16),     //9
        new Word(Opcode.LDI, 8, -1, 2),      //10  Output
        new Word(Opcode.LDD, 9, -1, 16),     //11  r9 <- A
        new Word(Opcode.TRAP, -1, -1, -1),   //12  Output
        new Word(Opcode.STOP, -1, -1, -1),   //13
        new Word(Opcode.DATA, -1, -1, -1),   //14 A  50
        new Word(Opcode.DATA, -1, -1, -1),   //15 B 100
        new Word(Opcode.DATA, -1, -1, -1)    //16 Result
    };

    /**
     * Pede dois Int16 de entrada e imprime a multiplicação
    */
    public static Word[] MULT = new Word[]{
        new Word(Opcode.LDI, 8, -1, 1),      //0 Input
        new Word(Opcode.TRAP, -1, -1, -1),   //1 Input
        new Word(Opcode.STD, 9, -1, 14),     //2 A <- r9
        new Word(Opcode.LDI, 8, -1, 1),      //3 Input
        new Word(Opcode.TRAP, -1, -1, -1),   //4 Input
        new Word(Opcode.STD, 9, -1, 15),     //5 A <- r9
        new Word(Opcode.LDD, 1, -1, 14),     //6 r1 <- A
        new Word(Opcode.LDD, 2, -1, 15),     //7 r2 <- B
        new Word(Opcode.MULT, 1, 2, -1),     //8 r1 <- r1 * r2
        new Word(Opcode.STD, 1, -1, 16),     //9
        new Word(Opcode.LDI, 8, -1, 2),      //10  Output
        new Word(Opcode.LDD, 9, -1, 16),     //11  r9 <- A
        new Word(Opcode.TRAP, -1, -1, -1),   //12  Output
        new Word(Opcode.STOP, -1, -1, -1),   //13
        new Word(Opcode.DATA, -1, -1, -1),   //14 A  50
        new Word(Opcode.DATA, -1, -1, -1),   //15 B 100
        new Word(Opcode.DATA, -1, -1, -1)    //16 Result
    };

    /**
     * Pede dois Int16 de entrada e imprime a subtração
    */
    public static Word[] SUB = new Word[]{
        new Word(Opcode.LDI, 8, -1, 1),      //0 Input
        new Word(Opcode.TRAP, -1, -1, -1),   //1 Input
        new Word(Opcode.STD, 9, -1, 14),     //2 A <- r9
        new Word(Opcode.LDI, 8, -1, 1),      //3 Input
        new Word(Opcode.TRAP, -1, -1, -1),   //4 Input
        new Word(Opcode.STD, 9, -1, 15),     //5 A <- r9
        new Word(Opcode.LDD, 1, -1, 14),     //6 r1 <- A
        new Word(Opcode.LDD, 2, -1, 15),     //7 r2 <- B
        new Word(Opcode.SUB, 1, 2, -1),      //8 r1 <- r1 - r2
        new Word(Opcode.STD, 1, -1, 16),     //9
        new Word(Opcode.LDI, 8, -1, 2),      //10  Output
        new Word(Opcode.LDD, 9, -1, 16),     //11  r9 <- A
        new Word(Opcode.TRAP, -1, -1, -1),   //12  Output
        new Word(Opcode.STOP, -1, -1, -1),   //13
        new Word(Opcode.DATA, -1, -1, -1),   //14 A  50
        new Word(Opcode.DATA, -1, -1, -1),   //15 B 100
        new Word(Opcode.DATA, -1, -1, -1)    //16 Result
    };

    public static Word[] E5 = new Word[]{
        new Word(Opcode.LDI, 8, -1,  1),     //0 Input
        new Word(Opcode.TRAP,-1, -1,-1),     //1 Input
        new Word(Opcode.STD, 9, -1, 11),     //2 
        new Word(Opcode.LDI, 3, -1, 10),     //3
        new Word(Opcode.STD, 3, -1, 12),     //4
        new Word(Opcode.LDD, 2, -1, 11),     //5
        new Word(Opcode.JMPIGM,-1,2,12),     //6 se for maior que 0 então Stop
        new Word(Opcode.LDI, 8, -1,  2),     //7 Output
        new Word(Opcode.LDD, 9, -1, 11),     //8 r2 <- A
        new Word(Opcode.TRAP,-1, -1,-1),     //9 Output
        new Word(Opcode.STOP,-1,-1, -1),     //10 
        new Word(Opcode.DATA,-1,-1, -1),     //11 A
        new Word(Opcode.DATA,-1, -1, -1)     //12 Stop
    };

    /** 
     * Recebe um int16 
     * Se INPUT = 0 
     *  STOP 
     * else 
     *  OUTPUT Entrada
     */
    public static Word[] E6 = new Word[]{
        new Word(Opcode.LDI, 8, -1,  1),     //0 Input
        new Word(Opcode.TRAP,-1, -1,-1),     //1 Input
        new Word(Opcode.STD, 9, -1, 11),     //2 
        new Word(Opcode.LDI, 3, -1, 10),     //3
        new Word(Opcode.STD, 3, -1, 12),     //4
        new Word(Opcode.LDD, 2, -1, 11),     //5
        new Word(Opcode.JMPIEM,-1,2,12),     //6 se for maior que 0 então Stop
        new Word(Opcode.LDI, 8, -1,  2),     //7 Output
        new Word(Opcode.LDD, 9, -1, 11),     //8 r2 <- A
        new Word(Opcode.TRAP,-1, -1,-1),     //9 Output
        new Word(Opcode.STOP,-1,-1, -1),     //10 
        new Word(Opcode.DATA,-1,-1, -1),     //11 A
        new Word(Opcode.DATA,-1, -1, -1)     //12 Stop
    };

    /**
     * Bubble Sort Ascendente para 5 numeros inteiros.
     * Incompleto, até o momento está apenas lendo 5 ints e botando em memória.
     */
    public static Word[] BubbleSortAsc = new Word[]{
        new Word(Opcode.LDI, 0, -1, 0),        //0    
        new Word(Opcode.LDI, 0, -1, 0),        //1 
        
        new Word(Opcode.LDI, 8, -1, 1),        //2 Input A
        new Word(Opcode.TRAP, -1, -1, -1),     //3 Lendo numero do teclado
        new Word(Opcode.STD, 9, -1, 22),       //4 Armazenando valor lido do teclado na memória

        new Word(Opcode.LDI, 8, -1, 1),        //5
        new Word(Opcode.TRAP, -1, -1, -1),     //6 Input B
        new Word(Opcode.STD, 9, -1, 23),       //7 
             
        new Word(Opcode.LDI, 8, -1, 1),        //8
        new Word(Opcode.TRAP, -1, -1, -1),     //9 Input C
        new Word(Opcode.STD, 9, -1, 24),       //10 

        new Word(Opcode.LDI, 8, -1, 1),        //11 
        new Word(Opcode.TRAP, -1, -1, -1),     //12 Input D
        new Word(Opcode.STD, 9, -1, 25),       //13 

        new Word(Opcode.LDI, 8, -1, 1),        //14 
        new Word(Opcode.TRAP, -1, -1, -1),     //15 Input E
        new Word(Opcode.STD, 9, -1, 26),       //16

        //FAZER DIRETO SWAP
        new Word(Opcode.LDD, 3, -1, 22),
        new Word(Opcode.LDD, 4, -1, 23),
        new Word(Opcode.SUB, 3, 4, -1),
        new Word(Opcode.JMPILM, -1, 3, 17), // foi para o 22



        new Word(Opcode.STOP, -1, -1, -1),     //21

        new Word(Opcode.DATA, -1, -1, -1),     //18 A
        new Word(Opcode.DATA, -1, -1, -1),     //19 B
        new Word(Opcode.DATA, -1, -1, -1),     //20 C
        new Word(Opcode.DATA, -1, -1, -1),     //21 D 
        new Word(Opcode.DATA, -1, -1, -1),     //22 E
        new Word(Opcode.DATA, -1,-1,-1),       //23 Controle de loop (i)
        new Word(Opcode.DATA, -1,-1,-1)        //24 Controle de loop (j)
    };  
}

