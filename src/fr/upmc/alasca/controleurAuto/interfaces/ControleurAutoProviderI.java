package fr.upmc.alasca.controleurAuto.interfaces;

import fr.upmc.alasca.computer.objects.VMCarac;
import fr.upmc.alasca.computer.objects.VMMessages;
import fr.upmc.components.interfaces.OfferedI;

public interface ControleurAutoProviderI extends OfferedI{

	/**
	 * Notifie le repartiteur de requetes de l'etat d'une VM et/ou de la fin de
	 * traitement d'une requete
	 * 
	 * @param m Notification de la VM au repartiteur de requetes
	 * @throws Exception
	 */
	public void notifyStatus(VMMessages m) throws Exception;

	public void notifyCarac(String id, VMCarac c)throws Exception;

}
