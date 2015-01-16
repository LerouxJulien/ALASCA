package fr.upmc.alasca.repartiteur.connectors;

import java.io.Serializable;

import fr.upmc.alasca.computer.objects.VMCarac;
import fr.upmc.alasca.computer.objects.VMMessages;
import fr.upmc.alasca.repartiteur.interfaces.RepartiteurProviderI;
import fr.upmc.components.connectors.AbstractConnector;

/**
 * Connecteur du repartiteur
 * 
 * @author Julien Leroux
 */
public class RepartiteurConnector extends AbstractConnector implements 
RepartiteurProviderI, Serializable {

	private static final long serialVersionUID = -651699635097926566L;

	@Override
	public void notifyStatus(VMMessages m) throws Exception { 
		((RepartiteurProviderI) this.offering).notifyStatus(m);
	}

	@Override
	public void notifyCarac(String id, VMCarac c) throws Exception{
		((RepartiteurProviderI) this.offering).notifyCarac(id, c);
	}

}
