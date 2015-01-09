package fr.upmc.alasca.appgen.interfaces;

import fr.upmc.components.interfaces.OfferedI;

/**
 * L'interface <code>ApplicationGeneratorI</code> d�finit le protocol pour 
 * envoyer une demande d'ex�cution d'application au contr�leur du centre de 
 * calcul.
 *
 * <p>
 * <strong>Description</strong>
 * </p>
 * 
 * L'interface ne contient qu'une seule m�thode, <code>acceptApplication</code> 
 * qui passe l'id d'une application en param�tre.
 * 
 * <p>
 * Created on : 23 dec. 2014
 * </p>
 * 
 * @author <a href="mailto:Henri.Ng@etu.upmc.fr">Henri Ng/a>
 * @version $Name$ -- $Revision$ -- $Date$
 */
public interface ApplicationGeneratorI extends OfferedI {
	
	/**
	 * Accepte une nouvelle application
	 * 
	 * @param appID ID de l'application
	 * 
	 * @throws Exception
	 */
	public void acceptApplication(Integer appID) throws Exception;
	
}

