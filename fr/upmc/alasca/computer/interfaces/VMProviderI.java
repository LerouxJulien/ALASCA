package fr.upmc.alasca.computer.interfaces;

import fr.upmc.alasca.requestgen.objects.Request;
import fr.upmc.components.interfaces.OfferedI;
import fr.upmc.components.interfaces.RequiredI;
/**
 * L'interface <code>VirtualMachineProviderI</code>
 */
public interface VMProviderI extends OfferedI, RequiredI {

	// Deploie la VM et creer une URI pour la VM
	public void processRequest(Request r) throws Exception;
	public boolean queueIsFull() throws Exception;
}
