package fr.upmc.alasca.computer.exceptions;

import java.rmi.RemoteException;

public class NotEnoughCapacityVMException extends RemoteException {
	
	private static final long serialVersionUID = 1190925171580256858L;

	public NotEnoughCapacityVMException()
	{
		super();
	}
	
	public NotEnoughCapacityVMException(String message)
	{
		super(message);
	}
	
	public NotEnoughCapacityVMException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
}
