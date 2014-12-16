package fr.upmc.alasca.computer.exceptions;

import java.rmi.RemoteException;

public class BadAddRequestException extends RemoteException {
	
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
