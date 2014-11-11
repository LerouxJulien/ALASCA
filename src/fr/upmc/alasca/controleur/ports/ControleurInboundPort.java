package fr.upmc.alasca.controleur.ports;

import fr.upmc.alasca.controleur.components.Controleur;
import fr.upmc.alasca.requestgen.interfaces.RequestArrivalI;
import fr.upmc.alasca.requestgen.objects.Request;
import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractInboundPort;

public class ControleurInboundPort extends AbstractInboundPort implements
		RequestArrivalI {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ControleurInboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, RequestArrivalI.class, owner);

		assert uri != null && owner != null;
		assert owner.isOfferedInterface(RequestArrivalI.class);
	}

	@Override
	public void acceptRequest(Request r) throws Exception {
		// TODO Auto-generated method stub
		Controleur c = (Controleur) this.owner;
		c.acceptRequest(r);
	}
}
