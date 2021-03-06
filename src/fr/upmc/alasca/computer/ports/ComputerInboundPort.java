package fr.upmc.alasca.computer.ports;

import java.rmi.RemoteException;

import fr.upmc.alasca.computer.components.Computer;
import fr.upmc.alasca.computer.exceptions.BadReinitialisationException;
import fr.upmc.alasca.computer.interfaces.ComputerProviderI;
import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractInboundPort;

/**
 * Port par lequel un <code>Computer</code> recoit une demande de deploiement de <code>VirtualMachine</code>
 */
public class ComputerInboundPort extends AbstractInboundPort implements
		ComputerProviderI {

	private static final long serialVersionUID = 1L;

	/**
	 * Cr�er l'InboundPort du <code>Computer</code>
	 * @param uri l'URI unique du port
	 * @param owner le composant propri�taire du port
	 * @throws Exception
	 */
	public ComputerInboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, ComputerProviderI.class, owner);
	}

	/**
	 * Deploie une machine virtuelle et la connecte au repartiteur de requete de
	 * l'application
	 * 
	 * @param nbCores le nombre de coeurs attribues � la machine virtuelle
	 * @param app l'ID de l'application
	 * @param URIRepartiteurFixe URI du port dans Repartiteur
	 * @param URIRepartiteurDCC URI du dcc dans Repartiteur
	 * @throws Exception 
	 */
	@Override
	public void deployVM(int nbCores, int app, String[] RepartiteurURI,
			String RepariteurURIDCC) throws Exception {
		final Computer comp = (Computer) this.owner;
		comp.deployVM(nbCores, app, RepartiteurURI, RepariteurURIDCC);
	}

	/**
	 * Detruit une machine virtuelle via son URI
	 * 
	 * @param vm l'URI de la VM
	 * @throws Exception 
	 */
	@Override
	public void destroyVM(String mv) throws Exception {
		final Computer comp = (Computer) this.owner;
		comp.destroyVM(mv);
	}

	/**
	 * Retourne le nombre de coeurs de Computer qui ne sont pas utilises par
	 * une machine virtuelle
	 * 
	 * @return le nombre de coeurs libre du Computer
	 * @throws Exception
	 */
	@Override
	public Integer availableCores() throws Exception {
		final Computer comp = (Computer) this.owner;
		return comp.availableCores();
	}
	
	@Override
	public boolean isMaxed(int appid) throws Exception {
		final Computer comp = (Computer) this.owner;
		return comp.isMaxed(appid);
	}

	@Override
	public void incFrequency(int appid) throws Exception {
		final Computer comp = (Computer) this.owner;
		comp.incFrequency(appid);
	}

	/**
	 * Initialise une VM via son URI en lui associant le r�partiteur de
	 * l'application donn�e
	 * 
	 * @param appID
	 * @param vm
	 * @throws Exception
	 */
	public void initVM(int appID, String vm) throws RemoteException, Exception {
		final Computer comp = (Computer) this.owner;
		comp.initVM(appID, vm);
	}

	/**
	 * R�initialise une VM via son URI en lui d�sassociant le r�partiteur de
	 * l'application donn�e
	 * 
	 * @param vm
	 * @throws BadReinitialisationException
	 * @throws RemoteException
	 * @throws Exception
	 */
	public void reInitVM(String vm) throws BadReinitialisationException,
		RemoteException, Exception {
		final Computer comp = (Computer) this.owner;
		comp.reInitVM(vm);
	}
	
}
