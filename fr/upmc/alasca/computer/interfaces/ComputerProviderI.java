package fr.upmc.alasca.computer.interfaces;

import java.util.List;

import fr.upmc.alasca.requestgen.objects.Request;
import fr.upmc.components.interfaces.OfferedI;

/**
 * L'interface <code>ComputerProviderI</code>
 * 
 * <p>Created on : 10 oct. 2014</p>
 * 
 * @author	<a href="mailto:henri.ng@etu.upmc.fr">Henri NG</a>
 */
public interface ComputerProviderI extends OfferedI {
	
	/**
	 * Alloue une machine virtuelle
	 * 
	 * @param nbCores
	 * @param app
	 * @param RepartiteurURI URI du port dans Repartiteur
	 * @param RepartiteurURIDCC URI du dcc dans Repartiteur
	 * @return boolean
	 */
	public boolean deployVM(int nbCores, int app, String RepartiteurURI, String RepartiteurURIDCC) throws Exception;
	
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
	 * 
	 * @return le nombre de coeurs de Computer qui ne sont pas utilises par une machine virtuelle
	 * @throws Exception
	 */
	public Integer availableCores() throws Exception;
	
	/**
	 * Reinitialise une machine virtuelle via son URI
	 * 
	 * @param vm
	 * @return boolean
	 */
	public boolean reInit(String vm) throws Exception;

}
