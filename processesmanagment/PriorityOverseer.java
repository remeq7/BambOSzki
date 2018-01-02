package processesmanagment;

import java.util.Random;

public class PriorityOverseer {
	
	//===ZMIENNE=========================================================================================
	
	private int priorytet;
	private int max_priorytet = 15;
	private int min_priorytet = 1;
	
	private Random wylosowana_liczba = new Random();
	
	//===METODY==========================================================================================
	
	public int RollPriority() {
		
		priorytet = wylosowana_liczba.nextInt(max_priorytet)+min_priorytet;
		
		return priorytet;
	}
}