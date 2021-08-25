package Software;

import java.io.Console;

import Hardware.CPU;
import Hardware.Memory;
import Hardware.Memory.Word;
import Software.SistemaOperacional.SOs;

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

		/**
		 * true  = Debug ON
		 * false = Debug OFF
		 */
		vm.cpu.debug = false;

		/**
		 * Programa a ser carregado em memória
		 */
		Word[] program = Softwares.fibonacci10;


		System.out.println("----------------");
		vm.cpu.aux.dump(program, 0, program.length);
		System.out.println("----------------");


		System.out.println("DEBUG: " + vm.cpu.debug.toString());
		vm.sos.loadProgram(0, program);
		vm.sos.runProgram(0, 0, program.length-1);

		System.out.println("----------------");
		vm.cpu.aux.dump(program, 0, program.length);
		System.out.println("----------------");

		System.out.println("\n----------------");
		System.out.println("Fim da execução.");
	}	

}
