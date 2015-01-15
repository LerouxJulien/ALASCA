package fr.upmc.alasca.controleur.ports;

import java.rmi.RemoteException;

import fr.upmc.alasca.computer.exceptions.BadReinitialisationException;
import fr.upmc.alasca.computer.interfaces.ComputerProviderI;
import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;

/**
 * Classe <code>ControleurOutboundPort</code>
 *
 * <p>
 * <strong>Description</strong>
 * </p>
 * 
 * <p>La classe <code>ControleurOutboundPort</code> implémente le port par
 * lequel le contrôleur demande à une machine le déploiement ou la destruction
 * de VM.
 * 
 * <p>Created on : 23 dec. 2014</p>
 * 
 * @author <a href="mailto:Henri.Ng@etu.upmc.fr">Henri Ng/a>
 * @version $Name$ -- $Revision$ -- $Date$
 */
public class ControleurOutboundPort extends AbstractOutboundPort implements
		ComputerProviderI {

	public ControleurOutboundPort(String uri, ComponentI owner)
			throws Exception {
		super(uri, ComputerProviderI.class, owner);
	}

	@Override
	public void deployVM(int nbCores, int app, String[] URIRepartiteurFixe,
			String URIRepartiteurDCC) throws Exception {
		((ComputerProviderI) this.connector).deployVM(nbCores, app,
				URIRepartiteurFixe, URIRepartiteurDCC);
	}

	@Override
	public void destroyVM(String mv) throws Exception {
		((ComputerProviderI) this.connector).destroyVM(mv);
	}

	@Override
	public void reInit(String vm) throws BadReinitialisationException, 
	RemoteException {
		((ComputerProviderI) this.connector).reInit(vm);
	}

	@Override
	public Integer availableCores() throws Exception {
		return ((ComputerProviderI) this.connector).availableCores();
	}

	
	@Override
	public boolean isMaxed(int appid) throws Exception {
		return ((ComputerProviderI) this.connector).isMaxed(appid);
	}

	@Override
	public void incFrequency(int appid) throws Exception {
		((ComputerProviderI) this.connector).incFrequency(appid);
	}
}
