package fr.upmc.alasca.repartiteur.ports;

import fr.upmc.alasca.controleur.interfaces.ControleurToRepartiteurProviderI;
import fr.upmc.alasca.repartiteur.components.Repartiteur;
import fr.upmc.alasca.repartiteur.interfaces.RepartiteurToControleurConsumerI;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;

public class RepartiteurToControleurOutboundPort extends AbstractOutboundPort
implements RepartiteurToControleurConsumerI {

	public RepartiteurToControleurOutboundPort(String uri, ComponentI owner)
			throws Exception {
		super(uri, RepartiteurToControleurConsumerI.class, owner);
	}

	

	@Override
	public void deployVM(int r, String[] uri, String RepartiteurURIDCC) throws Exception {
		((ControleurToRepartiteurProviderI)this.connector).deployVM(r, uri, RepartiteurURIDCC);
		
	}

}
