package fr.upmc.alasca.computer.ports;

import fr.upmc.alasca.computer.components.Computer;
import fr.upmc.alasca.computer.exceptions.BadDestroyException;
import fr.upmc.alasca.computer.exceptions.BadReinitialisationException;
import fr.upmc.alasca.computer.interfaces.ComputerProviderI;
import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractInboundPort;

/**
 * Port par lequel un computer recoit une demande de deploiement de VirtualMachine
 */
public class ComputerInboundPort extends AbstractInboundPort implements
		ComputerProviderI {

	private static final long serialVersionUID = 1L;

	public ComputerInboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, ComputerProviderI.class, owner);
	}

	@Override
	public void deployVM(int nbCores, int app, String RepartiteurURI,
			String RepariteurURIDCC) throws Exception {
		final Computer comp = (Computer) this.owner;
		comp.deployVM(nbCores, app, RepartiteurURI, RepariteurURIDCC);
	}

	@Override
	public void destroyVM(String mv) throws BadDestroyException {
		final Computer comp = (Computer) this.owner;
		comp.destroyVM(mv);
	}

	@Override
	public void reInit(String vm) throws BadReinitialisationException {
		final Computer comp = (Computer) this.owner;
		comp.reInit(vm);
	}

	@Override
	public Integer availableCores() throws Exception {
		final Computer comp = (Computer) this.owner;
		return comp.availableCores();
	}

}
