package fr.upmc.alasca.computer.interfaces;

import fr.upmc.alasca.requestgen.objects.Request;
import fr.upmc.components.interfaces.OfferedI;
import fr.upmc.components.interfaces.RequiredI;

/**
 * 
 */
public interface VMProviderI extends OfferedI, RequiredI {

	/**
	 * Transmet la requete a la VM
	 * 
	 * @param r
	 *            Requete transmise a la VM
	 * @throws Exception
	 */
	public void processRequest(Request r) throws Exception;

	/**
	 * @return true si la queue de la VM est pleine
	 * @throws Exception
	 */
	public boolean queueIsFull() throws Exception;
}
