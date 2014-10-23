package fr.upmc.alasca.computer.main;

import fr.upmc.alasca.computer.interfaces.ManagementVMI;
import fr.upmc.components.connectors.AbstractConnector;

public class ComputerConnector 
extends		AbstractConnector
implements	ManagementVMI{

	@Override
	public boolean deployVM(int nbCores, int app, String RepartiteurURI) throws Exception {
		return ((ManagementVMI)this.offering).deployVM(nbCores, app, RepartiteurURI);
	}
	
	@Override
	public boolean destroyVM(String mv) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

}
