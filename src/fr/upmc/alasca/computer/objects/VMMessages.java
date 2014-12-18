package fr.upmc.alasca.computer.objects;

import java.io.Serializable;

import fr.upmc.alasca.computer.enums.Status;
import fr.upmc.alasca.repartiteur.ports.RepartiteurInboundPort;
import fr.upmc.alasca.repartiteur.ports.RepartiteurOutboundPort;

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
	
	private RepartiteurInboundPort myrepport;

	/**
	 * Cree un message de notification de la VM a son repartiteur de requetes
	 * 
	 * @param mvID
	 * @param status
	 * @param time
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
	 * @param mvID
	 * @param status
	 * @param time
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
	 * @return the status
	 */
	public Status getStatus() {
		return status;
	}

	/**
	 * Retourne le temps de traitement d'une requete au moment de
	 * la notification de fin de traitement
	 * 
	 * @return the time
	 */
	public long getTime() {
		return time;
	}

	/**
	 * Retourne l'identifiant de la requete
	 * 
	 * @return the uri
	 */
	public String getUri() {
		return uri;
	}
	
	/**
	 * Retourne l'identifiant de la VM
	 * 
	 * @return the vmID
	 */
	public String getVmID() {
		return vmID;
	}
	
	public void setRepPort(RepartiteurInboundPort repartiteurInboundPort) {
		this.myrepport = repartiteurInboundPort;
	}
	
	
	public RepartiteurInboundPort getRepPort() {	
		return this.myrepport;	
	}
}
