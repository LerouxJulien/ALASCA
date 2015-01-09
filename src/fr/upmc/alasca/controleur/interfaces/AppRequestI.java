package fr.upmc.alasca.controleur.interfaces;

import fr.upmc.components.interfaces.OfferedI;
import fr.upmc.components.interfaces.RequiredI;

public interface AppRequestI extends OfferedI, RequiredI{

	
	public void acceptApplication(Integer application, String uri_new_requestGenerator) throws Exception;
}
