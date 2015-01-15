package fr.upmc.alasca.computer.interfaces;

import fr.upmc.alasca.requestgen.objects.Request;
import fr.upmc.components.interfaces.OfferedI;

/**
 * L'interface <code>VMConsumerI</code>, offre des fonctions du composant <code>VirtualMachine</code>
 */
public interface VMProviderI extends OfferedI {

	/**
	 * Transmet la requete a la VM
	 * 
	 * @param r Requete transmise a la VM
	 * @throws Exception
	 */
	public void processRequest(Request r) throws Exception;

	/**
	 * Retourne l'URI de la VM
	 * 
	 * @return l'URI de la VM
	 * @throws Exception
	 */
	public String getVMURI() throws Exception;

	/**
	 * Créer et envoi les notifications (<code>VMMessages</code> et <code>VMCarac</code>)
	 *  sur l'OutboundPort de la VM
	 *  
	 * @throws Exception
	 */
	public void startNotification() throws Exception;
	
	/**
	 * Retourne l'URI du Computer parent de la VM
	 * 
	 * @return l'URI du Computer parent de la VM
	 * @throws Exception
	 */
	public String getUriComputerParent() throws Exception;
	
	/**
	 * Retourne le nombre de coeurs de la VM
	 * 
	 * @return le nombre de coeurs de la VM
	 * @throws Exception
	 */
	public int getNbCores() throws Exception;
	
	/**
	 * Appelle la fonction shutdown de <code>AbstractComponent</code>, arrête la VM
	 * 
	 * @throws Exception
	 */
	public void shutdown() throws  Exception;
	
}
