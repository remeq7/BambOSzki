import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;

class interprocessCommunication
{
    //-------------------------ZMIENNE STATYCZNE-------------------------------

    private static int FIFO_LINES = -1;
    private static String FILE_NAME = "communcation.txt";

    //-------------------------ZMIENNE-----------------------------------------

    private processesManagment processesmanagment;
    private Locks lock;

    //-------------------------KONSTRUKTORY------------------------------------

    public interprocessCommunication()
    {
        processesmanagment = new processesManagment();
        lock = new Locks();
    }

    //-------------------------FUNKCJE-----------------------------------------

    //Zapisuje wiadomość do pliku
    public void write(String message)
    {
        lock.lock(processManagment.getProcess(receiverName));
        try
        {
            File file = new File(FILE_NAME);
            if(!file.isFile()) FIFO_LINES = 0;
            else
            {
                ArrayList<String> messages_array = new ArrayList<>();
                FileReader reader = new FileReader(file);
                Scanner out = new Scanner(reader);

                while(out.hasNextLine())
                {
                    messages_array.add(out.nextLine());
                }
                FIFO_LINES = messages_array.size();
                reader.close();
                out.close();
            }
            FileWriter writer = new FileWriter(file, true);
            BufferedWriter in = new BufferedWriter(writer);
            in.write(message + "\n");
            FIFO_LINES++;
            in.close();
            writer.close();
        } catch (IOException ex)
        {
            System.out.println("Error: " + ex.getMessage());
        }
        lock.unlock(processManagment.getProcess(receiverName));
    }

    //zczytuje wiadomość z pliku
    public String read()
    {
        lock.lock(processManagment.getProcess(senderName));
        String message = "";
        try
        {
            File file = new File(FILE_NAME);
            ArrayList<String> messages_array = new ArrayList<>();
            FileReader reader = new FileReader(file);
            Scanner out = new Scanner(reader);

            while(out.hasNextLine())
            {
                messages_array.add(out.nextLine());
            }
            reader.close();
            out.close();
            lock.unlock(processManagment.getProcess(senderName));

            if(messages_array.isEmpty())
            {
                System.out.println("Can't read from empty file");
            }
            else
            {
                message = messages_array.get(messages_array.size() - 1);

                messages_array.remove(messages_array.size() - 1);
                FIFO_LINES--;
                FileWriter writer = new FileWriter(file);
                BufferedWriter in = new BufferedWriter(writer);

                for(int i=0; i<messages_array.size(); i++) in.write(messages_array.get(i) + "\n");

                in.close();
                writer.close();
            }

            return message;

        } catch (FileNotFoundException ex1)
        {
            System.out.println("Error: " + ex1.getMessage());
            return message;
        } catch (IOException ex)
        {
            System.out.println("Error: " + ex.getMessage());
            return message;
        }
    }

    //wyświetla zawartość pliku
    public void show()
    {
        File file = new File(FILE_NAME);
        if(FIFO_LINES < 0) System.out.println("File didn't exist");
        else if(FIFO_LINES == 0) System.out.println("File is empty");
        else
        {
            try
            {
                System.out.println("Number of communicates: " + FIFO_LINES);
                FileReader reader = new FileReader(file);
                Scanner out = new Scanner(reader);
                for(int i=0; i<FIFO_LINES; i++)
                {
                    System.out.println(out.nextLine());
                }

            } catch (FileNotFoundException ex)
            {
                System.out.println("Error: " + ex.getMessage());
            }
        }
    }

    //usuwa plik
    private void delete()
    {
        File file = new File(FILE_NAME);
        file.delete();
        FIFO_LINES = -1;
    }
}
