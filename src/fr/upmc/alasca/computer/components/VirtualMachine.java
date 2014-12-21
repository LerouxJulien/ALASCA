package fr.upmc.alasca.computer.components;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import fr.upmc.alasca.computer.enums.Status;
import fr.upmc.alasca.computer.interfaces.VMConsumerI;
import fr.upmc.alasca.computer.interfaces.VMProviderI;
import fr.upmc.alasca.computer.objects.VMCarac;
import fr.upmc.alasca.computer.objects.VMMessages;
import fr.upmc.alasca.computer.objects.VMThread;
import fr.upmc.alasca.computer.ports.VMInboundPort;
import fr.upmc.alasca.computer.ports.VMOutboundPort;
import fr.upmc.alasca.requestgen.objects.Request;
import fr.upmc.alasca.requestgen.utils.TimeProcessing;
import fr.upmc.components.AbstractComponent;
import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.components.ports.PortI;

/**
 * La classe <code>VirtualMachine</code> definit la machine virtuelle qui
 * recupere les requetes transmises par le repartiteur de requetes et qui les
 * traitent.
 * 
 * <p>
 * Created on : 10 oct. 2014
 * </p>
 * 
 * @author <a href="mailto:nicolas.mounier@etu.upmc.fr">Nicolas Mounier</a>
 * @author <a href="mailto:henri.ng@etu.upmc.fr">Henri NG</a>
 * @version $Name$ -- $Revision$ -- $Date$
 */
public class VirtualMachine extends AbstractComponent {

	// ID de la VM
	private final String mvID;

	// ID de l'application
	private final int appID;

	// Nombre de coeurs attribues a la VM par la machine (de 1 a 16)
	private final int nbCores;

	// Frequence des coeurs de la VM
	private final List<Double> frequencies;

	// Statut de la VM
	// NEW  : VM venant d'etre deployee (Aucune requete n'est traitee.)
	// FREE : Libre (Au moins un fil d'execution est libre.)
	// BUSY : Occupe (Tous les fils d'execution sont occupes.)
	// IDLE : En attente d'eutanasie (S'il ne fout rien pendant X temps.)
	private Status status;

	// Nombre de requete traites par la VM
	private int nbRequest;

	// File d'attente de requetes
	protected BlockingQueue<Request> queue;

	// Fils d'execution des requetes
	private List<VMThread> threads;

	// Compteur pour alterner entre les differents coeurs
	protected int compteurCyclique = 0;

	VMInboundPort VMport;
	protected VMOutboundPort VMoport;
	
	/**
	 * @return the vMoport
	 */
	public VMOutboundPort getVMoport() {
		return VMoport;
	}

	

	/**
	 * Alloue une machine virtuelle.
	 * 
	 * L'allocation de la machine virtuelle se fait forcement avec une ID
	 * application associee. Le nombre de coeur est donnee par la machine hote.
	 *
	 * @param mvID
	 * @param appID
	 * @param frequencies
	 *            Liste des frequences des coeurs de la machine virtuelle
	 * @throws Exception
	 */
	public VirtualMachine(String port, String mvID, Integer appID,
			Integer queueMax, ArrayList<Double> frequencies) throws Exception {
		super();
		this.mvID = mvID;
		this.appID = appID;
		this.nbCores = frequencies.size();
		this.frequencies = new ArrayList<Double>(frequencies);
		this.status = Status.NEW;
		this.nbRequest = 0;
		queue = new LinkedBlockingQueue<Request>();

		// Initialise les VMThread de la VM
		threads = new ArrayList<VMThread>();
		for (int i = 0; i < nbCores; i++) {
			String VMThreadID = mvID + i + "";
			threads.add(new VMThread(VMThreadID, frequencies.get(i), this));
		}

		this.addOfferedInterface(VMProviderI.class);
		this.addRequiredInterface(VMConsumerI.class);
		
		VMport = new VMInboundPort(port, this);
		this.addPort(VMport);
		if (AbstractCVM.isDistributed) {
			VMport.publishPort() ;
		} else {
			VMport.localPublishPort() ;
		}
		
		VMoport = new VMOutboundPort(port+"outbound", this);
		this.addPort(VMoport);
		if (AbstractCVM.isDistributed) {
			VMoport.publishPort() ;
		} else {
			VMoport.localPublishPort() ;
		}
		
		/*VMMessages m = new VMMessages(getMvID(), status);
		VMCarac c = new VMCarac(this.getMvID(), this.getFrequencies());
		VMoport.notifyStatus(m);
		VMoport.notifyCarac(this.getMvID(),c);*/
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
	public String getMvID() {
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
	 * Retourne le nombre de coeurs en cours d'utilisation
	 * 
	 * @return nbCoresUsed
	 */
	public int getNbCoresUsed() {
		int nbCoresUsed = 0;
		for (int i = 0; i < nbCores; i++)
			if (threads.get(i).isWaiting())
				nbCoresUsed++;
		return nbCoresUsed;
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
	 * Retourne le nombre de requetes dans la file d'attente
	 * 
	 * @return size
	 */
	public int getQueueSize() {
		return queue.size();
	}

	/**
	 * Retourne le statut de la VM
	 * NEW  : VM venant d'etre deployee (Aucune requete n'est traitee.)
	 * FREE : Libre (Au moins un fil d'execution est libre.)
	 * BUSY : Occupe (Tous les fils d'execution sont occupes.)
	 * IDLE : En attente d'eutanasie (S'il ne fout rien pendant X temps.)
	 * 
	 * @return status
	 */
	public Status getStatus() {
		return status;
	}

	/**
	 * Teste si une des files d'execution est libre
	 * 
	 * @return boolean
	 */
	public boolean hasAvailableCore() {
		boolean available = false;
		for (int i = 0; i < nbCores; i++)
			available |= threads.get(i).isWaiting();
		return available;
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
	 * Recupere la requete envoyee par le repartiteur de requete et l'envoie
	 * dans la file d'attente. Le traitement de la requete debute lorsque
	 * le serveur a un fil d'execution de libre.
	 *
	 * @param r
	 * @throws Exception
	 */
	public void requestArrivalEvent(Request r) throws Exception {
		assert r != null;
		long t = System.currentTimeMillis();
		// La requete est directement placee dans la file d'attente.
		this.queue.add(r);
		System.out.println("Accepting request       " + r + " at "
				+ TimeProcessing.toString(t) + " --- Application number : "
				+ r.getAppId());
		r.setArrivalTime(t);
		// Aucun thread n'est disponible pour traiter la requete courante.
		if (!this.hasAvailableCore()) {
			status = Status.BUSY;
			System.out.println("Queueing request " + r);
			VMMessages m = new VMMessages(getMvID(), status);
			VMoport.notifyStatus(m);
		} else {
			// Parcours la liste de fils d'execution
			for (compteurCyclique = (compteurCyclique + 1) % getNbCores();
					compteurCyclique < nbCores;
					compteurCyclique = (compteurCyclique + 1) % getNbCores()) {
				// Verifie la disponibilite des fils d'execution pour les
				// requetes suivantes.
				if (getNbCoresUsed() == nbCores - 1) {
					status = Status.BUSY;
					VMMessages m = new VMMessages(getMvID(), status);
					VMoport.notifyStatus(m);
				}
				if (threads.get(compteurCyclique).isWaiting()) {
					threads.get(compteurCyclique).process();
					break;
				}
			}
		}
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

	public void setStatus(Status free) {
		this.status=free;	
	}
}

	
