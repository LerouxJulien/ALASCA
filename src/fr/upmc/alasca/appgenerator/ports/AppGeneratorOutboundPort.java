package fr.upmc.alasca.appgenerator.ports;

import fr.upmc.alasca.controleur.interfaces.AppRequestI;
import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;

/**
 * La classe <code>AppGeneratorOutboundPort</code> implémente le port de
 * sorti pour le composant envoyant l'ID d'une application vers le contrôleur.
 *
 * <p>
 * <strong>Description</strong>
 * </p>
 * 
 * Le port implemente l'interface <code>AppRequestI</code>.
 *
 * <p>
 * Created on : 23 dec. 2014
 * </p>
 * 
 * @author <a href="mailto:Nicolas.Mounier@etu.upmc.fr">Nicolas Mounier/a>
 *         <a href="mailto:Henri.Ng@etu.upmc.fr">Henri Ng/a>
 * @version $Name$ -- $Revision$ -- $Date$
 */
public class AppGeneratorOutboundPort extends AbstractOutboundPort
implements AppRequestI{

	public AppGeneratorOutboundPort(String uri, ComponentI owner)
			throws Exception {
		super(uri, AppRequestI.class, owner);
	}

	@Override
	public void acceptApplication(Integer application, String thresholds,
			String uri_new_requestGenerator) throws Exception {
		((AppRequestI) this.connector).acceptApplication(application,
				thresholds, uri_new_requestGenerator);
	}

}
