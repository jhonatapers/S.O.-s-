package Software;
import Hardware.CPU;
import Hardware.Memory;
//import Hardware.Memory.Word;
import Software.SistemaOperacional.SOs;

public class VirtualMachine {

	public CPU cpu;
	public Memory memory;
	public SOs sos;
	private final static int MEM_SIZE = 1024;
	private final static int PAGE_SIZE = 16;

    public VirtualMachine(CPU _cpu, Memory _memory){  
		cpu = _cpu;
		memory = _memory;
		sos = new SOs(_cpu, _memory, PAGE_SIZE);
    }

    public static void main(String args[]) {  	     
		CPU cpu = new CPU(new Memory(MEM_SIZE), PAGE_SIZE);	
		VirtualMachine vm = new VirtualMachine(cpu, cpu.m);

		/**
		 * Debug = Informar, no console, cada linha sendo executada pelo programa.
		 * true  = Debug ON
		 * false = Debug OFF
		 */
		vm.cpu.debug = false;

		//Carregando programas em memória.
		vm.sos.loadProgram(new Softwares().contadorInOut);// [10] //Carregando o programa em memória CONTADOR PROCESS ID [0]	
		vm.sos.loadProgram(new Softwares().contadorInOut);// [10] //Carregando o programa em memória CONTADOR PROCESS ID [0]
		vm.sos.loadProgram(new Softwares().contadorInOut);// [10] //Carregando o programa em memória CONTADOR PROCESS ID [0]
		vm.sos.loadProgram(new Softwares().contadorInOut);// [10] //Carregando o programa em memória CONTADOR PROCESS ID [0]
		//vm.sos.loadProgram(Softwares.ADD);// [20+3 => 23] //Carregando o programa em memória ADD PROCESS ID [2]
		//vm.sos.loadProgram(Softwares.SUB);// [30-32 => -2] //Carregando o programa em memória SUB PROCESS ID [3]
		//vm.sos.loadProgram(Softwares.PB);// [4 => 12] //Carregando o programa em memória de FATORIAL
		//vm.sos.loadProgram(Softwares.E1);// [12 => 12] [-1 => STOP (Interrupt ProgramEnd)] //Carregando o programa E1

		//Colocando o primeiro programa da fila no contexto do processador.
		vm.sos.loadNextProcess();

		//Executa o programa atual da CPU e os demais em fila, até encerrar todos.
		vm.cpu.run();
	}

	/*
	private static Word[] getProgram(int opt) {
		switch (opt) {
			case 1:
				return Softwares.PA;
			case 2:
				return Softwares.PB;
			case 3:
				return Softwares.BubbleSortAsc;
			case 4:
				return Softwares.contadorInOut;
			case 5:
				return Softwares.E1;
			case 6:
				return Softwares.ADD;
			case 7:
				return Softwares.MULT;
			case 8:
				return Softwares.SUB;
			case 9:
				return Softwares.E5;
			case 10:
				return Softwares.E6;
			case 11:
				return Softwares.fibonacci10;
			case 20: //debug
				return null;
			case 0: 
				System.out.println("Encerrando programa!");
				return null;
			default:
				System.out.println("Encerrando programa!");
				return null;
		}
	}
	*/
}
