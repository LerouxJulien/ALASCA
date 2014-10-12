package fr.upmc.alasca.computer.interfaces;

import java.util.List;

import fr.upmc.alasca.requestgen.objects.Request;
import fr.upmc.alasca.computer.objects.VirtualMachine;

public interface ComputerProviderI {
	
	/**
	 * Alloue une machine virtuelle
	 * 
	 * @param nbCores
	 * @param app
	 * @return boolean
	 */
	public boolean deployVM(int nbCores, int app);
	
	/**
	 * Detruit une machine virtuelle
	 *  
	 * @param vm
	 * @return boolean
	 */
	public boolean destroyVM(VirtualMachine vm);
	
	/**
	 * Envoie la liste des machines virtuelles allouees
	 * 
	 * @return boolean
	 */
	public List<VirtualMachine> getListVM();
	
	/**
	 *  Recupere une requete envoyee par le repartiteur de requetes
	 *  
	 * @param mv
	 * @param req
	 * @return boolean
	 */
	public boolean getRequest(VirtualMachine mv, Request req);
	
	/**
	 * Reinitialise une machine virtuelle
	 * 
	 * @param vm
	 * @return boolean
	 */
	public boolean reInit(VirtualMachine vm);

}
