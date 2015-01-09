package fr.upmc.alasca.appgen.ports;

import fr.upmc.alasca.appgen.interfaces.ApplicationGeneratorI;
import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;

/**
 * La classe <code>ApplicationGeneratorOutboundPort</code> implemente le port de
 * sorti pour le composant envoyant l'ID d'une application vers le controleur.
 *
 * <p>
 * <strong>Description</strong>
 * </p>
 * 
 * Le port implemente l'interface <code>ApplicationGeneratorI</code>.
 *
 * <p>
 * Created on : 23 dec. 2014
 * </p>
 * 
 * @author <a href="mailto:Henri.Ng@etu.upmc.fr">Henri Ng/a>
 * @version $Name$ -- $Revision$ -- $Date$
 */
public class ApplicationGeneratorOutboundPort extends AbstractOutboundPort
		implements ApplicationGeneratorI {
	
	/**
	 * Créé le port de son propre composant avec son URI
	 *
	 * @param uri
	 * @param owner
	 * 
	 * @throws Exception
	 */
	public ApplicationGeneratorOutboundPort(String uri, ComponentI owner)
			throws Exception {
		super(uri, ApplicationGeneratorI.class, owner);
	}

	/**
	 * Passe l'ID de l'application au connecteur
	 * 
	 * @param appID ID de l'application
	 * 
	 * @throws Exception
	 */
	@Override
	public void acceptApplication(Integer appID) throws Exception {
		((ApplicationGeneratorI) this.connector).acceptApplication(appID);
	}
	
}