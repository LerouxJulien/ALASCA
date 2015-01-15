package fr.upmc.alasca.computer.connectors;

import java.rmi.RemoteException;

import fr.upmc.alasca.computer.exceptions.BadReinitialisationException;
import fr.upmc.alasca.computer.interfaces.ComputerProviderI;
import fr.upmc.components.connectors.AbstractConnector;

/**
 * La classe <code>ComputerConnector</code> implemente un connecteur entre un
 * <code>Controleur</code> et un <code>Computer</code>
 * */
public class ComputerConnector extends AbstractConnector implements
		ComputerProviderI {

	/**
	 * Appelle la fonction destroyVM de <code>Computer</code>
	 * 
	 * @param mv l'URI de la VM a detruire
	 * @throws Exception
	 */
	@Override
	public void destroyVM(String mv) throws Exception  {
		((ComputerProviderI) this.offering).destroyVM(mv);
	}

	/**
	 * Appelle la fonction deployVM de <code>Computer</code>
	 * 
	 * @param nbCores le nombre de coeur a allouer pour la VM
	 * @param app l'ID de l'application liée à la VM
	 * @param RepartiteurURI l'URI du port dans Repartiteur
	 * @param RepartiteurURIDCC l'URI du dcc dans Repartiteur
	 * @throws Exception
	 */
	@Override
	public void deployVM(int nbCores, int app, String[] RepartiteurURI,
			String RepartiteurURIDCC) throws Exception {
		((ComputerProviderI) this.offering).deployVM(nbCores, app,
				RepartiteurURI, RepartiteurURIDCC);
	}

	/**
	 * Appelle la fonction reInit de <code>Computer</code>
	 * 
	 * @param vm l'URI de la VM a reinitialiser
	 * @throws BadReinitialisationException
	 * @throws RemoteException
	 */
	@Override
	public void reInit(String vm) throws BadReinitialisationException, RemoteException  {
		((ComputerProviderI) this.offering).reInit(vm);
	}

	/**
	 * Appelle la fonction availableCores de <code>Computer</code>
	 * 
	 * @return le nombre de coeurs physiques disponibles du Computer
	 * @throws Exception
	 */
	@Override
	public Integer availableCores() throws Exception {
		return ((ComputerProviderI) this.offering).availableCores();
	}

}
