package fr.upmc.alasca.computer.interfaces;

import java.util.List;

import fr.upmc.alasca.requestgen.objects.Request;
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
	 * @return boolean
	 */
	public boolean deployVM(int nbCores, int app);
	
	/**
	 * Detruit une machine virtuelle via son URI
	 *  
	 * @param mv
	 * @return boolean
	 */
	public boolean destroyVM(String mv);
	
	/**
	 * Envoie la liste des URI des machines virtuelles allouees
	 * 
	 * @return boolean
	 */
	public List<String> getListVM();
	
	/**
	 *  Recupere une requete envoyee par le repartiteur de requetes
	 *  
	 * @param mv
	 * @param req
	 * @return boolean
	 */
	public boolean getRequest(String mv, Request req);
	
	/**
	 * Reinitialise une machine virtuelle via son URI
	 * 
	 * @param vm
	 * @return boolean
	 */
	public boolean reInit(String vm);

}
