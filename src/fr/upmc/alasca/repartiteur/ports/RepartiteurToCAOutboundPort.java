package fr.upmc.alasca.repartiteur.ports;

import fr.upmc.alasca.controleurAuto.interfaces.CAProviderI;
import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;

public class RepartiteurToCAOutboundPort extends AbstractOutboundPort implements CAProviderI{

	public RepartiteurToCAOutboundPort(String uri, ComponentI owner)
			throws Exception {
		super(uri, CAProviderI.class, owner);
	}
	
	@Override
	public void deployFirstVM() throws Exception {
		((CAProviderI)this.connector).deployFirstVM();
	}

}