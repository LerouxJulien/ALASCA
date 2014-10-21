package fr.upmc.alasca.computer.interfaces;

import fr.upmc.components.interfaces.OfferedI;
/**
 * L'interface <code>VirtualMachineProviderI</code>
 */
public interface VirtualMachineProviderI extends OfferedI {

	// Deploie la VM et creer une URI pour la VM
	public String provideURI() throws Exception;
	
}