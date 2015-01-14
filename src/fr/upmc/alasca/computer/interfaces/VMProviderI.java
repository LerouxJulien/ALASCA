package fr.upmc.alasca.computer.interfaces;

import fr.upmc.alasca.computer.objects.VMCarac;
import fr.upmc.alasca.computer.objects.VMMessages;
import fr.upmc.alasca.requestgen.objects.Request;
import fr.upmc.components.exceptions.ComponentShutdownException;
import fr.upmc.components.interfaces.OfferedI;

public interface VMProviderI extends OfferedI {

	/**
	 * Transmet la requete a la VM
	 * 
	 * @param r Requete transmise a la VM
	 * @throws Exception
	 */
	public void processRequest(Request r) throws Exception;

	public String getVMURI() throws Exception;

	public void startNotification() throws Exception;
	
	
	
	public String getUriComputerParent() throws Exception;
	
	
	public int getNbCores() throws Exception;
	
	
	public void shutdown() throws  Exception;
	
}
