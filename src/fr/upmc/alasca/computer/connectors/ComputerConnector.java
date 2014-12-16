package fr.upmc.alasca.computer.connectors;

import fr.upmc.alasca.computer.exceptions.BadDestroyException;
import fr.upmc.alasca.computer.exceptions.BadReinitialisationException;
import fr.upmc.alasca.computer.interfaces.ComputerProviderI;
import fr.upmc.components.connectors.AbstractConnector;

/**
 * La classe <code>ComputerConnector</code> implemente un connecteur entre un
 * <code>Controleur</code> et un <code>Computer</code>
 * */
public class ComputerConnector extends AbstractConnector implements
		ComputerProviderI {

	@Override
	public void destroyVM(String mv) throws BadDestroyException {
		((ComputerProviderI) this.offering).destroyVM(mv);
	}

	@Override
	public void deployVM(int nbCores, int app, String RepartiteurURI,
			String RepartiteurURIDCC) throws Exception {
		((ComputerProviderI) this.offering).deployVM(nbCores, app,
				RepartiteurURI, RepartiteurURIDCC);
	}

	@Override
	public void reInit(String vm) throws BadReinitialisationException {
		((ComputerProviderI) this.offering).reInit(vm);
	}

	@Override
	public Integer availableCores() throws Exception {
		return ((ComputerProviderI) this.offering).availableCores();
	}

}
