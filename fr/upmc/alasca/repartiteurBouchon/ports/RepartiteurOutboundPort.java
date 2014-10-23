package fr.upmc.alasca.repartiteurBouchon.ports;

import fr.upmc.alasca.computer.interfaces.VMProviderI;
import fr.upmc.alasca.requestgen.objects.Request;
import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;

public class RepartiteurOutboundPort extends		AbstractOutboundPort implements VMProviderI{

	public RepartiteurOutboundPort(String uri,
		ComponentI owner) throws Exception {
		super(uri, VMProviderI.class, owner) ;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void processRequest(Request r) throws Exception {
		((VMProviderI)this.connector).processRequest(r);
	}
	

}
