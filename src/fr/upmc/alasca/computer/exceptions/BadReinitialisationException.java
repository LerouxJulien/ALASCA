package fr.upmc.alasca.computer.exceptions;

/**
 * La classe <code>BadReinitialisationException</code> est une <code>Exception</code> levée en cas d'impossibilité
 * de reinitialiser une <code>VirtualMachine</code>
 */
public class BadReinitialisationException extends Exception {
	
	private static final long serialVersionUID = -8401861574612713618L;

	/**
	 * Constructeur identique au constructeur Exception()
	 */
	public BadReinitialisationException()
	{
		super();
	}
	
	/**
	 * Constructeur identique au constructeur Exception(String message)
	 * 
	 * @param message le message a afficher a la levée de l'exception
	 */
	public BadReinitialisationException(String message)
	{
		super(message);
	}
	
	/**
	 * Constructeur identique au constructeur Exception(String message, Throwable cause)
	 * 
	 * @param message le message a afficher a la levée d'une exception
	 * @param cause la cause de cette levée d'exception
	 */
	public BadReinitialisationException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
}
