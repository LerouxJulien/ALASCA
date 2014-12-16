package fr.upmc.alasca.controleur.exceptions;

public class NoRepartitorException extends Exception {
	
	private static final long serialVersionUID = 6082668585322329491L;

	public NoRepartitorException()
	{
		super();
	}
	
	public NoRepartitorException(String message)
	{
		super(message);
	}
	
	public NoRepartitorException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
}
