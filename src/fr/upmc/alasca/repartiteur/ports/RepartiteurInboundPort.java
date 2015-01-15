package fr.upmc.alasca.repartiteur.ports;

import fr.upmc.alasca.repartiteur.components.Repartiteur;
import fr.upmc.alasca.requestgen.interfaces.RequestArrivalI;
import fr.upmc.alasca.requestgen.objects.Request;
import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractInboundPort;

/**
 * Port d'entree du repartiteur venant du generateur de requete
 * 
 * @author Julien Leroux
 *
 */
public class RepartiteurInboundPort extends AbstractInboundPort implements
		RequestArrivalI {

	private static final long serialVersionUID = 1L;

	public RepartiteurInboundPort(String uri, ComponentI owner)
			throws Exception {
		super(uri, RequestArrivalI.class, owner);

		assert uri != null && owner != null;
		assert owner.isOfferedInterface(RequestArrivalI.class);
	}

	@Override
	public void acceptRequest(Request r) throws Exception {
		Repartiteur rr = (Repartiteur) this.owner;
		rr.acceptRequest(r);
	}
}