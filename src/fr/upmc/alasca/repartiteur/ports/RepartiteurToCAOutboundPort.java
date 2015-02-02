package fr.upmc.alasca.repartiteur.ports;

import fr.upmc.alasca.computer.objects.VMCarac;
import fr.upmc.alasca.computer.objects.VMMessages;
import fr.upmc.alasca.repartiteur.interfaces.RepartiteurProviderI;
import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;

public class RepartiteurToCAOutboundPort extends AbstractOutboundPort implements RepartiteurProviderI{

	public RepartiteurToCAOutboundPort(String uri, ComponentI owner)
			throws Exception {
		super(uri, RepartiteurProviderI.class, owner);
	}
	
	@Override
	public void notifyStatus(VMMessages m) throws Exception {
		((RepartiteurProviderI)this.connector).notifyStatus(m);
	}

	@Override
	public void notifyCarac(String id, VMCarac c) throws Exception {
		((RepartiteurProviderI)this.connector).notifyCarac(id, c);
	}

	@Override
	public String[] addNewPorts(String portURI) throws Exception {
		/* to let empty */
		return null;
	}

	@Override
	public void setVMConnection(String URIRep) throws Exception {
		/* to let empty */
	}

}