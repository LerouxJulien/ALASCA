package fr.upmc.alasca.computer.exceptions;

import java.rmi.RemoteException;

public class BadReinitialisationException extends RemoteException {
	
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
