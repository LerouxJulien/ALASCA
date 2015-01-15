package fr.upmc.alasca.controleur.connectors;

import fr.upmc.alasca.controleur.interfaces.AppRequestI;
import fr.upmc.components.connectors.AbstractConnector;

/**
 * Classe <code>ApplicationGeneratorConnector</code>
 *
 * <p>
 * <strong>Description</strong>
 * </p>
 * 
 * <p>La classe <code>ApplicationGeneratorConnector</code> implémente le
 * connecteur entre le port de sortie du générateur d'applications et le port
 * d'entrée du contrôleur.</p>
 * 
 * <p>Created on : 23 dec. 2014</p>
 * 
 * @author <a href="mailto:Nicolas.Mounier@etu.upmc.fr">Nicolas Mounier/a>
 *         <a href="mailto:Henri.Ng@etu.upmc.fr">Henri Ng/a>
 * @version $Name$ -- $Revision$ -- $Date$
 */
public class ApplicationRequestConnector extends AbstractConnector implements
AppRequestI {

	@Override
	public void acceptApplication(Integer application, String thresholds,
			String uri_new_requestGenerator)
					throws Exception {
		((AppRequestI) this.offering).acceptApplication(application, thresholds,
				uri_new_requestGenerator);
	}

}
