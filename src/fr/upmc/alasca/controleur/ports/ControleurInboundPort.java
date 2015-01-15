package fr.upmc.alasca.controleur.ports;

import fr.upmc.alasca.controleur.components.Controleur;
import fr.upmc.alasca.controleur.interfaces.AppRequestI;
import fr.upmc.alasca.requestgen.interfaces.RequestArrivalI;
import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractInboundPort;

/**
 * Classe <code>ControleurInboundPort</code>
 *
 * <p>
 * <strong>Description</strong>
 * </p>
 * 
 * <p>La classe <code>ControleurInboundPort</code> implémente le port par lequel 
 * le contrôleur reçoit les requêtes du générateur de requêtes.</p>
 * 
 * <p>Created on : 23 dec. 2014</p>
 * 
 * @author <a href="mailto:Nicolas.Mounier@etu.upmc.fr">Nicolas Mounier/a>
 *         <a href="mailto:Henri.Ng@etu.upmc.fr">Henri Ng/a>
 * @version $Name$ -- $Revision$ -- $Date$
 */
public class ControleurInboundPort extends AbstractInboundPort implements
AppRequestI {
	
	private static final long serialVersionUID = 1L;

	public ControleurInboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, RequestArrivalI.class, owner);

		assert uri != null && owner != null;
		assert owner.isOfferedInterface(RequestArrivalI.class);
	}

	@Override
	public void acceptApplication(Integer application, String thresholds,
			String uri_new_requestGenerator)
					throws Exception {
		Controleur c = (Controleur) this.owner;
		c.acceptApplication(application, thresholds, uri_new_requestGenerator);
	}
}
