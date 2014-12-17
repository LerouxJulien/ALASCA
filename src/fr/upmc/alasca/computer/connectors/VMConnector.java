package fr.upmc.alasca.computer.connectors;

import java.io.Serializable;

import fr.upmc.alasca.computer.interfaces.VMProviderI;
import fr.upmc.alasca.computer.objects.VMMessages;
import fr.upmc.alasca.computer.ports.VMInboudPort;
import fr.upmc.alasca.repartiteur.ports.RepartiteurOutboundPort;
import fr.upmc.alasca.requestgen.objects.Request;
import fr.upmc.components.connectors.AbstractTwoWayConnector;

/**
 * La classe VMConnector implemente un connecteur entre un Repartiteur et une
 * VirtualMachine
 * */
public class VMConnector extends AbstractTwoWayConnector implements VMProviderI,
		Serializable {

	private static final long serialVersionUID = 1L;

	@Override
	public void processRequest(Request r) throws Exception {
		((VMInboudPort) this.offering).processRequest(r);
	}

	@Override
	public void notifyRR(VMMessages m) throws Exception {
		((RepartiteurOutboundPort) this.requiring).notifyRR(m);
	}
	
}
