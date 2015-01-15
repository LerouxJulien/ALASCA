package fr.upmc.alasca.computer.interfaces;

import fr.upmc.alasca.computer.objects.VMCarac;
import fr.upmc.alasca.computer.objects.VMMessages;
import fr.upmc.components.interfaces.RequiredI;

/**
 * L'interface <code>VMConsumerI</code>, requiert des fonctions pour le composant <code>VirtualMachine</code>
 */
public interface VMConsumerI extends RequiredI {

	/**
	 * Notifie le <code>Repartieur</code> de l'etat d'une VM et/ou de la fin de
	 * traitement d'une requete
	 * 
	 * @param m Notification de la VM au repartiteur de requetes
	 * @throws Exception
	 */
	public void notifyStatus(VMMessages m) throws Exception;

	/**
	 * Notifie le <code>Repartiteur</code> des caracteristiques de la VM
	 * 
	 * @param id l'ID de la VM
	 * @param c le message de caracteristiques de la VM
	 * @throws Exception
	 */
	public void notifyCarac(String id, VMCarac c) throws Exception;
}
