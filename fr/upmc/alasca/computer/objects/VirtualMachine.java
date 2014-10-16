package fr.upmc.alasca.computer.objects;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import fr.upmc.alasca.requestgen.objects.Request;

/**
 * La classe <code>VirtualMachine</code> definit la machine virtuelle qui
 * recupere les requetes transmises par la machine hôte et qui les traitent.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 * 
 * <p>Created on : 10 oct. 2014</p>
 * 
 * @author	<a href="mailto:henri.ng@etu.upmc.fr">Henri NG</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public class VirtualMachine {

	// ID de la VM
	private final int mvID;
	
	// ID de l'application
	private final int appID;
	
	// Nombre de coeurs attribues a la VM par la machine (de 1 a 16)
	private final int nbCores;

	// Taille maximale de la file d'attente
	private final int queueMax;
	
	// Frequence des coeurs de la VM
	private final List<Float> frequencies;
	
	// File d'attente de requetes
	private LinkedList<Request> queue;
	
	// Fils d'execution des requetes
	private List<VMThread> threads;
	
	// Etat de la VM :
	// True  : Libre  (Au moins un fil d'execution en attente)
	// False : Occupe (Toutes les fils d'execution occupes)
	private boolean isIdle;
	
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
	public VirtualMachine(int mvID, int appID, int nbCores, int queueMax,
			List<Float> frequencies) {
		super();
		this.mvID        = mvID;
		this.appID       = appID;
		this.nbCores     = nbCores;
		this.queueMax    = queueMax;
		this.frequencies = new ArrayList<Float>(frequencies);
		queue            = new LinkedList<Request>();
		threads          = new ArrayList<VMThread>();
		isIdle           = true;
		// Initialise les VMThread de la VM
		for (int i = 0; i < nbCores; i++) {
			int VMThreadID = mvID * 10 + i;
			threads.add(new VMThread(VMThreadID, frequencies.get(i)));
		}
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
	 * Retourne la taille maximale de la file d'attente
	 * 
	 * @return queueMax
	 */
	public int getQueueMax() {
		return queueMax;
	}

	/**
	 * Retourne la frequence totale de la VM
	 * 
	 * @return frequencies
	 */
	public List<Float> getFrequencies() {
		return frequencies;
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
	 * Retourne le nombre de requetes dans la file d'attente
	 * 
	 * @return size
	 */
	public int getQueueSize() {
		return queue.size();
	}

	/**
	 * Retourne l'etat de la VM
	 * True  : Libre  (Pas de requetes en cours)
	 * False : Occupe (Traitement de requetes en cours)
	 * 
	 * @return isIdle
	 */
	public boolean isIdle() {
		boolean isIdle = true;
		for (int i = 0; isIdle && i < nbCores; i++)
			isIdle &= threads.get(i).isWaiting();
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
			System.out.println("VM " + mvID + " is idle !");
		else if (getQueueSize() == 0)
			System.out.println("No request in queue in VM " + mvID + " !");
		else {
			// Parcours la liste de fils d'execution et recupere le thread libre
			// pour traiter la requete.
			for (int i = 0; i < nbCores; i++) {
				if (threads.get(i).isWaiting) {
					time = threads.get(i).process();
					// Traite qu'une seule requete lorsqu'un fil est libre
					break;
				}
			}
		}
		return time;
	}
	
	/**
	 * Teste la taille de la file d'attente
	 * 
	 * @return boolean
	 */
	public boolean queueIsFull() {
		return getQueueSize() >= queueMax;
	}
	
	/**
	 * Retire la premiere requete de la file d'attente
	 * 
	 * @return request
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
				+ ", nbCores=" + nbCores + ", frequence=" + frequencies
				+ ", isIdle=" + isIdle + ", queue=" + queue + "]";
	}
	
	/**
	 * La classe <code>VMThread</code> definit un thread dans la machine
	 * virtuelle <code>VirtualMachine</code>. Ce thread recupere une requetes
	 * de la file d'attente et la met dans son fil d'execution.
	 * 
	 * <p><strong>Description</strong></p>
	 * 
	 * <p><strong>Invariant</strong></p>
	 * 
	 * <pre>
	 * invariant	true
	 * </pre>
	 * 
	 * <p>Created on : 16 oct. 2014</p>
	 * 
	 * @author	<a href="mailto:henri.ng@etu.upmc.fr">Henri NG</a>
	 * @version	$Name$ -- $Revision$ -- $Date$
	 */
	public class VMThread {

		// ID du thread de la VM
		private final int VMThreadID;
			
		// Frequence du coeur associe
		private final float frequence;
		
		// Etat du thread
		private boolean isWaiting;

		/**
		 * Initialisation des threads internes a la VM (correspond a la fil
		 * d'execution dans le sujet)
		 * 
		 * Les VMThread sont inialises en meme temps que le deploiement de
		 * la VM. Un VMThread correspond a un coeur de la VM avec sa frequence
		 * associee. Il est par defaut en attente de requete a traiter. 
		 * 
		 * @param threadID
		 * @param frequence
		 * @param isWaiting
		 */
		public VMThread(int VMThreadID, float frequence) {
			super();
			this.VMThreadID = VMThreadID;
			this.frequence  = frequence;
			this.isWaiting  = true;
		}
		
		/**
		 * Retourne la frequence du coeur associee de la VMThread
		 * 
		 * @return frequence
		 */
		public float getFrequence() {
			return frequence;
		}

		/**
		 * Retourne l'ID de la VMThread
		 * 
		 * @return VMThreadID
		 */
		public int getVMThreadID() {
			return VMThreadID;
		}

		/**
		 * Retourne l'etat de la VMThread.
		 * La fil d'execution est soit en attente de requetes, soit occupee.
		 * 
		 * @return VMThreadID
		 */
		public boolean isWaiting() {
			return isWaiting;
		}
		
		/**
		 * Retire une requete de la file d'attente de VM et la traite
		 * 
		 * @return time
		 */
		public float process() {
			float time = 0;
			// Ne passe jamais dans ce cas (sinon erreur d'implementation)
			if (!isWaiting)
				System.out.println("VMThread " + VMThreadID + " is still"
						+ " dealing with a request !");
			else {
				Request req = removeRequest();
				long instructions = req.getInstructions();
				isWaiting = false;
				// Execute et attends la fin de traitement du thread
				time = instructions / frequence;
				isWaiting = true;
			}
			return time;
		}
		
	}
	
}
