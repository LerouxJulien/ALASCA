package fr.upmc.alasca.controleurAdmission.interfaces;

import fr.upmc.alasca.requestgen.objects.Request;
import fr.upmc.components.interfaces.OfferedI;

/**
 * L'interface <code>ControleurProviderClientI</code> définit les fonctions du composant <code>Controleur</code>
 *  offertes pour les composants <code>Client</code>
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

public interface ControleurProviderClientI extends OfferedI{
	
	/**
	 * fonction permettant de récupérer une requête client.
	 * @param r la requête a récupérer
	 * @throws Exception
	 */
	public void acceptRequest(Request r) throws Exception;
	
	/**
	 * fonction permettant de récupérer une nouvelle application client
	 * @param id l'application a récupérer
	 * @throws Exception
	 */
	public void acceptApplication(int id) throws Exception;
}
