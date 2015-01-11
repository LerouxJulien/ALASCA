package fr.upmc.alasca.computer.connectors;

import java.io.Serializable;

import fr.upmc.alasca.computer.interfaces.VMConsumerI;
import fr.upmc.alasca.computer.interfaces.VMProviderI;
import fr.upmc.alasca.computer.objects.VMCarac;
import fr.upmc.alasca.computer.objects.VMMessages;
import fr.upmc.alasca.computer.ports.VMInboundPort;
import fr.upmc.alasca.repartiteur.interfaces.RepartiteurProviderI;
import fr.upmc.alasca.repartiteur.ports.RepartiteurToVMInboundPort;
import fr.upmc.alasca.requestgen.objects.Request;
import fr.upmc.components.connectors.AbstractConnector;

/**
 * La classe VMConnector implemente un connecteur entre un Repartiteur et une
 * VirtualMachine
 * */
public class VMConnector extends AbstractConnector implements VMProviderI,
VMConsumerI, Serializable {

	private static final long serialVersionUID = 1L;

	@Override
	public void processRequest(Request r) throws Exception {
		((VMProviderI) this.offering).processRequest(r);
	}
	
	@Override
	public String getVMURI() throws Exception {
		return ((VMProviderI) this.offering).getVMURI();
	}
	
	@Override
	public void startNotification() throws Exception {
		((VMProviderI) this.offering).startNotification();
		
	}
	
	@Override
	public void notifyStatus(VMMessages m) throws Exception { 
		((RepartiteurProviderI) this.requiring).notifyStatus(m);
	}

	@Override
	public void notifyCarac(String id, VMCarac c) throws Exception{
		((RepartiteurProviderI) this.requiring).notifyCarac(id, c);
		
	}

	
}
