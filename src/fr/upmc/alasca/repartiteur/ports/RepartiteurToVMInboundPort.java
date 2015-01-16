package fr.upmc.alasca.repartiteur.ports;

import fr.upmc.alasca.computer.objects.VMCarac;
import fr.upmc.alasca.computer.objects.VMMessages;
import fr.upmc.alasca.repartiteur.components.Repartiteur;
import fr.upmc.alasca.repartiteur.interfaces.RepartiteurProviderI;
import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractInboundPort;

/**
 * Port d'entrée du repartiteur venant d'une VM
 * 
 * @author Julien Leroux
 *
 */
public class RepartiteurToVMInboundPort extends AbstractInboundPort
implements RepartiteurProviderI {

	private static final long serialVersionUID = 8210006640377358437L;

	public RepartiteurToVMInboundPort(String uri, ComponentI owner)
			throws Exception {
		super(uri, RepartiteurProviderI.class, owner);
	}

	@Override
	public void notifyStatus(VMMessages m) throws Exception {
		m.setRepPort(this);
		Repartiteur rep = (Repartiteur) this.owner;
		rep.notifyStatus(m);
	}

	@Override
	public void notifyCarac(String id,VMCarac c) throws Exception {
		Repartiteur rep = (Repartiteur) this.owner;
		rep.notifyCarac(id, c);
	}
	
}
