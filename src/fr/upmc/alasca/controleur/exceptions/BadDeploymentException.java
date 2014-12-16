package fr.upmc.alasca.controleur.exceptions;

import java.rmi.RemoteException;

public class BadDeploymentException extends RemoteException {
	
	private static final long serialVersionUID = -5083689331391719975L;

	public BadDeploymentException()
	{
		super();
	}
	
	public BadDeploymentException(String message)
	{
		super(message);
	}
	
	public BadDeploymentException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
}
