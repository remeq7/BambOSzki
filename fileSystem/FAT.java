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
	private boolean[] FreeBlocks;
	
	FAT() {
		try {
			disk = new char[DISK_SIZE];
			EMPTY_FILE = new File();
			FATarray = new Vector<File>(BLOCKS);
			FreeBlocks = new boolean[BLOCKS];
			
			Arrays.fill(disk, EMPTY_BYTE);
			Arrays.fill(FreeBlocks, true);
			
			for(int i=0; i<BLOCKS; i++) {
				FATarray.add(EMPTY_FILE);
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	public boolean AppendToFile(String fullName, String content) throws Exception {
		if(!DoesFileExist(fullName)) throw new Exception("Nie znaleziono pliku");
		else {
			int firstBlock = FindFilesFirstBlock(fullName);
			if(firstBlock == BLOCK_ERROR) throw new Exception("Nie znaleziono pierwszego bloku");
			else { 
				File file = FATarray.get(firstBlock);
				int filesFreeSpace = FilesFreeSpace(fullName);
				if (filesFreeSpace == BLOCK_ERROR) throw new Exception("Nie znaleziono pliku.");
				else {
					int contentSize = content.length();
					if((FreeBlocksSpace() + filesFreeSpace) < contentSize) throw new OutOfBlocks("Brak miejsca na dysku");
					else {
						int lastBlock = file.GetNextBlock();
						int charToWrite = 0;
						
						while(lastBlock != -1) {
							file = FATarray.get(lastBlock);
							lastBlock = file.GetNextBlock();
						}
						for(int i=0; i<filesFreeSpace && i<contentSize; i++) {
							disk[i+BLOCK_SIZE*lastBlock] = content.charAt(charToWrite);
							charToWrite++;
							contentSize--;
						}
						while(contentSize !=0) {
							int freeBlock = FindFreeBlock();
							FATarray.set(freeBlock, file);
							file.SetNextBlock(LAST_BLOCK);
							for(int i=0; i<BLOCK_SIZE && contentSize!=0; i++) {
								disk[i+BLOCK_SIZE*freeBlock] = content.charAt(charToWrite);
								charToWrite++;
								contentSize--;
							}
							FreeBlocks[freeBlock] = false;
							if(contentSize > 0) {
								file = new File(file);
								int currentBlock = freeBlock;
								freeBlock = FindFreeBlock();
								FATarray.get(currentBlock).SetNextBlock(freeBlock);
							}
						}
					}
				}
			} 				
		}
		return true;
	}
	
	public boolean CreateEmptyFile(String fullName) throws Exception {
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
		
	public boolean CreateNewFile(String fullName, String content) throws Exception {
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
				double d_neededBlocks = (double) fileSize/BLOCK_SIZE; //Casting double to integer rounds down 
				int i_neededBlocks = (int) d_neededBlocks;
				if (d_neededBlocks%1 != 0) i_neededBlocks+=1;
				
				if(i_neededBlocks > numberOfFreeBlocks) throw new OutOfBlocks("Brak miejsca na dysku. Kod -2");
				else {
					/* nowy wpis katalogowy */
					File file = new File(fullName, freeBlock, LAST_BLOCK, fileSize);
					
					int charToWrite = 0;	//Zmienna pomocnicza
					while(fileSize != 0) {
						FATarray.set(freeBlock, file);
						file.SetNextBlock(LAST_BLOCK);
						for(int i=0; i<BLOCK_SIZE && fileSize!=0; i++) {
							disk[i+BLOCK_SIZE*freeBlock] = content.charAt(charToWrite);
							charToWrite++;
							fileSize--;
						}
						FreeBlocks[freeBlock] = false;
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
				FreeBlocks[blockToDelete] = true;
				blockToDelete = nextBlockToDelete;
			} while(nextBlockToDelete != -1);
		}
	}
	
	public boolean DoesFileExist(String fullName) {
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
				System.out.print("\nBlok " + blockNr + "Nastepny: " + FATarray.get(i/BLOCK_SIZE).GetNextBlock() + "Plik: " + FATarray.get(blockNr).GetFullName() + " ");
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
	private boolean CheckFileName(String input) {
		/* format nazwy: nazwa.txt, gdzie nazwa max. 8 znakow */
		String pattern = "^[a-zA-Z0-9]{1,8}[.]txt";
		Pattern p = Pattern.compile(pattern);
		Matcher matcher = p.matcher(input);
		boolean matches = matcher.matches();
		
		if(!matches) {
			return false;
		}
		else return true;
	}
	
	private int CountFreeBlocks(){
		int countFreeBlocks = 0;
		for(int i=0; i<FreeBlocks.length; i++) {
			if(FreeBlocks[i] == true) countFreeBlocks++;
		}
		return countFreeBlocks;
	}
	
	private int FilesFreeSpace(String fullName) {
		if(DoesFileExist(fullName)) {
			int blockToRead = FindFilesFirstBlock(fullName);
			int counter = 0;
			File file = FATarray.get(blockToRead);
			int fileSize = file.GetSize();
			do {
				for(int i=0; i<BLOCKS && fileSize!=0; i++) {
					if (disk[i+blockToRead*BLOCK_SIZE] == '#') counter++;
					fileSize--;
				}
				if(fileSize > 0) {
					blockToRead = file.GetNextBlock();
					file = FATarray.get(blockToRead);
				}
			} while (file.GetNextBlock() != -1 && fileSize != 0);
			return counter;
		}
		else return BLOCK_ERROR;
	}
	private int FindFreeBlock() {
		for(int i=0; i<FreeBlocks.length; i++) {
			if(FreeBlocks[i] == true) return i;
		}
		return BLOCK_ERROR;
	}
	
	private int FindFilesFirstBlock(String fullName) {
		if(DoesFileExist(fullName)) {
			for(File file : FATarray) {
				if(file.GetFullName().equals(fullName)) return FATarray.indexOf(file);
			}
		}
		return BLOCK_ERROR;
	}
	
	private int FreeBlocksSpace() {
		return CountFreeBlocks() * BLOCK_SIZE;
	}
}
