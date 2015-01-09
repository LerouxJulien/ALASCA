package fr.upmc.alasca.appgen.interfaces;

import fr.upmc.components.interfaces.OfferedI;

/**
 * L'interface <code>ApplicationGeneratorI</code> définit le protocol pour 
 * envoyer une demande d'exécution d'application au contrôleur du centre de 
 * calcul.
 *
 * <p>
 * <strong>Description</strong>
 * </p>
 * 
 * L'interface ne contient qu'une seule méthode, <code>acceptApplication</code> 
 * qui passe l'id d'une application en paramètre.
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

