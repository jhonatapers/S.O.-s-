package Software;

import Hardware.CPU;
import Hardware.Memory;
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

		vm.cpu.debug = false;

		vm.sos.loadProgram(0, Softwares.E1);
		vm.sos.runProgram(0, 0, Softwares.E1.length-1);
		
		System.out.println("\n----------------");
		System.out.println("Fim da execu��o.");
	}	

}
