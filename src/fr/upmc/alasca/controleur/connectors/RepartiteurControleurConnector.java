package fr.upmc.alasca.controleur.connectors;

import java.io.Serializable;

import fr.upmc.alasca.computer.interfaces.VMConsumerI;
import fr.upmc.alasca.computer.interfaces.VMProviderI;
import fr.upmc.alasca.controleur.interfaces.AppRequestI;
import fr.upmc.alasca.controleur.interfaces.ControleurToRepartiteurProviderI;
import fr.upmc.alasca.repartiteur.components.Repartiteur;
import fr.upmc.alasca.requestgen.objects.Request;
import fr.upmc.components.connectors.AbstractConnector;

public class RepartiteurControleurConnector extends AbstractConnector implements ControleurToRepartiteurProviderI,
 Serializable {

	private static final long serialVersionUID = 1L;

	
	@Override
	public void deployVM(int r, String[] uri, String RepartiteurURIDCC) throws Exception {
		((ControleurToRepartiteurProviderI)this.offering).deployVM(r, uri, RepartiteurURIDCC);
		
	}
}
