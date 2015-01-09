package fr.upmc.alasca.appgenerator.ports;

import fr.upmc.alasca.controleur.interfaces.AppRequestI;
import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;

public class AppGeneratorOutboundPort extends AbstractOutboundPort
implements AppRequestI{

	public AppGeneratorOutboundPort(String uri,
			ComponentI owner) throws Exception {
		super(uri, AppRequestI.class, owner);
	}

	@Override
	public void acceptApplication(Integer application,
			String uri_new_requestGenerator) throws Exception {
		((AppRequestI) this.connector).acceptApplication(application, uri_new_requestGenerator);
	}

}
