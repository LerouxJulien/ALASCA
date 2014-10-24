package fr.upmc.alasca.controleurAdmission.interfaces;

import java.util.List;

import fr.upmc.alasca.requestgen.objects.Request;
import fr.upmc.components.interfaces.RequiredI;

/**
 * L'interface <code>ControleurConsumerComputerI</code>
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 * 
 * <p>Created on : 10 oct. 2014</p>
 * 
 * @author	<a href="mailto:william.chasson@etu.upmc.fr">William CHASSON</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public interface ControleurConsumerComputerI extends RequiredI {
	/**
	 * Alloue une machine virtuelle
	 * 
	 * @param nbCores
	 * @param app
	 * @param RepartiteurURI necessaire pour connecter le repartiteur et la vm
	 * @return boolean
	 */
	public boolean deployVM(int nbCores, int app, String RepartiteurURI) throws Exception;
	
	/**
	 * Detruit une machine virtuelle via son URI
	 *  
	 * @param mv
	 * @return boolean
	 */
	public boolean destroyVM(String mv) throws Exception;
	
	/**
	 * Envoie la liste des URI des machines virtuelles allouees
	 * 
	 * @return boolean
	 */
	public List<String> getListVM() throws Exception;
	
	/**
	 *  Recupere une requete envoyee par le repartiteur de requetes
	 *  
	 * @param mv
	 * @param req
	 * @return boolean
	 */
	public boolean getRequest(String mv, Request req) throws Exception;
	
	/**
	 * Reinitialise une machine virtuelle via son URI
	 * 
	 * @param vm
	 * @return boolean
	 */
	public boolean reInit(String vm) throws Exception;
	
	/**
	 * Renvoi le nombre de nouvelle VM pouvant être créée sur le Computer
	 * 
	 * @return int
	 */
	public int nbVMDispo();

	public String getURINewVM();

}
