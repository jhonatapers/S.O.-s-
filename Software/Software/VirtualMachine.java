package Software;
import java.util.concurrent.Semaphore;

import Hardware.CPU;
import Hardware.Memory;
//import Hardware.Memory.Word;
import Software.SistemaOperacional.SOs;
//import Software.SistemaOperacional.Scheduler;

public class VirtualMachine {

	public CPU cpu;
	public Memory memory;
	public SOs sos;
	
	private final static int MEM_SIZE = 1024;
	private final static int PAGE_SIZE = 16;
	private Semaphore semaSch;
	private Semaphore semaCPU;
	private Semaphore semaProcessQueue;

    public VirtualMachine(CPU _cpu, Memory _memory, Semaphore semaSch, Semaphore semaCPU, Semaphore semaProcessQueue){  
		
		this.semaSch = semaSch;
		this.semaCPU = semaCPU;
		this.semaProcessQueue = semaProcessQueue;

		cpu = _cpu;
		memory = _memory;
		sos = new SOs(_cpu, _memory, PAGE_SIZE, this.semaSch, this.semaCPU, this.semaProcessQueue);
		
    }

    public static void main(String args[]) {  
		
		Semaphore semaSch = new Semaphore(0);
		Semaphore semaCPU = new Semaphore(0);
		Semaphore semaProcessQueue = new Semaphore(1);
		
		CPU cpu = new CPU(new Memory(MEM_SIZE), PAGE_SIZE, semaCPU);	
		VirtualMachine vm = new VirtualMachine(cpu, cpu.m, semaSch, semaCPU, semaProcessQueue);

		/**
		 * Debug = Informar, no console, cada linha sendo executada pelo programa.
		 * true  = Debug ON
		 * false = Debug OFF
		 */
		vm.cpu.debug = false;


		//Carregando programas em memória.
		vm.sos.newProcess(new Softwares().contadorInOut);// [10] //Carregando o programa em memória CONTADOR PROCESS ID [0]
		//vm.sos.newProcess(new Softwares().ADD);// [20+3 => 23] //Carregando o programa em memória ADD PROCESS ID [2]
		//vm.sos.newProcess(new Softwares().SUB);// [30-32 => -2] //Carregando o programa em memória SUB PROCESS ID [3]
		//vm.sos.loadProgram(new Softwares().PB);// [4 => 12] //Carregando o programa em memória de FATORIAL
		//vm.sos.loadProgram(new Softwares().E1);// [12 => 12] [-1 => STOP (Interrupt ProgramEnd)] //Carregando o programa E1

		//Colocando o primeiro programa da fila no contexto do processador.
		//vm.sos.loadNextProcess();

		//Executa o programa atual da CPU e os demais em fila, até encerrar todos.
		vm.sos.runScheduler();
		vm.cpu.start();
		
		
	}


}
