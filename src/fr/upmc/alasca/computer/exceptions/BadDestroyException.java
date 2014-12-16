package fr.upmc.alasca.computer.exceptions;

import java.rmi.RemoteException;

public class BadDestroyException extends RemoteException {
	
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
