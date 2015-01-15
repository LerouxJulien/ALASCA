package fr.upmc.alasca.repartiteur.ports;

import java.rmi.RemoteException;

import fr.upmc.alasca.computer.exceptions.BadDestroyException;
import fr.upmc.alasca.controleur.interfaces.ControleurFromRepartiteurProviderI;
import fr.upmc.alasca.repartiteur.components.Repartiteur;
import fr.upmc.alasca.repartiteur.interfaces.RepartiteurToControleurConsumerI;
import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;


/**
 * 
 * Port de sortie du repartiteur vers le controleur
 * 
 * @author Julien Leroux
 *
 */
public class RepartiteurToControleurOutboundPort extends AbstractOutboundPort
implements RepartiteurToControleurConsumerI {

	public RepartiteurToControleurOutboundPort(String uri, ComponentI owner)
			throws Exception {
		super(uri, RepartiteurToControleurConsumerI.class, owner);
	}

	

	@Override
	public void deployVM(int r, String[] uri, String RepartiteurURIDCC) throws Exception {
		((ControleurFromRepartiteurProviderI)this.connector).deployVM(r, uri, RepartiteurURIDCC);
		
	}



	@Override
	public void destroyVM(String uriComputerParent, String vm) throws Exception {
		((ControleurFromRepartiteurProviderI)this.connector).destroyVM(uriComputerParent, vm);
	}

	
	@Override
	public void incFrequency(int app) throws Exception {
		((ControleurFromRepartiteurProviderI)this.connector).incFrequency(app);
	}
}
