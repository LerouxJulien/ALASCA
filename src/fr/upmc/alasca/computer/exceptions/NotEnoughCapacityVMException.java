package fr.upmc.alasca.computer.exceptions;

/**
 * La classe <code>NotEnoughCapacityVMException</code> est une <code>Exception</code> lev�e en cas d'impossibilit�
 * de cr�er une nouvelle <code>VirtualMachine</code> (plus assez de coeurs physiques disponibles)
 */
public class NotEnoughCapacityVMException extends Exception {
	
	private static final long serialVersionUID = 1190925171580256858L;

	/**
	 * Constructeur identique au constructeur Exception()
	 */
	public NotEnoughCapacityVMException()
	{
		super();
	}
	
	/**
	 * Constructeur identique au constructeur Exception(String message)
	 * 
	 * @param message le message a afficher a la lev�e de l'exception
	 */
	public NotEnoughCapacityVMException(String message)
	{
		super(message);
	}
	
	/**
	 * Constructeur identique au constructeur Exception(String message, Throwable cause)
	 * 
	 * @param message le message a afficher a la lev�e d'une exception
	 * @param cause la cause de cette lev�e d'exception
	 */
	public NotEnoughCapacityVMException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
}
