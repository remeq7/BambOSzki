package fileSystem;

public class File {
	private final int BLOCK_ERROR = -1, LAST_BLOCK = -1;
	
	private String name, extension;
	private int firstBlock, nextBlock, size;
	
	File(String fullName, int firstBlock, int nextBlock, int size) throws Exception {
		this.name = fullName.substring(0, fullName.length()-4);
		this.extension = fullName.substring(fullName.length()-3, fullName.length());
		if(firstBlock < 0 || firstBlock > 31 || nextBlock < -1 || nextBlock > 31) {
			throw new Exception("Niepoprawny nr bloku");
		}
		else {
			this.firstBlock = firstBlock;
			this.nextBlock = nextBlock;
		}
		if(size < 0 || size > 1024) {
			throw new Exception("Niepoprawny rozmiar");
		}
		else {
			this.size = size;
		}
	}
	
	protected File() {
		//null file
		this.name = "#";
		this.firstBlock = -1;
		this.nextBlock = -1;
		this.size = -1;
	}
	
	protected File(File file) {
		this.name = file.name;
		this.extension = file.extension;
		this.size = file.size;
		this.firstBlock = file.firstBlock;
		this.nextBlock = file.nextBlock;
	}
	
	protected int GetFirstBlock() {
		return this.firstBlock;
	}
	
	protected int GetNextBlock() {
		return this.nextBlock;
	}
	
	protected int SetFirstBlock(int firstBlock) {
		if(firstBlock < 0 || firstBlock > 32) return BLOCK_ERROR;
		else {
			this.firstBlock = firstBlock;
			return firstBlock;
		}
	}
	
	protected int SetNextBlock(int nextBlock) {
		if(nextBlock < 0 || nextBlock > 32) return -1;
		else {
			this.nextBlock = nextBlock;
			return nextBlock;
		}
	}
	
	public String GetFullName(){
		return this.name + '.' + this.extension;
	}
	
	public int GetSize() {
		return this.size;
	}
}
