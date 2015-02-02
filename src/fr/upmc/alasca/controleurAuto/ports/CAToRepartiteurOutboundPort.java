package fr.upmc.alasca.controleurAuto.ports;

import fr.upmc.alasca.computer.objects.VMCarac;
import fr.upmc.alasca.computer.objects.VMMessages;
import fr.upmc.alasca.repartiteur.interfaces.RepartiteurProviderI;
import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;

public class CAToRepartiteurOutboundPort extends AbstractOutboundPort implements RepartiteurProviderI{

	public CAToRepartiteurOutboundPort(String uri, ComponentI owner)
			throws Exception {
		super(uri, RepartiteurProviderI.class, owner);
	}
	
	@Override
	public void notifyStatus(VMMessages m) throws Exception {
		/* to let empty */
	}

	@Override
	public void notifyCarac(String id, VMCarac c) throws Exception {
		/* to let empty */
	}

	@Override
	public String[] addNewPorts(String portURI) throws Exception {
		// TODO Auto-generated method stub
		return ((RepartiteurProviderI)this.connector).addNewPorts(portURI);
	}

	@Override
	public void setVMConnection(String URIRep) throws Exception {
		((RepartiteurProviderI)this.connector).setVMConnection(URIRep);
	}

}
