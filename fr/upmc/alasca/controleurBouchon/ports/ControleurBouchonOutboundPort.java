package fr.upmc.alasca.controleurBouchon.ports;

import fr.upmc.alasca.computer.interfaces.ManagementVMI;
import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;

public class  ControleurBouchonOutboundPort extends		AbstractOutboundPort
implements	ManagementVMI{

	public ControleurBouchonOutboundPort(String uri,
		ComponentI owner) throws Exception {
		super(uri, ManagementVMI.class, owner) ;

		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean deployVM(int nbCores, int app) throws Exception {
		return ((ManagementVMI)this.connector).deployVM(nbCores, app) ;
	}

	@Override
	public boolean destroyVM(String mv) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

}
