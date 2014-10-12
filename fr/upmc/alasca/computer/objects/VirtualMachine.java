package fr.upmc.alasca.computer.objects;

import java.util.LinkedList;
import java.util.List;

import fr.upmc.alasca.requestgen.objects.Request;

public class VirtualMachine {

	// ID de la VM
	private final int mvID;
	
	// ID de l'application
	private final int appID;
	
	// Nombre de coeurs attribues à la VM
	private final int nbCores;
	
	// Frequence totale pour la VM
	private final float frequence;
	
	// Etat de la VM :
	// True  : Libre  (Pas de requetes en cours)
	// False : Occupe (Traitement de requetes en cours)
	private boolean isIdle;
	
	// File d'attente de requêtes
	private LinkedList<Request> queue; 
	
	/**
	 * Alloue une machine virtuelle.
	 * 
	 * On suppose que l'allocation de la machine se fait forcement avec
	 * la requete et son application associee. Le nombre de coeur est donnee par
	 * la machine hôte. 
	 * 
	 * @param mvID
	 * @param appID
	 * @param nbCores
	 * @param frequence
	 */
	public VirtualMachine(int mvID, int appID, int nbCores, float frequence) {
		super();
		this.mvID      = mvID;
		this.appID     = appID;
		this.nbCores   = nbCores;
		this.frequence = frequence;
		isIdle         = true;
		queue          = new LinkedList<Request>();
	}

	/**
	 * Ajoute une requete a la file
	 * 
	 * @param req
	 * 
	 * @return boolean
	 */
	public boolean addRequest(Request req) {
		return queue.add(req);
	}
	
	/**
	 * Retourne l'ID de la VM
	 * 
	 * @return mvID
	 */
	public int getMvID() {
		return mvID;
	}
	
	/**
	 * Retourne l'ID de l'application
	 * 
	 * @return appID
	 */
	public int getAppID() {
		return appID;
	}

	/**
	 * Retourne le nombre de coeurs de la VM
	 * 
	 * @return nbCores
	 */
	public int getNbCores() {
		return nbCores;
	}

	/**
	 * Retourne la frequence totale de la VM
	 * 
	 * @return mvID
	 */
	public float getFrequence() {
		return frequence;
	}

	/**
	 * Retourne la file d'attente de requetes
	 * 
	 * @return queue
	 */
	public List<Request> getQueue() {
		return queue;
	}
	
	/**
	 * Retourne la taille de la file d'attente
	 * 
	 * @return size
	 */
	public int getQueueSize() {
		return queue.size();
	}

	/**
	 * Retourne l'etat de la VM
	 * 
	 * @return isIdle
	 */
	public boolean isIdle() {
		return isIdle;
	}

	/**
	 * Traite la premiere requete de la file et retourne le temps d'execution
	 * 
	 * @return time
	 */
	public float process() {
		float time = 0;
		if (!isIdle)
			System.out.println("VM is still dealing with a request !");
		else if (getQueueSize() == 0)
			System.out.println("No request in queue !");
		else {
			Request req = removeRequest();
			long instructions = req.getInstructions();
			isIdle = false;
			time = instructions / frequence;
			isIdle = true;
		}
		return time;
	}
	
	/**
	 * Retire la premiere requete de la file d'attente
	 */
	public Request removeRequest() {
		return queue.removeFirst();
	}
	
	/**
	 * Modifie l'etat de la VM
	 * 
	 * @param isIdle
	 */
	public void setIdle(boolean isIdle) {
		this.isIdle = isIdle;
	}

	/**
	 * Retourne la description de la VM en l'etat
	 * 
	 * @return string
	 */
	@Override
	public String toString() {
		return "VirtualMachine [mvID=" + mvID + ", appID=" + appID
				+ ", nbCores=" + nbCores + ", frequence=" + frequence
				+ ", isIdle=" + isIdle + ", queue=" + queue + "]";
	}
	
}
