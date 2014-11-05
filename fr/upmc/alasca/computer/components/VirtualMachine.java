package fr.upmc.alasca.computer.components;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import fr.upmc.alasca.computer.interfaces.VMProviderI;
import fr.upmc.alasca.computer.ports.VMInboudPort;
import fr.upmc.alasca.requestgen.objects.Request;
import fr.upmc.alasca.requestgen.utils.TimeProcessing;
import fr.upmc.components.AbstractComponent;
import fr.upmc.components.ports.PortI;

/**
 * La classe <code>VirtualMachine</code> definit la machine virtuelle qui
 * recupere les requetes transmises par la machine h�te et qui les traitent.
 *
 * <p>
 * <strong>Description</strong>
 * </p>
 * 
 * <p>
 * <strong>Invariant</strong>
 * </p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 * 
 * <p>
 * Created on : 10 oct. 2014
 * </p>
 * 
 * @author <a href="mailto:henri.ng@etu.upmc.fr">Henri NG</a>
 * @version $Name$ -- $Revision$ -- $Date$
 */
public class VirtualMachine extends AbstractComponent{

	// ID de la VM
	private final int mvID;

	// ID de l'application
	private final int appID;

	// Nombre de coeurs attribues a la VM par la machine (de 1 a 16)
	private final int nbCores;

	// Frequence des coeurs de la VM
	private final List<Double> frequencies;
	
	// Statut de la VM
	// NEW  : VM venant d'etre deployee (Aucune requete traitee)
	// FREE : Libre (Au moins un fil d'execution libre)
	// BUSY : Occupe (Tous les fils occupes)
	// STOP : En attente d'eutanasie (S'il ne fout toujours rien)
	private enum Status {NEW, FREE, BUSY, }
	private Status status;

	// Taille maximale de la file d'attente
	private final int queueMax;

	// Nombre de requete traites par la VM
	private int nbRequest;

	// File d'attente de requetes
	protected BlockingQueue<Request> queue;

	// Fils d'execution des requetes
	private List<VMThread> threads;

	// Compteur pour alterner entre les differents coeurs
	protected int compteurCyclique = 0;

	protected Request servicing;

