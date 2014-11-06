package fr.upmc.alasca.computer.interfaces;

import fr.upmc.components.interfaces.OfferedI;

/**
 * L'interface <code>ComputerProviderI</code>
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
 * @author	<a href="mailto:henri.ng@etu.upmc.fr">Henri NG</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public interface ComputerProviderI extends OfferedI {
	
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
	 * Retourne le nombre de coeurs disponibles
	 * 
	 * @return nbCoresFree
	 */
	public int getNbCoreDispo();
	
	/**
	 * Reinitialise une machine virtuelle via son URI
	 * 
	 * @param vm
	 * @return boolean
	 */
	public boolean reInit(String vm) throws Exception;

}
