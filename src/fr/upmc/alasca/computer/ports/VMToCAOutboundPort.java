package fr.upmc.alasca.computer.ports;

import fr.upmc.alasca.computer.interfaces.VMConsumerI;
import fr.upmc.alasca.computer.objects.VMCarac;
import fr.upmc.alasca.computer.objects.VMMessages;
import fr.upmc.alasca.controleurAuto.interfaces.CANotificationProviderI;
import fr.upmc.alasca.controleurAuto.interfaces.CAProviderI;
import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;

public class VMToCAOutboundPort extends AbstractOutboundPort
implements CANotificationProviderI {

	/**
	 * Créer l'OutboundPort de <code>VirtualMachine</code>
	 * 
	 * @param uri l'URI unique du port
	 * @param owner le composant propriétaire du port
	 * @throws Exception
	 */
	public VMToCAOutboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, VMConsumerI.class, owner);
	}
	
	/**
	 * Appelle la fonction notifyStatus du <code>Repartiteur</code>
	 * 
	 * @param m le message a transmettre
	 * @throws Exception
	 */
	@Override
	public void notifyStatus(VMMessages m) throws Exception {
		((CANotificationProviderI) this.connector).notifyStatus(m);
	}

	/**
	 * Appelle la fonction notifyCarac du <code>Repartiteur</code>
	 * 
	 * @param id l'ID de la VM
	 * @param c le message de caracteristiques de cette VM
	 * @throws Exception
	 */
	public void notifyCarac(String id, VMCarac c) throws Exception {
		( (CANotificationProviderI) this.connector).notifyCarac(id,c);
	}
}