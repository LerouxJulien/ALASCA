package fr.upmc.alasca.computer.exceptions;

public class BadAddRequestException extends Exception {
	
	private static final long serialVersionUID = 4366415476890065164L;

	public BadAddRequestException()
	{
		super();
	}
	
	public BadAddRequestException(String message)
	{
		super(message);
	}
	
	public BadAddRequestException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
}
