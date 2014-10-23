package fr.upmc.alasca.computer.ports;

import fr.upmc.alasca.computer.components.Computer;
import fr.upmc.alasca.computer.interfaces.ManagementVMI;
import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractInboundPort;

public class ComputerInboundPort extends AbstractInboundPort implements ManagementVMI {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ComputerInboundPort(String uri, ComponentI owner)
			throws Exception {
		super(uri, ManagementVMI.class, owner) ;
	}

	@Override
	public boolean deployVM(int nbCores, int app, String RepartiteurURI) throws Exception {
		final Computer comp = (Computer) this.owner ;
		return comp.deployVM(nbCores, app, RepartiteurURI);
		/*sp.handleRequestAsync(
				new ComponentService<Void>() {
					@Override
					public Void call() throws Exception {
						sp.requestArrivalEvent(fc);
						return null;
					}
				}) ;*/
	}

	@Override
	public boolean destroyVM(String mv) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}
	
}
