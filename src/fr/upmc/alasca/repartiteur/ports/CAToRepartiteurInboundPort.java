package fr.upmc.alasca.repartiteur.ports;

import fr.upmc.alasca.computer.objects.VMCarac;
import fr.upmc.alasca.computer.objects.VMMessages;
import fr.upmc.alasca.repartiteur.components.Repartiteur;
import fr.upmc.alasca.repartiteur.interfaces.RepartiteurProviderI;
import fr.upmc.alasca.requestgen.interfaces.RequestArrivalI;
import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractInboundPort;

public class CAToRepartiteurInboundPort extends AbstractInboundPort implements RepartiteurProviderI{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CAToRepartiteurInboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, RequestArrivalI.class, owner);
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
		Repartiteur rep = (Repartiteur) this.owner;
		return rep.addNewPorts(portURI);
	}

	/*@Override
	public void setVMConnection(String URIRep) throws Exception {
		Repartiteur rep = (Repartiteur) this.owner;
		rep.setVMConnection(URIRep);
	}*/
}
