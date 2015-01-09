package fr.upmc.alasca.controleur.ports;

import fr.upmc.alasca.controleur.components.Controleur;
import fr.upmc.alasca.controleur.interfaces.AppRequestI;
import fr.upmc.alasca.requestgen.interfaces.RequestArrivalI;
import fr.upmc.alasca.requestgen.objects.Request;
import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractInboundPort;

/**
 * Port par lequel le Controleur reçoit les requetes du generateur de requetes
 *
 */
public class ControleurInboundPort extends AbstractInboundPort implements
AppRequestI  {
	
	private static final long serialVersionUID = 1L;

	public ControleurInboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, RequestArrivalI.class, owner);

		assert uri != null && owner != null;
		assert owner.isOfferedInterface(RequestArrivalI.class);
	}

	@Override
	public void acceptApplication(Integer application,
			String uri_new_requestGenerator) throws Exception {
		Controleur c = (Controleur) this.owner;
		c.acceptApplication(application, uri_new_requestGenerator);
	}
}
