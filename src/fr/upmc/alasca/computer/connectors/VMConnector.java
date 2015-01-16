package fr.upmc.alasca.computer.connectors;

import java.io.Serializable;

import fr.upmc.alasca.computer.interfaces.VMConsumerI;
import fr.upmc.alasca.computer.interfaces.VMProviderI;
import fr.upmc.alasca.computer.objects.VMCarac;
import fr.upmc.alasca.computer.objects.VMMessages;
import fr.upmc.alasca.repartiteur.interfaces.RepartiteurProviderI;
import fr.upmc.alasca.requestgen.objects.Request;
import fr.upmc.components.connectors.AbstractConnector;

/**
 * La classe <code>VMConnector</code> implemente un connecteur entre un <code>Repartiteur</code> et une
 * <code>VirtualMachine</code>
 * */
public class VMConnector extends AbstractConnector implements VMProviderI,
VMConsumerI, Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Appelle la fonction getNbCores de <code>VirtualMachine</code>
	 * 
	 * @return le nombre de coeurs de la VM
	 * @throws Exception
	 */
	@Override
	public int getNbCores() throws Exception{
		return ((VMProviderI) this.offering).getNbCores();
	}
	
	/**
	 * Appelle la fonction getUriComputerParent de <code>VirtualMachine</code>
	 * 
	 * @return l'URI du Computer parent de la VM
	 * @throws Exception
	 */
	@Override
	public String getUriComputerParent() throws Exception{
		return ((VMProviderI) this.offering).getUriComputerParent();
	}
	
	/**
	 * Appelle la fonction getVMInboundPortURI de <code>VirtualMachine</code>
	 * 
	 * @return l'URI du Computer parent de la VM
	 * @throws Exception
	 */
	@Override
	public String getVMInboundPortURI() throws Exception{
		return ((VMProviderI) this.offering).getVMInboundPortURI();
	}
	
	/**
	 * Appelle la fonction getVMURI de <code>VirtualMachine</code>
	 * 
	 * @return l'URI de la VM
	 * @throws Exception
	 */
	@Override
	public String getVMURI() throws Exception {
		return ((VMProviderI) this.offering).getVMURI();
	}

	/**
	 * Appelle la fonction notifyCarac de <code>Repartiteur</code>
	 * 
	 * @param id l'ID de la VM
	 * @param c le message de caracteristiques de la VM
	 * @throws Exception
	 */
	@Override
	public void notifyCarac(String id, VMCarac c) throws Exception{
		((RepartiteurProviderI) this.requiring).notifyCarac(id, c);
	}
	
	/**
	 * Appelle la fonction notifyStatus de <code>Repartiteur</code>
	 * 
	 * @param m le message de notification de la VM
	 * @throws Exception
	 */
	@Override
	public void notifyStatus(VMMessages m) throws Exception { 
		((RepartiteurProviderI) this.requiring).notifyStatus(m);
	}
	
	/**
	 * Appelle la fonction processRequest de <code>VirtualMachine</code>
	 * 
	 * @param r la requête à traiter
	 * @throws Exception
	 */
	@Override
	public void processRequest(Request r) throws Exception {
		((VMProviderI) this.offering).processRequest(r);
	}
	
	/**
	 * Appelle la fonction startNotification de <code>VirtualMachine</code>
	 * 
	 * @throws Exception
	 */
	@Override
	public void startNotification() throws Exception {
		((VMProviderI) this.offering).startNotification();
	}
	
	/**
	 * Appelle la fonction shutdown de <code>VirtualMachine</code>
	 * 
	 * @throws Exception
	 */
	@Override
	public void shutdown() throws Exception {
		((VMProviderI) this.offering).shutdown();
	}
	
}
