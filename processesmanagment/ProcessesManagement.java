package processesmanagment;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import memoryManagement.VirtualMemory;

public class ProcessesManagement extends Process {

	// TO JEST KLASA Z METODAMI DLA WAS
	
	//***ZMIENNE*****************************************************************************************
	
	public List<Process> processesList;
	
	//---pomocnicze--------------------------------------------------------------------------------------
	
	private ID_Overseer idoverseer;
	
	private ProcessStateOverseer stateOverseer;
	
	private List<Integer> finishedProcessList;
	
	private VirtualMemory RAM;
	
	private int processNumber;
	
	//***METODY******************************************************************************************
	
	//---Konstruktor-------------------------------------------------------------------------------------
	
	public ProcessesManagement(VirtualMemory RAM) {
		this.RAM = RAM;
		processesList = new LinkedList<Process>();
		idoverseer = new ID_Overseer();
		stateOverseer = new ProcessStateOverseer();
		finishedProcessList = new LinkedList<Integer>();
		processNumber = 1;
	}
	
	//===Dodaj/Usun Procesy===============================================================================
	
	//---

	public int NewProcess_XC(String ProgramPath_Original, String Name) throws IOException{
		int i = FindProcessWithName(Name);
		  
		  if(i != -1) {
		   
		   System.out.println("You can't create two programs with the same name while using command XC");
		   return -1;
		  }
		  
		  Process process = new Process();
		  int id = idoverseer.PickID();
		  String s = Integer.toString(id);
		  process.CreateProcess(id,Name, processNumber);
		  processesList.add(process); 
		  processNumber++;
		  
		  RAM.loadProcess(Name,???,???);
		  CheckStates();
		 return 0;
	}
	
	public void NewProcess_forUser(String ProgramPath_Original, String Name) throws IOException {
		Process process = new Process();
		int id = idoverseer.PickID();	
		int i = FindProcessWithName(Name);
		
		if(i != -1) {
			Name = Name + id;
		}
		String s = Integer.toString(id);
		process.CreateProcess(id,ProgramPath_Original, Name, processNumber);
		processesList.add(process);
		processNumber++;
		
		RAM.loadProcess(Name,???, ???);
		CheckStates();
	}
	
	public  Process NewProcess_EmptyProcess(String Name) {
		Process process = new Process();
		process.CreateProcess(-1, Name, -1);
		process.SetBasePriority(0);
		process.SetCurrentPriority(0);
		CheckStates();
		return process;
	}
	
	//---
	
	private void  DeleteProcess() throws IOException {
		for (int i = 0; i < finishedProcessList.size(); i++) {
			int index = FindProcessWithID(finishedProcessList.get(i));
			RAM.deleteProcess(processesList.get(i).GetName()); //Pewnie bedziesz z tego jakos korzystal Pawel
			processesList.remove(index);
		}
		finishedProcessList.clear();
	}
	
	public void DeleteProcessWithID(int ID) {
                for(int i=0;i<processesList.size();i++)
                {
                    if(processesList.get(i).GetID()==ID)
                    {
                        processesList.remove(i);    
                    }
                }

	}
	
	public void DeleteProcessWithName_XD(String name) throws IOException {
               for(int i=0;i<processesList.size();i++)
                {
                    if(processesList.get(i).GetName()==name)
                    {
                        processesList.remove(i); 
                        RAM.deleteProcess(name);
                    }
                }

	}
	
	
	
	public void CheckStates() throws IOException {
		for (int i = 0; i < processesList.size(); i++) {
			if(processesList.get(i).pcb.ProcessState == stateOverseer.finished) {
				finishedProcessList.add(processesList.get(i).pcb.ProcessID);
			}
		}
		
		DeleteProcess();
	}
	
	//===Szukanie procesow===============================================================================
	
	//---
	
	public int FindProcessWithID(int ID) {
		Process proces_kopia;
		
		for(int i = 0; i < processesList.size(); i++) {
			proces_kopia = processesList.get(i);
			if(proces_kopia.GetID() == ID) {
				return i;
			}	
		}
		
		return -1;
	}
	
	//---
	
	public int FindProcessWithName(String name) {
		for(int i = 0; i < processesList.size(); i++) {
			if(processesList.get(i).GetName().equals(name)) {
				return i;
			}	
		}
		return -1;
	}
	
	//===Get/Set===========================================================================================
	
	//---
	
	public int GetIDwithName(String name) {
            int Identyfikator=-1;
		for(int i=0;i<processesList.size();i++)
                {
                    if(processesList.get(i).GetName()==name)
                    {
                        Identyfikator = processesList.get(i).GetID();     
                    }
                }
		//int index = FindProcessWithName(name);
                
		return Identyfikator;
	}
	
	//---
	
	public String GetNameWithID(int ID) {
            String nazwa = "Brak procesu o takim ID";
		for(int i=0;i<processesList.size();i++)
                {
                    if(processesList.get(i).GetID()==ID)
                    {
                        nazwa = processesList.get(i).GetName();     
                    }
                }
                return nazwa;
	}
	
	//---
	
	public int GetWhenCameToListWithID(int ID) {
			 int przybycie = 0;
		for(int i=0;i<processesList.size();i++)
                {
                    if(processesList.get(i).GetID()==ID)
                    {
                        przybycie = processesList.get(i).GetWhenCameToList();     
                    }
                }
                return przybycie;
	}
		
