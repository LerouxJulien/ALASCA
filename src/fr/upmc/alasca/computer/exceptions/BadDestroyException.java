package fr.upmc.alasca.computer.exceptions;

/**
 * La classe <code>BadDestroyException</code> est une <code>Exception</code> levée en cas d'impossibilité
 * de detruire une <code>VirtualMachine</code>
 */
public class BadDestroyException extends Exception {
	
	private static final long serialVersionUID = 6350760457368543110L;

	/**
	 * Constructeur identique au constructeur Exception()
	 */
	public BadDestroyException()
	{
		super();
	}
	
	/**
	 * Constructeur identique au constructeur Exception(String message)
	 * 
	 * @param message le message a afficher a la levée de l'exception
	 */
	public BadDestroyException(String message)
	{
		super(message);
	}
	
	/**
	 * Constructeur identique au constructeur Exception(String message, Throwable cause)
	 * 
	 * @param message le message a afficher a la levée d'une exception
	 * @param cause la cause de cette levée d'exception
	 */
	public BadDestroyException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
}
