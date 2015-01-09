package fr.upmc.alasca.controleur.ports;

import fr.upmc.alasca.appgen.interfaces.ApplicationGeneratorI;
import fr.upmc.alasca.controleur.components.Controleur;
import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractInboundPort;

/**
 * Port par lequel le Controleur reçoit les ID des applications à lancer
 *
 */
public class ControleurAppGenInboundPort extends AbstractInboundPort implements
		ApplicationGeneratorI {
	
	private static final long serialVersionUID = 1L;

	public ControleurAppGenInboundPort(String uri, ComponentI owner)
			throws Exception {
		super(uri, ApplicationGeneratorI.class, owner);

		assert uri != null && owner != null;
		assert owner.isOfferedInterface(ApplicationGeneratorI.class);
	}

	@Override
	public void acceptApplication(Integer appID) throws Exception {
		Controleur c = (Controleur) this.owner;
		c.acceptApplication(appID);
	}
	
}
