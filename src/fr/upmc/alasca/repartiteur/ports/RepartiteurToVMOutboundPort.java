package fr.upmc.alasca.repartiteur.ports;

import fr.upmc.alasca.computer.interfaces.VMConsumerI;
import fr.upmc.alasca.computer.interfaces.VMProviderI;
import fr.upmc.alasca.repartiteur.interfaces.RepartiteurConsumerI;
import fr.upmc.alasca.repartiteur.interfaces.RepartiteurProviderI;
import fr.upmc.alasca.requestgen.objects.Request;
import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;

/**
 * Port de sortie du repartiteur vers une VM
 * 
 * @author Julien Leroux
 *
 */
public class RepartiteurToVMOutboundPort extends AbstractOutboundPort
implements RepartiteurConsumerI {

	public RepartiteurToVMOutboundPort(String uri, ComponentI owner)
			throws Exception {
		super(uri, RepartiteurConsumerI.class, owner);
	}

	@Override
	public void processRequest(Request r) throws Exception {
		((VMProviderI) this.connector).processRequest(r);
	}

	@Override
	public String getVMURI() throws Exception {
		return ((VMProviderI) this.connector).getVMURI();
		
		
	}

	public void startNotification() throws Exception {
		
		((VMProviderI) this.connector).startNotification();
		
	}

	public int getNbCore() throws Exception {
		
		return ((VMProviderI) this.connector).getNbCores();
		
	}
	
	public String getUriComputerParent() throws Exception{
		return ((VMProviderI) this.connector).getUriComputerParent();
	}
}
