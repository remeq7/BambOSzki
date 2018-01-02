package processesmanagment;

public class ID_Overseer {
	
	//===ZMIENNE=========================================================================================
	
	private int NumberOfID = 1000;
	
	
	private int currentID;
	private int FreeID[];
	
	//===METODY==========================================================================================
	
	public ID_Overseer() {
		
		FreeID = new int[NumberOfID];
		
		for(int i=0;i < NumberOfID; i++) {
			
			FreeID[i] = 1;
		}
	}
	
	public int PickID() {
		
		for(int i=0;i < NumberOfID; i++) {
			
			if(FreeID[i] == 1) {
				
				currentID = i;
				FreeID[i] = 0;
				
				//System.out.print(currentID);
				
				return currentID;
			}
		}
		
		return 0;
	}
	
	public void ClearID(int ID) {
		
		FreeID[ID] = 1;
	}
}