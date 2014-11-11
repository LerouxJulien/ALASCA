package fr.upmc.alasca.computer.connectors;

import fr.upmc.alasca.computer.interfaces.ComputerProviderI;
import fr.upmc.components.connectors.AbstractConnector;

/**
 * La classe <code>ComputerConnector</code> implemente un connecteur entre un
 * <code>Controleur</code> et un <code>Computer</code>
 * */
public class ComputerConnector extends AbstractConnector implements
		ComputerProviderI {

	@Override
	public boolean destroyVM(String mv) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean deployVM(int nbCores, int app, String RepartiteurURI,
			String RepartiteurURIDCC) throws Exception {
		return ((ComputerProviderI) this.offering).deployVM(nbCores, app,
				RepartiteurURI, RepartiteurURIDCC);
	}

	@Override
	public boolean reInit(String vm) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Integer availableCores() throws Exception {
		return ((ComputerProviderI) this.offering).availableCores();
	}

}
