package fr.upmc.alasca.repartiteur.ports;

import fr.upmc.alasca.computer.interfaces.VMProviderI;
import fr.upmc.alasca.computer.objects.VMMessages;
import fr.upmc.alasca.repartiteur.components.Repartiteur;
import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractInboundPort;

public class RepartiteurInboundPort extends AbstractInboundPort
implements VMProviderI {

	private static final long serialVersionUID = 8210006640377358437L;

	public RepartiteurInboundPort(String uri, ComponentI owner)
			throws Exception {
		super(uri, VMProviderI.class, owner);
	}

	@Override
	public void notifyStatus(VMMessages m) throws Exception {
		//m.setRepPort(this);
		Repartiteur rep = (Repartiteur) this.owner;
		rep.notifyStatus(m);
	}
	
}
