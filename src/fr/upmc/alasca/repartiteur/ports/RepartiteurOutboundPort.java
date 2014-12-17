package fr.upmc.alasca.repartiteur.ports;

import fr.upmc.alasca.computer.interfaces.VMConsumerI;
import fr.upmc.alasca.requestgen.objects.Request;
import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;

public class RepartiteurOutboundPort extends AbstractOutboundPort
implements VMConsumerI {

	public RepartiteurOutboundPort(String uri, ComponentI owner)
			throws Exception {
		super(uri, VMConsumerI.class, owner);
	}

	@Override
	public void processRequest(Request r) throws Exception {
		((VMConsumerI) this.connector).processRequest(r);
	}

}
