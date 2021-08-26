package Software;

import Hardware.CPU;
import Hardware.Memory;
import Hardware.Memory.Word;
import Software.SistemaOperacional.SOs;
import Software.SistemaOperacional.Drivers.KeyboardDriver;

public class VirtualMachine {

	public CPU cpu;
	public Memory memory;
	public SOs sos;
	private static int MEM_SIZE = 1024;

    public VirtualMachine(CPU _cpu, Memory _memory){  
		cpu = _cpu;
		memory = _memory;
		sos = new SOs(_cpu, _memory);
    }

    public static void main(String args[]) {   		     
		CPU cpu = new CPU(new Memory(MEM_SIZE));	

		VirtualMachine vm = new VirtualMachine(cpu, cpu.m);

		vm.cpu.debug = true;

		KeyboardDriver kDriver = new KeyboardDriver();
		int opt = 100;
		while(opt != 0){
			System.out.println("\nDigite o programa que deseja executar:");
			System.out.println("\t1 - PA");
			System.out.println("\t2 - PB");
			System.out.println("\t3 - PC");
			System.out.println("\t4 - Contador");
			System.out.println("\t5 - E1 (DETALHAR)");
			System.out.println("\t6 - E2 (DETALHAR)");
			System.out.println("\t7 - E3 (DETALHAR)");
			System.out.println("\t0 - Encerrar execução");

			
			opt = kDriver.readKeyboardInput();

			vm.sos.loadProgram(0, getProgram(opt));
			vm.sos.runProgram(0, 0, getProgram(opt).length-1);

			cpu = new CPU(new Memory(MEM_SIZE));	//Limpando a memória para execução de um novo programa.
			vm = new VirtualMachine(cpu, cpu.m);
		}

		System.out.println("\n----------------");
		System.out.println("Fim da execução.");
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
				return Softwares.E2;
			case 7:
				return Softwares.E3;
			case 0: 
				System.out.println("Encerrando programa!");
				return null;
			default:
				System.out.println("Encerrando programa!");
				return null;
		}
	}	

}
