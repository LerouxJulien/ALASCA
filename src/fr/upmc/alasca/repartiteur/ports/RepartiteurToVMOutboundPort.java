package fr.upmc.alasca.repartiteur.ports;

import fr.upmc.alasca.computer.interfaces.VMConsumerI;
import fr.upmc.alasca.repartiteur.interfaces.RepartiteurProviderI;
import fr.upmc.alasca.requestgen.objects.Request;
import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;

public class RepartiteurToVMOutboundPort extends AbstractOutboundPort
implements RepartiteurProviderI {

	public RepartiteurToVMOutboundPort(String uri, ComponentI owner)
			throws Exception {
		super(uri, RepartiteurProviderI.class, owner);
	}

	@Override
	public void processRequest(Request r) throws Exception {
		((VMConsumerI) this.connector).processRequest(r);
	}

}
