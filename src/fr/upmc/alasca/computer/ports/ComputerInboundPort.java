package fr.upmc.alasca.computer.ports;

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
	 * Créer l'InboundPort du <code>Computer</code>
	 * @param uri l'URI unique du port
	 * @param owner le composant propriétaire du port
	 * @throws Exception
	 */
	public ComputerInboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, ComputerProviderI.class, owner);
	}

	/**
	 * Deploie une machine virtuelle et la connecte au repartiteur de requete de
	 * l'application
	 * 
	 * @param nbCores le nombre de coeurs attribues ï¿½ la machine virtuelle
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
	 * Reinitialise une machine virtuelle via son URI
	 * 
	 * @param vm l'URI de la VM
	 * @throws BadReinitialisationException 
	 */
	@Override
	public void reInit(String vm) throws BadReinitialisationException {
		final Computer comp = (Computer) this.owner;
		comp.reInit(vm);
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

}
