package fr.upmc.alasca.repartiteur.interfaces;

import fr.upmc.alasca.requestgen.objects.Request;
import fr.upmc.components.interfaces.OfferedI;
import fr.upmc.components.interfaces.RequiredI;

public interface RepartiteurProviderI extends OfferedI{

	/**
	 * Transmet la requete a la VM
	 * 
	 * @param r Requete transmise a la VM
	 * @throws Exception
	 */
	public void processRequest(Request r) throws Exception;
	
}
