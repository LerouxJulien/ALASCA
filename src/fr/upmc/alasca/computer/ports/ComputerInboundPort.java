package fr.upmc.alasca.computer.ports;

import fr.upmc.alasca.computer.components.Computer;
import fr.upmc.alasca.computer.interfaces.ComputerProviderI;
import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractInboundPort;

public class ComputerInboundPort extends AbstractInboundPort implements
		ComputerProviderI {

	private static final long serialVersionUID = 1L;

	public ComputerInboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, ComputerProviderI.class, owner);
	}

	@Override
	public boolean deployVM(int nbCores, int app, String RepartiteurURI,
			String RepariteurURIDCC) throws Exception {
		final Computer comp = (Computer) this.owner;
		return comp.deployVM(nbCores, app, RepartiteurURI, RepariteurURIDCC);
	}

	@Override
	public boolean destroyVM(String mv) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean reInit(String vm) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Integer availableCores() throws Exception {
		final Computer comp = (Computer) this.owner;
		return comp.availableCores();
	}

}
