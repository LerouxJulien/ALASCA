package fr.upmc.alasca.controleur.ports;

//import fr.upmc.alasca.computer.exceptions.BadDestroyException;
//import fr.upmc.alasca.computer.exceptions.BadReinitialisationException;
import fr.upmc.alasca.computer.interfaces.ComputerProviderI;
import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;

/**
 * Port par lequel le Controleur demande a un Computer de deployer une
 * nouvelle machine virtuelle
 *
 */
public class ControleurOutboundPort extends AbstractOutboundPort implements
		ComputerProviderI {

	public ControleurOutboundPort(String uri, ComponentI owner)
			throws Exception {
		super(uri, ComputerProviderI.class, owner);
	}

	@Override
	public void deployVM(int nbCores, int app, String[] uri,
			String RepartiteurURIDCC) throws Exception {
		((ComputerProviderI) this.connector).deployVM(nbCores, app,
				uri, RepartiteurURIDCC);
	}

	@Override
	public void destroyVM(String mv) throws Exception {
		((ComputerProviderI) this.connector).destroyVM(mv);
	}

	@Override
	public void reInit(String vm) throws Exception {
		((ComputerProviderI) this.connector).reInit(vm);
	}

	@Override
	public Integer availableCores() throws Exception {
		return ((ComputerProviderI) this.connector).availableCores();
	}

}
