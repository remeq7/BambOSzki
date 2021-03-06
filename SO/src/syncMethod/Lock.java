package syncMethod;

import java.util.LinkedList;
import java.util.Queue;

import processesmanagment.Process;

public class Lock 
{	
	public String name;
	private boolean state;
	private Process lockingProces;
	private Queue<Process> queueWaitingProcesses;
	
	public Lock(String name) {
		this.name = name;
		this.setState(false);
		queueWaitingProcesses = new LinkedList<Process>();
	}
	
        public void Add(Process process)
        {
            if(process.GetState()==3)
            {
                queueWaitingProcesses.add(process);
            }
        }
	public boolean isState()  {
		return state;
	}
	
	public void setState(boolean state)  {
		this.state = state;
	}
	
	public void displayLockingProces() {
		System.out.println(lockingProces.GetName());
	}
	
	public void lock(Process procesWhichCloses) {
		if(!isState()) {
			//zamek jest otwarty, proces zamyka zamek i rusza dalej
			setState(true);
			this.lockingProces = procesWhichCloses;
		} else {
			//zamek jest zamkni�ty wi�c proces w�druje do kolejki i ustawiany jest jego bit blocked
			queueWaitingProcesses.offer(procesWhichCloses);
			procesWhichCloses.SetBlocked(true);
			procesWhichCloses.SetState(3);
			
		}
	}
	
	public void unlock(Process procesWhichOpen) {
		//odblokowuje zamek i je�li kolejka nie jest pusta to zeruje bit blocked pierwszego oczekujacego procesu.
		if(procesWhichOpen.GetID() == this.lockingProces.GetID()) {
			this.setState(false);
			this.lockingProces = null;
			if(!queueWaitingProcesses.isEmpty()) {
				queueWaitingProcesses.peek().SetState(1);
				queueWaitingProcesses.poll().SetBlocked(false);
			}
		}
	}
        
}
