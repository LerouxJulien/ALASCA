package fr.upmc.alasca.repartiteur.interfaces;

import fr.upmc.alasca.requestgen.objects.Request;
import fr.upmc.components.interfaces.RequiredI;

public interface RepartiteurConsumerI extends RequiredI {
	
	public String getUriComputerParent()throws Exception;
	
	public String getVMInboundPortURI()throws Exception;

	public String getVMURI() throws Exception;
	
	public void processRequest(Request r) throws Exception;
	
	public void startNotification() throws Exception;
	
}
