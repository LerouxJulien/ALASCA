package fr.upmc.alasca.controleur.ports;

import fr.upmc.alasca.computer.interfaces.ComputerProviderI;
import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;

public class ControleurOutboundPort extends AbstractOutboundPort implements
		ComputerProviderI {

	public ControleurOutboundPort(String uri, ComponentI owner)
			throws Exception {
		super(uri, ComputerProviderI.class, owner);

		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean deployVM(int nbCores, int app, String RepartiteurURI,
			String RepartiteurURIDCC) throws Exception {
		return ((ComputerProviderI) this.connector).deployVM(nbCores, app,
				RepartiteurURI, RepartiteurURIDCC);
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
		return ((ComputerProviderI) this.connector).availableCores();
	}

}
