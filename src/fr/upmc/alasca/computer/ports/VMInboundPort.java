package fr.upmc.alasca.computer.ports;

import fr.upmc.alasca.computer.components.VirtualMachine;
import fr.upmc.alasca.computer.interfaces.VMProviderI;
import fr.upmc.alasca.requestgen.objects.Request;
import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractInboundPort;

/**
 * Classe de définition de l'InboundPort du composant <code>VirtualMachine</code>
 */
public class VMInboundPort extends AbstractInboundPort implements VMProviderI {

	private static final long serialVersionUID = 5983071151387825406L;

	/**
	 * Créer l'InboundPort de <code>VirtualMachine</code>
	 * 
	 * @param uri l'URI unique du port
	 * @param owner le composant propriétaire du port
	 * @throws Exception
	 */
	public VMInboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, VMProviderI.class, owner);
	}

	/**
	 * Retourne le nombre de coeurs de la VM
	 * 
	 * @return le nombre de coeurs de la VM
	 */
	@Override
	public int getNbCores() throws Exception {
		final VirtualMachine vm = (VirtualMachine) this.owner;
		return vm.getNbCores();
	}
	
	/**
	 * Retourne l'URI du Computer parent de la VM
	 * 
	 * @return l'URI du Computer parent de la VM
	 */
	@Override
	public String getUriComputerParent() throws Exception {
		final VirtualMachine vm = (VirtualMachine) this.owner;
		return vm.getUriComputerParent();
	}
	
	/**
	 * Retourne l'URI du InboundPort de la VM
	 * 
	 * @return l'URI du InboundPort de la VM
	 */
	@Override
	public String getVMInboundPortURI() throws Exception {
		final VirtualMachine vm = (VirtualMachine) this.owner;
		return vm.getVMiport().getPortURI();
	}

	/**
	 * Retourne l'URI de la VM
	 * 
	 * @return l'URI de la VM
	 * @throws Exception
	 */
	@Override
	public String getVMURI() throws Exception {
		final VirtualMachine vm = (VirtualMachine) this.owner;
		return vm.getVMoport().getPortURI();
	}

	/**
	 * Recupere la requete envoyee par le repartiteur de requete et l'envoie
	 * dans la file d'attente. Le traitement de la requete debute lorsque
	 * le serveur a un fil d'execution de libre.
	 *
	 * @param r la requête a traiter
	 * @throws Exception
	 */
	@Override
	public void processRequest(Request r) throws Exception {
		final VirtualMachine vm = (VirtualMachine) this.owner;
		vm.requestArrivalEvent(r);
	}

	/**
	 * Créer et envoi les notifications (<code>VMMessages</code> et <code>VMCarac</code>)
	 *  sur l'OutboundPort de la VM
	 *  
	 * @throws Exception
	 */
	@Override
	public void startNotification() throws Exception {
		final VirtualMachine vm = (VirtualMachine) this.owner;
		vm.startNotification();
	}
	
	/**
	 * Appelle la fonction shutdown de <code>AbstractComponent</code>, arrête la VM
	 * 
	 * @throws Exception
	 */
	@Override
	public void shutdown() throws Exception {
		final VirtualMachine vm = (VirtualMachine) this.owner;
		vm.shutdown();
	}

}
