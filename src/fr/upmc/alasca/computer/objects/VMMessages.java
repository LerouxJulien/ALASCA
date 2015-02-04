package fr.upmc.alasca.computer.objects;

import java.io.Serializable;

import fr.upmc.alasca.computer.enums.Status;
import fr.upmc.alasca.computer.ports.VMOutboundPort;

/**
 * La classe <code>VMMessages</code> definit le message de notification d'une
 * machine virtuelle au repartiteur de requetes. Elle renseigne le statut de la
 * machine virtuelle au moment de la notification et le temps de traitement
 * d'une requete si le message consiste a renvoyer une notification de fin de
 * traitement d'une requete.
 * 
 * <p>
 * Created on : 30 nov. 2014
 * </p>
 * 
 * @author <a href="mailto:henri.ng@etu.upmc.fr">Henri NG</a>
 * @version $Name$ -- $Revision$ -- $Date$
 */
public class VMMessages implements Serializable {
	
	private static final long serialVersionUID = -3472054271779774097L;

	// Identitifant de la VM
	private String vmID;
	
	// Etat de la VM lors de la notification
	private Status status;
	
	// Identifiant de la requete
	private String uri;
	
	// Temps de traitement d'une requete lors de la notification
	private long time;
	
	// InboundPort du Repartiteur lié à la VM
	private VMOutboundPort myrepport;

	/**
	 * Cree un message de notification de la VM a son repartiteur de requetes
	 * 
	 * @param mvID l'ID de la VM
	 * @param status le status de la VM
	 */
	public VMMessages(String vmID, Status status) {
		super();
		this.vmID = vmID;
		this.status = status;
		this.uri = "";
		this.time = 0;
	}
	
	/**
	 * Cree un message de notification de la VM a son repartiteur de requetes
	 * pour la fin d'un traitement d'une requete
	 * 
	 * @param vmID l'ID de la VM
	 * @param status le status de la VM
	 * @param uri l'identifiant de la requête
	 * @param time le temps de traitement de la requête
	 */
	public VMMessages(String vmID, Status status, String uri, long time) {
		super();
		this.vmID = vmID;
		this.status = status;
		this.uri = "";
		this.time = time;
	}

	/**
	 * Retourne le statut de la VM au moment de la notification
	 * 
	 * @return status
	 */
	public Status getStatus() {
		return status;
	}

	/**
	 * Retourne le temps de traitement d'une requete au moment de
	 * la notification de fin de traitement
	 * 
	 * @return time
	 */
	public long getTime() {
		return time;
	}

	/**
	 * Retourne l'identifiant de la requete
	 * 
	 * @return uri
	 */
	public String getUri() {
		return uri;
	}
	
	/**
	 * Retourne l'identifiant de la VM
	 * 
	 * @return vmID
	 */
	public String getVmID() {
		return vmID;
	}
	
	/**
	 * Modifie l'InboundPort du <code>Repartiteur</code> lié à la VM
	 * 
	 * @param repartiteurInboundPort le nouveau port du Repartiteur
	 */
	public void setRepPort(VMOutboundPort repartiteurInboundPort) {
		this.myrepport = repartiteurInboundPort;
	}
	
	/**
	 * Retourne l'InboundPort du <code>Repartiteur</code> lié à la VM
	 * 
	 * @return myrepport
	 */
	public VMOutboundPort getRepPort() {	
		return this.myrepport;	
	}
	
	/**
	 * Modifie le temps de traitement de la requête
	 * 
	 * @param time le nouveau temps de traitement
	 */
	public void setTime(long time) {
		this.time = time;
	}
}
