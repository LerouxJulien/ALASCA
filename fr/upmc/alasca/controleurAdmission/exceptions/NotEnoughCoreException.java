package fr.upmc.alasca.controleurAdmission.exceptions;

/**
 * La classe <code>NotEnoughCoreException</code> implémente l'exception levée si tous les coeurs de tous les
 * <code>Computer</code> sont occupés, La requête ne peut pas être traitée.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 * 
 * <p>Created on : 10 oct. 2014</p>
 * 
 * @author	<a href="mailto:william.chasson@etu.upmc.fr">William CHASSON</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */

public class NotEnoughCoreException extends Exception {
	private static final long serialVersionUID = 1L;

	/**
	 * Création de l'exception
	 * @param s le message d'erreur
	 */
	public NotEnoughCoreException(String s){
		super(s);
	}
}
