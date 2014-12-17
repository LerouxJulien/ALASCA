package fr.upmc.alasca.computer.interfaces;

import fr.upmc.alasca.requestgen.objects.Request;
import fr.upmc.components.interfaces.RequiredI;

public interface VMConsumerI extends RequiredI {

	/**
	 * Transmet la requete a la VM
	 * 
	 * @param r Requete transmise a la VM
	 * @throws Exception
	 */
	public void processRequest(Request r) throws Exception;

}
