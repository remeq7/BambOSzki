package exceptions_FAT;

public class IllegalFileNameException extends Exception {
	public IllegalFileNameException(String input) {
		super(input);		
	}
}