	/**
	 * Alloue une machine virtuelle.
	 * 
	 * On suppose que l'allocation de la machine se fait forcement avec la
	 * requete et son application associee. Le nombre de coeur est donnee par la
	 * machine h�te.
	 * 
	 * @param mvID
	 * @param appID
	 * @param nbCores
	 * @param frequence
	 * @throws Exception 
	 */
	public VirtualMachine(String port, Integer mvID, Integer appID, Integer nbCores, Integer queueMax,
			ArrayList<Double> frequencies) throws Exception {
		super();
		this.mvID      = mvID;
		this.appID     = appID;
		this.nbCores   = nbCores;
		this.queueMax  = queueMax;
		this.frequencies = new ArrayList<Double>(frequencies);
		this.status    = Status.NEW;
		this.nbRequest = 0;
		queue   = new LinkedBlockingQueue<Request>();
		threads = new ArrayList<VMThread>();
		// Initialise les VMThread de la VM
		for (int i = 0; i < nbCores; i++) {
			//int VMThreadID = mvID * 10 + i;
			threads.add(new VMThread(i, frequencies.get(i), this));
		}
		
		this.addOfferedInterface(VMProviderI.class) ;
		PortI p = new VMInboudPort(port, this) ;
		this.addPort(p);
		p.localPublishPort() ;
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
	 * Retourne l'ID de l'application
	 * 
	 * @return appID
	 */
	public int getAppID() {
		return appID;
	}

	/**
	 * Retourne la frequence de chaque coeur attribue de la VM
	 * 
	 * @return frequencies
	 */
	public List<Double> getFrequencies() {
		return frequencies;
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
	 * Retourne le nombre de coeurs de la VM
	 * 
	 * @return nbCores
	 */
	public int getNbCores() {
		return nbCores;
	}

	/**
	 * Retourne le nombre de requetes traites par la VM
	 * 
	 * @return nbRequest
	 */
	public int getNbRequest() {
		return nbRequest;
	}

	/**
	 * Retourne la file d'attente de requetes
	 * 
	 * @return queue
	 */
	public BlockingQueue<Request> getQueue() {
		return queue;
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
	 * Retourne le nombre de requetes dans la file d'attente
	 * 
	 * @return size
	 */
	public int getQueueSize() {
		return queue.size();
	}
	
	/**
	 * Retourne le statut de la VM
	 * NEW  : VM venant d'etre deployee (Aucune requete traitee)
	 * FREE : Libre (Au moins un fil d'execution libre)
	 * BUSY : Occupe (Tous les fils occupes)
	 * 
	 * @return status
	 */
	public Status getStatus() {
		return status;
	}

	/**
	 * Teste si une des files d'execution est libre
	 * 
	 * @return isIdle
	 */
	public boolean isIdle() {
		boolean isIdle = false;
		for (int i = 0; i < nbCores; i++)
			isIdle |= threads.get(i).isWaiting();
		return isIdle;
	}
	
	/**
	 * Alias de isIdle
	 * 
	 * @return isIdle
	 */
	public boolean hasAvailableCore() {
		boolean isIdle = false;
		for (int i = 0; i < nbCores; i++)
			isIdle |= threads.get(i).isWaiting();
		return isIdle;
	}

	/**
	 * Traite la premiere requete de la file et retourne le temps d'execution
	 * 
	 * @return time
	 */
	public double process() {
		double time = 0;
		if (!isIdle()) {
			status = Status.BUSY;
			System.out.println("VM " + mvID + " is not idle !");
		}
		else if (getQueueSize() == 0) {
			status = Status.FREE;
			System.out.println("No request in queue in VM " + mvID + " !");
		}
		else {
			// Parcours la liste de fils d'execution et recupere le thread libre
			// pour traiter la requete.
			for (int i = 0; i < nbCores; i++) {
				status = Status.FREE;
				if (i == nbCores - 1)
					status = Status.BUSY;
				if (threads.get(i).isWaiting) {
					// Traite qu'une seule requete lorsqu'un fil est libre
					time = threads.get(i).process();
					status = Status.FREE;
					break;
				}
			}
			nbRequest++;
		}
		return time;
	}

	/**
	 * Retire la premiere requete de la file d'attente
	 * 
	 * @return request
	 */
	public Request removeRequest() {
		return queue.remove();
	}

	/**
	 * process a request arrival event, queueing the request and the processing
	 * a begin sericing event if the server is currently idle.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	r != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param r
	 * @throws Exception
	 */
	public void requestArrivalEvent(Request r) throws Exception {
		assert r != null;
		long t = System.currentTimeMillis();
		if(!this.queueIsFull()){
			this.queue.add(r);
			System.out.println("Accepting request       " + r + " at "
					+ TimeProcessing.toString(t));
			r.setArrivalTime(t);
		
		if (!this.hasAvailableCore()) {
			System.out.println("Queueing request " + r);
		} else {
			for (compteurCyclique = (compteurCyclique + 1) % getNbCores();
					compteurCyclique < nbCores;
					compteurCyclique = (compteurCyclique + 1)
					% getNbCores()) {
				if (threads.get(compteurCyclique).isWaiting()) {
					threads.get(compteurCyclique).process();
					break;
				}
			}
		}
		}
		else {
			System.out.println("Demande rejet�e par la VM " + this.mvID +" : queue pleine");
		}
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
	 * Retourne la description de la VM en l'etat
	 * 
	 * @return string
	 */
	@Override
	public String toString() {
		return "VirtualMachine [mvID=" + mvID + ", appID=" + appID
				+ ", nbCores=" + nbCores + ", frequence=" + frequencies
				+ ", status=" + status + ", queue=" + queue + "]";
	}

	/**
	 * La classe <code>VMThread</code> definit un thread dans la machine
	 * virtuelle <code>VirtualMachine</code>. Ce thread recupere une requetes de
	 * la file d'attente et la met dans son fil d'execution.
	 * 
	 * <p>
	 * <strong>Description</strong>
	 * </p>
	 * 
	 * <p>
	 * <strong>Invariant</strong>
	 * </p>
	 * 
	 * <pre>
	 * invariant	true
	 * </pre>
	 * 
	 * <p>
	 * Created on : 16 oct. 2014
	 * </p>
	 * 
	 * @author <a href="mailto:henri.ng@etu.upmc.fr">Henri NG</a>
	 * @version $Name$ -- $Revision$ -- $Date$
	 */
	public class VMThread extends AbstractComponent {

		private Request servicing;

		private VirtualMachine owner;

		// ID du thread de la VM
		private final int VMThreadID;

		// Frequence du coeur associe
		private final double frequence;

		// Etat du thread
		private boolean isWaiting;

		protected Future<?> nextEndServicingTaskFuture;

		/**
		 * Initialisation des threads internes a la VM (correspond a la fil
		 * d'execution dans le sujet)
		 * 
		 * Les VMThread sont inialises en meme temps que le deploiement de la
		 * VM. Un VMThread correspond a un coeur de la VM avec sa frequence
		 * associee. Il est par defaut en attente de requete a traiter.
		 * 
		 * @param threadID
		 * @param frequence
		 * @param isWaiting
		 */
		public VMThread(int VMThreadID, double frequence, VirtualMachine owner) {
			super(true, true);
			this.VMThreadID = VMThreadID;
			this.frequence = frequence;
			this.isWaiting = true;
			this.owner = owner;
			this.nextEndServicingTaskFuture = null ;
		}

		/**
		 * Retourne la frequence du coeur associee de la VMThread
		 * 
		 * @return frequence
		 */
		public double getFrequence() {
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
		 * Retourne l'etat de la VMThread. La fil d'execution est soit en
		 * attente de requetes, soit occupee.
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
		public long process() {
			this.servicing = owner.queue.remove();
			System.out.println("Begin servicing request " + this.servicing
					+ " at "
					+ TimeProcessing.toString(System.currentTimeMillis())
					+ " by " + this.VMThreadID
					+ " with " + this.servicing.getInstructions() + " instructions");
			this.isWaiting = false;
			final VMThread vmt = (VMThread) this;
			final long processingTime = (long) ((double)this.servicing.getInstructions()
					/ ((double) frequence * 1000000));
			this.nextEndServicingTaskFuture = this.scheduleTask(
					new ComponentTask() {
						@Override
						public void run() {
							try {
								vmt.endServicingEvent();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}, processingTime, TimeUnit.SECONDS);
			return processingTime;
		}

		public void endServicingEvent() throws Exception {
			long t = System.currentTimeMillis();
			long st = t - this.servicing.getArrivalTime();
			System.out.println("End servicing request   " + this.servicing
					+ " at " + TimeProcessing.toString(t)
					+ " with service time " + st
					+ " by " + this.VMThreadID
					+ " --- Size queue : " + this.owner.queue.size());
			if (owner.queue.isEmpty()) {
				this.servicing = null;
				this.isWaiting = true;
				this.nextEndServicingTaskFuture = null;
			} else {
				this.process();
			}
		}

	}

}
