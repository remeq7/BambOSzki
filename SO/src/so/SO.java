
package so;

import java.io.IOException;
import memoryManagement.VirtualMemory;
import memoryManagement.ExchangeFile;
import processesmanagment.ProcessesManagement;
import interproccessCommunication.interprocessCommunication;
import syncMethod.Lock;


public class SO {

    public static void main(String[] args) throws IOException {
       VirtualMemory virtualMemory = new VirtualMemory();
        ProcessesManagement processesManagement = new ProcessesManagement();
        
        ExchangeFile exchangeFile = new ExchangeFile();
        virtualMemory.currentProcess = "test1";
        interprocessCommunication iC = new interprocessCommunication(virtualMemory.currentProcess);
        
        processesManagement.NewProcess_XC("test1");
        processesManagement.SetFirstPageNumberWithID(0, (int) (exchangeFile.getExchangeFileLength() / 16));
        processesManagement.SetHowManyPagesWithID(0, ((45 - 1) / 16 + 1));
        virtualMemory.loadProcess("test1", "0000000000000000111111111111111122222", 45);
        processesManagement.NewProcess_XC("test2");
        processesManagement.SetFirstPageNumberWithID(1, (int) (exchangeFile.getExchangeFileLength() / 16));
        processesManagement.SetHowManyPagesWithID(1, ((30 - 1) / 16 + 1));
        virtualMemory.loadProcess("test2", "3333333333333333444444444", 30);
        processesManagement.NewProcess_XC("test3");
        processesManagement.SetFirstPageNumberWithID(2, (int) (exchangeFile.getExchangeFileLength() / 16));
        processesManagement.SetHowManyPagesWithID(2, ((120 - 1) / 16 + 1));
        virtualMemory.loadProcess("test3", "5555555555555555666666666666666677777777777777778888", 120);

        System.out.println(virtualMemory.readMemory(0));
        virtualMemory.currentProcess = "test2";
        virtualMemory.writeMemory(0, 'X');
        virtualMemory.currentProcess = "test3";
        System.out.println(virtualMemory.readMemory(0));
        virtualMemory.currentProcess = "test1";
        System.out.println(virtualMemory.readMemory(0));
        virtualMemory.printVirtualMemory(0, 128);

        //processesManagement.SetState(0, 4);
        //processesManagement.CheckStates();

        virtualMemory.currentProcess = "test3";
        System.out.println(virtualMemory.readMemory(32));
        virtualMemory.printVirtualMemory(0, 128);
        System.out.println(virtualMemory.readMemory(16));
        virtualMemory.printVirtualMemory(0, 128);
        System.out.println(virtualMemory.readMemory(0));
        virtualMemory.printVirtualMemory(0, 128);
        System.out.println(virtualMemory.readMemory(40));
        virtualMemory.printVirtualMemory(0, 128);
        System.out.println(virtualMemory.readMemory(60));
        virtualMemory.printVirtualMemory(0, 128);
        virtualMemory.printSecondChance();
        System.out.println(virtualMemory.readMemory(80));
        virtualMemory.printVirtualMemory(0, 128);
        virtualMemory.printSecondChance();
        System.out.println(virtualMemory.readMemory(100));
        virtualMemory.printVirtualMemory(0, 128);
        virtualMemory.printSecondChance();
        virtualMemory.printFreeFrames();
        virtualMemory.printPageTable("test1");
        virtualMemory.printPageTable("test2");
        virtualMemory.printPageTable("test3");
        System.out.println(virtualMemory.readMemory(119));
        virtualMemory.printVirtualMemory(0, 128);
        virtualMemory.printSecondChance();

        virtualMemory.currentProcess = "test2";
        System.out.println(virtualMemory.readMemory(16));
        virtualMemory.printVirtualMemory(0, 128);
        virtualMemory.printPageTable("test1");
        virtualMemory.printPageTable("test2");
        virtualMemory.printPageTable("test3");
        virtualMemory.printSecondChance();
        System.out.println(virtualMemory.readMemory(0));

        virtualMemory.printVirtualMemory(0, 128);

        //processesManagement.SetState(2, 4);
       // processesManagement.CheckStates();

        virtualMemory.printVirtualMemory(0, 128);
        
        processesManagement.printProcessListInformations();
        processesManagement.printProcessInformations(0);
        processesManagement.printProcessInformations(1);               
        processesManagement.printProcessInformations(2);
        processesManagement.SetState(2, 3);
        processesManagement.SetState(0, 3);
        processesManagement.SetState(1, 3);
        Lock lock = new Lock("niemaznaczenia");
        lock.Add(processesManagement.getProcess("test1"));
        iC.write("kupa","test2",processesManagement.getProcess("test2"));    
        iC.read("test2", processesManagement.getProcess("test1"));
        processesManagement.printProcessInformations(0);
        processesManagement.printProcessInformations(1);               
        processesManagement.printProcessInformations(2);
    }
    
}
