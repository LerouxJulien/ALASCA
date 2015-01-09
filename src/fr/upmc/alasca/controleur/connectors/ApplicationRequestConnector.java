package fr.upmc.alasca.controleur.connectors;

import fr.upmc.alasca.controleur.interfaces.AppRequestI;
import fr.upmc.components.connectors.AbstractConnector;

public class ApplicationRequestConnector extends AbstractConnector implements
AppRequestI{

	@Override
	public void acceptApplication(Integer application,
			String uri_new_requestGenerator) throws Exception {
		((AppRequestI) this.offering).acceptApplication(application,uri_new_requestGenerator);
	}

}