	public void SetWhenCameToListWithID(int ID, int whenCametoList) {
		for(int i=0;i<processesList.size();i++)
                {
                    if(processesList.get(i).GetID()==ID)
                    {
                        processesList.get(i).SetWhenCameToList(whenCametoList);     
                    }
                }
		
	}
	
	//---
	
	public int GetStateWithID(int ID) {
            int stan=-1;
		for(int i=0;i<processesList.size();i++)
                {
                    if(processesList.get(i).GetID()==ID)
                    {
                       stan= processesList.get(i).GetState();     
                    }
                }

		return stan;
	}
		
	public void SetState(int ID, int State) {
		for(int i=0;i<processesList.size();i++)
                {
                    if(processesList.get(i).GetID()==ID)
                    {
                       processesList.get(i).SetState(State);     
                    }
                }

	}
	
	//---
	
	public int GetBasePriorityWithID(int ID) {
		  int priorytet=-1;
		for(int i=0;i<processesList.size();i++)
                {
                    if(processesList.get(i).GetID()==ID)
                    {
                       priorytet= processesList.get(i).GetBasePriority();     
                    }
                }

		return priorytet;
	}
	
	public void SetBasePriorityWithID(int ID, int priority) {
		for(int i=0;i<processesList.size();i++)
                {
                    if(processesList.get(i).GetID()==ID)
                    {
                       processesList.get(i).SetBasePriority(priority);     
                    }
                }

	}
	
	//---
	
	public int GetCurrentPrirityWithID(int ID) {
		  int priorytet=-1;
		for(int i=0;i<processesList.size();i++)
                {
                    if(processesList.get(i).GetID()==ID)
                    {
                       priorytet= processesList.get(i).GetCurrentPriority();     
                    }
                }

		return priorytet;
	}
		
	public void SetCurrentPririty(int ID, int priority) {
		for(int i=0;i<processesList.size();i++)
                {
                    if(processesList.get(i).GetID()==ID)
                    {
                       processesList.get(i).SetCurrentPriority(priority);     
                    }
                }
	}
	
	//---
	
	public int GetHowLongWaitingWithID(int ID) {
		  int jakDlugo=-1;
		for(int i=0;i<processesList.size();i++)
                {
                    if(processesList.get(i).GetID()==ID)
                    {
                       jakDlugo= processesList.get(i).GetHowLongWaiting();     
                    }
                }

		return jakDlugo;	
		
	}
		
	public void SetHowLongWaitingWithID(int ID, int howLong) {
			
		for(int i=0;i<processesList.size();i++)
                {
                    if(processesList.get(i).GetID()==ID)
                    {
                       processesList.get(i).SetHowLongWaiting(howLong);     
                    }
                }
	}
        public int GetHowManyPagesWithID(int ID) {
		  int jakDuzo=-1;
		for(int i=0;i<processesList.size();i++)
                {
                    if(processesList.get(i).GetID()==ID)
                    {
                       jakDuzo= processesList.get(i).GetHowManyPages();     
                    }
                }

		return jakDuzo;	
		
	}
		
	public void SetHowManyPagesWithID(int ID, int howMany) {
			
		for(int i=0;i<processesList.size();i++)
                {
                    if(processesList.get(i).GetID()==ID)
                    {
                       processesList.get(i).SetHowManyPages(howMany);     
                    }
                }
	}
	
	//---
	
	public boolean GetBlockedWithID(int ID) {
		
		  boolean zablokowany=false;
		for(int i=0;i<processesList.size();i++)
                {
                    if(processesList.get(i).GetID()==ID)
                    {
                       zablokowany= processesList.get(i).GetBlocked();     
                    }
                }

		return zablokowany;
	}
	
	public void SetBlocked(int ID, boolean blockedState) {
		
		for(int i=0;i<processesList.size();i++)
                {
                    if(processesList.get(i).GetID()==ID)
                    {
                       processesList.get(i).SetBlocked(blockedState);     
                    }
                }
	}
	
	//---
	
	public PCB GetPCBWithID(int ID) {
            int pomoc=0;
		for(int i=0;i<processesList.size();i++)
                {
                    if(processesList.get(i).GetID()==ID)
                    {
                      pomoc=i;  
                    }
                }
                return processesList.get(pomoc).pcb; 
		//int index = FindProcessWithID(ID);
		//return processesList.get(index).pcb;
	}
	
	//===Shell=============================================================================================
	
	public void printProcessListInformations() {
		System.out.println("------------------------------");
		System.out.println("Processes list:");
		
		for(int i = 0; i < processesList.size(); i++) {
			System.out.println( i + ". " + processesList.get(i).GetID() + ", " + processesList.get(i).GetName());
		}
	}

	public void printProcessInformations(int ID) {
            
		for(int i=0;i<processesList.size();i++)
                {
                    if(processesList.get(i).GetID()==ID)
                    {
                      processesList.get(i).printInformations();  
                    }
                    else if (processesList.get(i).GetID()!=ID)
			System.out.println("This process does not exist");
                }

	}
	
	/**
	 * @warning niebezpieczna. uzycie nazwy, ktora nie istnieje powoduje ArrayBoundOfException [-1]
	 * @param name
	 * @return process by ID.
	 */
	public Process getProcess(String name) {
		return processesList.get(FindProcessWithName(name));
	}
}
