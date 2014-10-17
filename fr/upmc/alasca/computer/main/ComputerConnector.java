package fr.upmc.alasca.computer.main;

import fr.upmc.alasca.computer.interfaces.ManagementVMI;
import fr.upmc.components.connectors.AbstractConnector;

public class ComputerConnector 
extends		AbstractConnector
implements	ManagementVMI{

	@Override
	public boolean deployVM(int nbCores, int app) throws Exception {
		return ((ManagementVMI)this.offering).deployVM(nbCores, app);
	}
	
	@Override
	public boolean destroyVM(String mv) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

}
