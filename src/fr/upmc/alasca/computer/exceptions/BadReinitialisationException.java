package fr.upmc.alasca.computer.exceptions;

public class BadReinitialisationException extends Exception {
	
	private static final long serialVersionUID = -8401861574612713618L;

	public BadReinitialisationException()
	{
		super();
	}
	
	public BadReinitialisationException(String message)
	{
		super(message);
	}
	
	public BadReinitialisationException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
}
