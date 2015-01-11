package fr.upmc.alasca.computer.ports;

import fr.upmc.alasca.computer.interfaces.VMConsumerI;
import fr.upmc.alasca.computer.interfaces.VMProviderI;
import fr.upmc.alasca.computer.objects.VMCarac;
import fr.upmc.alasca.computer.objects.VMMessages;
import fr.upmc.alasca.repartiteur.interfaces.RepartiteurProviderI;
import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;

public class VMOutboundPort extends AbstractOutboundPort
implements VMConsumerI {

	public VMOutboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, VMConsumerI.class, owner);
	}
	
	@Override
	public void notifyStatus(VMMessages m) throws Exception {
		
		((RepartiteurProviderI) this.connector).notifyStatus(m);
	}

	public void notifyCarac(String id, VMCarac c) throws Exception {
		( (RepartiteurProviderI) this.connector).notifyCarac(id,c);
	}
	
}
