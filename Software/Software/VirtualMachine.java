package Software;
import Hardware.CPU;
import Hardware.Memory;
import Hardware.Memory.Word;
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

		vm.sos.loadProgram(getProgram(4));// [5] //Carregando o programa em memória MULT PROCESS ID [0]
		vm.sos.loadProgram(getProgram(7));// [4*3 => x] //Carregando o programa em memória CONTADOR PROCESS ID [1]
		//vm.sos.loadProgram(getProgram(6));// [20+5 => x] //Carregando o programa em memória ADD PROCESS ID [2]
		//vm.sos.loadProgram(getProgram(8));// [30-32 => x] //Carregando o programa em memória SUB PROCESS ID [3]

		vm.sos.loadNextProcess();
		vm.cpu.run();
		/*
		KeyboardDriver kDriver = new KeyboardDriver();
		
		int opt = 100;
		
		while(opt != 0){
			System.out.println("\nDigite o programa que deseja executar:");
			System.out.println("\t\tDEBUG: "+((vm.cpu.debug) ? "Ativado" : "Desativado"));
			System.out.println("\t1 - PA");
			System.out.println("\t2 - PB");
			System.out.println("\t3 - PC");
			System.out.println("\t4 - Contador");
			System.out.println("\t5 - E1");
			System.out.println("\t6 - ADD");
			System.out.println("\t7 - MULT");
			System.out.println("\t8 - SUB");
			System.out.println("\t9 - E5");
			System.out.println("\t10 - E6");
			System.out.println("\t0 - Encerrar execução");
			
			opt = kDriver.readKeyboardInput();
			
			System.out.println("----------------");
			if(vm.cpu.debug) //Modo DEBUG ON -> Estado anterior a execução
				vm.cpu.aux.dump(getProgram(opt), 0, getProgram(opt).length);
			System.out.println("----------------");

			vm.sos.loadProgram(getProgram(opt));//Carregando o programa em memória
			//vm.sos.runProgram(0, 0, getProgram(opt).length-1); //Executando o programa
			vm.sos.runNextProcess();

			System.out.println("----------------");
			if(vm.cpu.debug)//Modo DEBUG ON -> Estado posterior a execução
				vm.cpu.aux.dump(getProgram(opt), 0, getProgram(opt).length);
			System.out.println("----------------");

			cpu = new CPU(new Memory(MEM_SIZE), PAGE_SIZE);	//Limpando a memória para execução de um novo programa.
			vm = new VirtualMachine(cpu, cpu.m);
		}
		

		System.out.println("\n----------------");
		System.out.println("Fim da execução.");
		*/
	}

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
}
