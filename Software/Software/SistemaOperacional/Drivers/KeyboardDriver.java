package Software.SistemaOperacional.Drivers;
import java.util.Scanner;

public class KeyboardDriver {
	private Scanner sc = new Scanner(System.in);
	int input;
	
	public int readKeyboardInput() {
		System.out.println("Digite um int:");
		input = sc.nextInt();
		return input;
	}
	
	//Utilizado para "limpar" o conteúdo do leitor.
	public void flushReaderBuffer() { 
		sc.nextLine();
	}

}
