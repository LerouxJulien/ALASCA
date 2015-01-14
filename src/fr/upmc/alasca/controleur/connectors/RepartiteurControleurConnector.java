package fr.upmc.alasca.controleur.connectors;

import java.io.Serializable;
import java.rmi.RemoteException;

import fr.upmc.alasca.computer.exceptions.BadDestroyException;
import fr.upmc.alasca.computer.interfaces.VMConsumerI;
import fr.upmc.alasca.computer.interfaces.VMProviderI;
import fr.upmc.alasca.controleur.interfaces.AppRequestI;
import fr.upmc.alasca.controleur.interfaces.ControleurFromRepartiteurProviderI;
import fr.upmc.alasca.repartiteur.components.Repartiteur;
import fr.upmc.alasca.requestgen.objects.Request;
import fr.upmc.components.connectors.AbstractConnector;

public class RepartiteurControleurConnector extends AbstractConnector implements ControleurFromRepartiteurProviderI,
 Serializable {

	private static final long serialVersionUID = 1L;

	
	@Override
	public void deployVM(int r, String[] uri, String RepartiteurURIDCC) throws Exception {
		((ControleurFromRepartiteurProviderI)this.offering).deployVM(r, uri, RepartiteurURIDCC);
		
	}
	
	@Override
	public void destroyVM(String uriComputerParent, String vm) throws Exception {
		((ControleurFromRepartiteurProviderI)this.offering).destroyVM(uriComputerParent, vm);
	}
}
