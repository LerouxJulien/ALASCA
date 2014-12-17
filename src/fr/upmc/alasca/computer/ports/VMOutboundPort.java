package fr.upmc.alasca.computer.ports;

import fr.upmc.alasca.computer.interfaces.VMProviderI;
import fr.upmc.alasca.computer.objects.VMMessages;
import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;

public class VMOutboundPort extends AbstractOutboundPort
implements VMProviderI {

	public VMOutboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, VMProviderI.class, owner);
	}
	
	@Override
	public void notifyStatus(VMMessages m) throws Exception {
		((VMProviderI) this.connector).notifyStatus(m);
	}
	
}
