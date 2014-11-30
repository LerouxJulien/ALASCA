package fr.upmc.alasca.computer.interfaces;

import fr.upmc.alasca.computer.objects.VMMessages;
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
	 * @param r Requete transmise a la VM
	 * @throws Exception
	 */
	public void processRequest(Request r) throws Exception;

	/**
	 * Notifie le repartiteur de requetes de l'etat d'une VM et/ou de la fin de
	 * traitement d'une requete
	 * 
	 * @param m Notification de la VM au repartiteur de requetes
	 * @throws Exception
	 */
	public void notifyRR(VMMessages m) throws Exception;
	
}
