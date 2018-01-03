package fileSystem;
import java.util.Arrays;
import java.util.Collections;
import java.util.Vector;
import java.util.regex.*;
import exceptions_FAT.*;

public class FAT {
	private final int DISK_SIZE = 1024;
	private final int BLOCK_SIZE = 32;
	private final int BLOCKS = DISK_SIZE / BLOCK_SIZE;
	private final int BLOCK_ERROR = -1, LAST_BLOCK = -1;
	private final char EMPTY_BYTE = '#';
	private final File EMPTY_FILE;
	
	private char disk[];
	private Vector<File> FATarray;
	private Boolean[] FreeBlocks;
	
	FAT() {
		try {
			disk = new char[DISK_SIZE];
			EMPTY_FILE = new File();
			FATarray = new Vector<File>(BLOCKS);
			FreeBlocks = new Boolean[BLOCKS];
			
			Arrays.fill(disk, EMPTY_BYTE);
			Arrays.fill(FreeBlocks, Boolean.TRUE);
			
			for(int i=0; i<BLOCKS; i++) {
				FATarray.add(EMPTY_FILE);
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	public Boolean AppendToFile(String fullName) throws Exception {
		if(!DoesFileExist(fullName)) throw new Exception("Nie znaleziono pliku");
		else {
			int firstBlock = FindFilesFirstBlock(fullName);
			File file = FATarray.get(firstBlock);
			if(file.GetNextBlock() == LAST_BLOCK) {
				
			}
		}
		return true;
	}
	
	public Boolean CreateEmptyFile(String fullName) throws Exception {
		if(!CheckFileName(fullName)) {
			throw new IllegalFileNameException("Podano nieprawidlowa nazwe. Nazwa musi zawierac 1 do 8 znakow (male, duze litery i cyfry) + kropke i nazwe rozszerzenia (txt)");
		}
		else if(DoesFileExist(fullName)) {
			throw new IllegalFileNameException("Istnieje plik o podanej nazwie");
		}
		else {
			int freeBlock = FindFreeBlock();
			if(freeBlock == -1) throw new OutOfBlocks("Brak miejsca na dysku");
			else if(freeBlock < -1 || freeBlock > 31) throw new Exception("Blad algorytmu");
			else {
				File file = new File(fullName, freeBlock, LAST_BLOCK, 0);
				FATarray.add(freeBlock, file);				
			}
		}
		return true;
	}
		
	public Boolean CreateNewFile(String fullName, String content) throws Exception {
		if(!CheckFileName(fullName)) {
			throw new IllegalFileNameException("Podano nieprawidlowa nazwe. Nazwa musi zawierac 1 do 8 znakow (male, duze litery i cyfry) + kropke i nazwe rozszerzenia (txt)");
		}
		else if(DoesFileExist(fullName)) {
			throw new IllegalFileNameException("Istnieje plik o podanej nazwie");
		}
		else {
			int freeBlock = FindFreeBlock();
			int fileSize = content.length();
			int numberOfFreeBlocks = CountFreeBlocks();
			
			if(freeBlock == -1) throw new OutOfBlocks("Brak miejsca na dysku. Kod -1");
			else if(freeBlock < -1 || freeBlock > 31) throw new Exception("Blad algorytmu");
			else {
				double d_neededBlocks = (double) fileSize/BLOCK_SIZE; 
				int i_neededBlocks = (int) d_neededBlocks;
				if (d_neededBlocks%1 != 0) i_neededBlocks+=1; //Casting double to integer rounds down
				
				System.out.println("Rozmiar pliku to: " + fileSize + " na 32 to: " + i_neededBlocks + " freeblock " + numberOfFreeBlocks);
				if(i_neededBlocks > numberOfFreeBlocks) throw new OutOfBlocks("Brak miejsca na dysku. Kod -2");
				else {
					/* nowy wpis katalogowy */
					File file = new File(fullName, freeBlock, LAST_BLOCK, fileSize);
					
					int charToWrite = 0;	//Zmienna pomocnicza
					while(fileSize != 0) {
						FATarray.set(freeBlock, file);
						file.SetNextBlock(LAST_BLOCK);
						//System.out.println("Nadpisano plikiem: " + FATarray.get(freeBlock).GetFullName());
						for(int i=0; i<BLOCK_SIZE && fileSize!=0; i++) {
							disk[i+BLOCK_SIZE*freeBlock] = content.charAt(charToWrite);
							System.out.println("push: " + disk[i+BLOCK_SIZE*freeBlock] +  " " + i + freeBlock);
							charToWrite++;
							fileSize--;
						}
						FreeBlocks[freeBlock] = Boolean.FALSE;
						if(fileSize > 0) {
							file = new File(file);
							int currentBlock = freeBlock;
							freeBlock = FindFreeBlock();
							FATarray.get(currentBlock).SetNextBlock(freeBlock);
						}
					}
				}
			}
		}
		System.out.println("POKAZUJE DYSK:");
		PrintDisk();
		return true;
	}
	
	public void DeleteFile(String fullName) throws Exception {
		if(!DoesFileExist(fullName)) throw new Exception("Brak pliku o podanej nazwie");
		else {
			int blockToDelete = FindFilesFirstBlock(fullName);
			int nextBlockToDelete;
			
			do {
				nextBlockToDelete = FATarray.get(blockToDelete).GetNextBlock();
				FATarray.set(blockToDelete, EMPTY_FILE);
				for(int i=0; i<BLOCK_SIZE; i++) disk[i+BLOCK_SIZE*blockToDelete] = EMPTY_BYTE;
				FreeBlocks[blockToDelete] = Boolean.TRUE;
				blockToDelete = nextBlockToDelete;
			} while(nextBlockToDelete != -1);
		}
	}
	
	public Boolean DoesFileExist(String fullName) {
		for(int i=0; i<FATarray.size(); i++) {
			if(FATarray.get(i).GetFullName().equals(fullName)) return true;
		}
		return false;
	}
	
	public void PrintDisk() {
		int blockNr = 1;
		System.out.println("Ilosc wolnego miejsca na dysku " + CountFreeBlocks()*BLOCK_SIZE);
		for(int i=0; i<disk.length; i++) {
			if(i%BLOCK_SIZE == 0) {
				blockNr = (int) i/BLOCK_SIZE;
				System.out.print("\nBlok " + blockNr + " Plik: " + FATarray.get(blockNr).GetFullName() + " ");
			}
			System.out.print(disk[i]);
		}
	}
	public String PrintFilesContent(String fullName) throws Exception {
		if(DoesFileExist(fullName)) {
			int blockToRead = FindFilesFirstBlock(fullName);
			String content = "";
			File file = FATarray.get(blockToRead);
			int fileSize = file.GetSize();
			do {
				for(int i=0; i<BLOCKS && fileSize!=0; i++) {
					content += disk[i+blockToRead*BLOCK_SIZE];
					fileSize--;
				}
				if(fileSize > 0) {
					blockToRead = file.GetNextBlock();
					file = FATarray.get(blockToRead);
				}
			} while (file.GetNextBlock() != -1 && fileSize != 0);
			return content;
		}
		else throw new Exception("Plik nie istnieje");
	}
	
	/* zwraca czy nazwa jest poprawna */
	private Boolean CheckFileName(String input) {
		/* format nazwy: nazwa.txt, gdzie nazwa max. 8 znakow */
		String pattern = "^[a-zA-Z0-9]{1,8}[.]txt";
		Pattern p = Pattern.compile(pattern);
		Matcher matcher = p.matcher(input);
		Boolean matches = matcher.matches();
		
		if(!matches) {
			return false;
		}
		else return true;
	}
	
	private int CountFreeBlocks(){
		int countFreeBlocks = 0;
		for(int i=0; i<BLOCKS; i++) {
			System.out.println(i + ": " + FATarray.get(i).GetFirstBlock());
			if(FATarray.get(i).GetFirstBlock() == -1) countFreeBlocks++;
		}
		return countFreeBlocks;
	}
	private int FindFreeBlock() {
		for(int i=0; i<FreeBlocks.length; i++) {
			if(FreeBlocks[i] == Boolean.TRUE) return i;
		}
		return BLOCK_ERROR;
	}
	
	private int FindFilesFirstBlock(String fullName) {
		if(DoesFileExist(fullName)) {
			for(int i=0; i<FATarray.size(); i++) {
				if(FATarray.get(i).GetFullName().equals(fullName)) return i;
			}
		}
		return BLOCK_ERROR;
	}
}
