package fr.upmc.alasca.repartiteur.ports;

import fr.upmc.alasca.computer.interfaces.VMProviderI;
import fr.upmc.alasca.computer.objects.VMMessages;
import fr.upmc.alasca.repartiteur.components.Repartiteur;
import fr.upmc.alasca.requestgen.objects.Request;
import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractTwoWayPort;

public class RepartiteurOutboundPort extends AbstractTwoWayPort implements
		VMProviderI {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1307494045619019507L;

	public RepartiteurOutboundPort(String uri, ComponentI owner)
			throws Exception {
		super(uri, VMProviderI.class, owner);
	}

	@Override
	public void processRequest(Request r) throws Exception {
		((VMProviderI) this.connector).processRequest(r);
	}
	
	@Override
	public void notifyRR(VMMessages m) throws Exception {
		m.setRepPort(this);
		Repartiteur rep = (Repartiteur) this.owner;
		rep.notifyRR(m);
	}

}
