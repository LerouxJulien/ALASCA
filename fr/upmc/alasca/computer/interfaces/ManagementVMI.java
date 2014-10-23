package fr.upmc.alasca.computer.interfaces;

import fr.upmc.components.interfaces.OfferedI;
import fr.upmc.components.interfaces.RequiredI;

public interface ManagementVMI extends		OfferedI, RequiredI
{
	/**
	 * Alloue une machine virtuelle
	 * 
	 * @param nbCores
	 * @param app
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
}