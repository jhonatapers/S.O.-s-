package Software;

import Hardware.CPU;
import Hardware.Memory;

public class VirtualMachine {

	public CPU cpu;
	public Memory memory;
	public SOs sos;

    public VirtualMachine(CPU _cpu, Memory _memory){  
		cpu = _cpu;
		memory = _memory;
		sos = new SOs(_cpu, _memory);
    }

    public static void main(String args[]) {
        
		Memory memory = new Memory();
		CPU cpu = new CPU(memory);		
		VirtualMachine vm = new VirtualMachine(cpu, memory);
		vm.sos.loadProgram(0, Softwares.fibonacci10);
		vm.sos.runProgram(0);
	}
	
    // -------------------------------------------------------------------------------------------------------
    // --------------- TUDO ABAIXO DE MAIN É AUXILIAR PARA FUNCIONAMENTO DO SISTEMA - nao faz parte 

	// -------------------------------------------- teste do sistema ,  veja classe de programas

    /*
	public void test1(){
		Aux aux = new Aux();
		Word[] p = new Programas().fibonacci10;
		aux.carga(p, vm.m);
		vm.cpu.setContext(0);
		System.out.println("---------------------------------- programa carregado ");
		aux.dump(vm.m, 0, 33);
		vm.cpu.run();
		System.out.println("---------------------------------- após execucao ");
		aux.dump(vm.m, 0, 33);
	}*/

}
