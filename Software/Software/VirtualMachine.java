package Software;
import java.util.concurrent.Semaphore;

import Hardware.CPU;
import Hardware.Memory;
import Software.SistemaOperacional.SOs;

public class VirtualMachine {

	public CPU cpu;
	public Memory memory;
	public SOs sos;
	private final static int MEM_SIZE = 128;//1024;
	private final static int PAGE_SIZE = 16;

	
    public VirtualMachine(CPU _cpu, Memory _memory, Semaphore sSch, Semaphore sCPU){  
		cpu = _cpu;
		memory = _memory;
		sos = new SOs(_cpu, _memory, PAGE_SIZE, sSch, sCPU);
    }

    public static void main(String args[]) {  

			
		Semaphore sSch = new Semaphore(0);
		Semaphore sCPU = new Semaphore(1);

		Semaphore sNeedInput = new Semaphore(1);
		Semaphore sInput = new Semaphore(1);
		Semaphore sInputed = new Semaphore(1);
		
		CPU cpu = new CPU(new Memory(MEM_SIZE), PAGE_SIZE, sCPU);	
		VirtualMachine vm = new VirtualMachine(cpu, cpu.m, sSch, sCPU);
		Shell shell = new Shell(vm.sos, sNeedInput, sInput, sInputed);
		vm.sos.runConsole(sNeedInput, sInput, sInputed);
		/**
		 * Debug = Informar, no console, cada linha sendo executada pelo programa.
		 * true  = Debug ON
		 * false = Debug OFF
		 */
		vm.cpu.debug = false;

		//Carregando programas em memória.
		//vm.sos.newProcess(new Softwares().contadorInOut);// [10] //Carregando o programa em memória CONTADOR PROCESS ID [0]
		//vm.sos.newProcess(new Softwares().Fatorial);// [10] //Carregando o programa em memória CONTADOR PROCESS ID [0]
		//vm.sos.newProcess(new Softwares().contadorInOut);
		//vm.sos.newProcess(new Softwares().contadorInOut);
		//vm.sos.newProcess(new Softwares().contadorInOut);
		//vm.sos.newProcess(new Softwares().contadorInOut);
		
		//vm.sos.newProcess(new Softwares().Fatorial);
		//vm.sos.newProcess(new Softwares().ADD);// [10] //Carregando o programa em memória CONTADOR PROCESS ID [0]
		//vm.sos.loadProgram(new Softwares().ADD);// [20+3 => 23] //Carregando o programa em memória ADD PROCESS ID [2]
		//vm.sos.loadProgram(new Softwares().SUB);// [30-32 => -2] //Carregando o programa em memória SUB PROCESS ID [3]
		//vm.sos.loadProgram(new Softwares().PB);// [4 => 12] //Carregando o programa em memória de FATORIAL
		//vm.sos.loadProgram(new Softwares().E1);// [12 => 12] [-1 => STOP (Interrupt ProgramEnd)] //Carregando o programa E1

		//Colocando o primeiro programa da fila no contexto do processador.
		//vm.sos.loadNextProcess();

		//Executa o programa atual da CPU e os demais em fila, até encerrar todos.
		shell.start();
		vm.sos.runScheduler();
		vm.cpu.start();
		
	}

	


}
