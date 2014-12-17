package fr.upmc.alasca.repartiteur.interfaces;

import fr.upmc.alasca.requestgen.objects.Request;

public interface RepartiteurProviderI {

	/**
	 * Transmet la requete a la VM
	 * 
	 * @param r Requete transmise a la VM
	 * @throws Exception
	 */
	public void processRequest(Request r) throws Exception;
	
}
