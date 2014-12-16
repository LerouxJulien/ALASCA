package fr.upmc.alasca.computer.exceptions;

public class BadDestroyException extends Exception {
	
	private static final long serialVersionUID = 6350760457368543110L;

	public BadDestroyException()
	{
		super();
	}
	
	public BadDestroyException(String message)
	{
		super(message);
	}
	
	public BadDestroyException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
}
