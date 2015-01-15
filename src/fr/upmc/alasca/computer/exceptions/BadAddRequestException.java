package fr.upmc.alasca.computer.exceptions;

/**
 * La classe <code>BadAddRequestException</code> est une <code>Exception</code> levée en cas d'impossibilité
 * d'ajouter une nouvelle requête
 */
public class BadAddRequestException extends Exception {
	
	private static final long serialVersionUID = 4366415476890065164L;

	/**
	 * Constructeur identique au constructeur Exception()
	 */
	public BadAddRequestException()
	{
		super();
	}
	
	/**
	 * Constructeur identique au constructeur Exception(String message)
	 * 
	 * @param message le message a afficher a la levée de l'exception
	 */
	public BadAddRequestException(String message)
	{
		super(message);
	}
	
	/**
	 * Constructeur identique au constructeur Exception(String message, Throwable cause)
	 * 
	 * @param message le message a afficher a la levée d'une exception
	 * @param cause la cause de cette levée d'exception
	 */
	public BadAddRequestException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
}
