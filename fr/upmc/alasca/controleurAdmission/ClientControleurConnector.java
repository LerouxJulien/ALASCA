package fr.upmc.alasca.controleurAdmission;

import fr.upmc.alasca.controleurAdmission.interfaces.ControleurProviderClientI;
import fr.upmc.alasca.requestgen.interfaces.RequestArrivalI;
import fr.upmc.alasca.requestgen.objects.Request;
import fr.upmc.components.connectors.AbstractConnector;

/**
 * La classe <code>ControleurClientConnector</code> définit le connecteur entre l'interface 
 * offerte <code>ControleurProviderClientI</code> et l'interface requise <code>RequestArrivalI</code>
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

public class ClientControleurConnector extends AbstractConnector implements RequestArrivalI{

	/**
	 * fonction de liaison pour la méthode acceptRequest
	 * @param r la requête a récupérer
	 * @throws Exception
	 */
	public void acceptRequest(Request r) throws Exception {
		((ControleurProviderClientI) this.offering).acceptRequest(r);
	}
	
	/**
	 * fonction de liaison pour la méthode acceptApplication
	 * @param id l'application a récupérer
	 * @throws Exception
	 */
	public void acceptApplication(int id) throws Exception {
		((ControleurProviderClientI) this.offering).acceptApplication(id);
	}
	
}
