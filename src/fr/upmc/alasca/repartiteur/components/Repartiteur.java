package fr.upmc.alasca.repartiteur.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import fr.upmc.alasca.computer.objects.VMMessages;
import fr.upmc.alasca.computer.exceptions.NotEnoughCapacityVMException;
import fr.upmc.alasca.repartiteur.ports.RepartiteurOutboundPort;
import fr.upmc.alasca.requestgen.interfaces.RequestArrivalI;
import fr.upmc.alasca.requestgen.objects.Request;
import fr.upmc.components.AbstractComponent;
import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.components.cvm.pre.dcc.DynamicallyConnectableComponentI;
import fr.upmc.components.cvm.pre.dcc.DynamicallyConnectableComponentInboundPort;
import fr.upmc.components.cvm.pre.dcc.DynamicallyConnectableI;
import fr.upmc.components.ports.PortI;

/**
 * Le repartiteur de requete est charge de transmettre les requetes d'une unique
 * application aux VM auxquelles il est connecte. Il doit être vu comme un
 * sous-composant du Controleur, d'ou les appels directs permis du Controleur
 * aux methodes du repartiteur sans passer par des ports (processRequest et
 * addNewPort par exemple).
 *
 */
public class Repartiteur extends AbstractComponent implements
		DynamicallyConnectableI {

	// utilise pour la connexion dynamique aux VM
	protected DynamicallyConnectableComponentInboundPort dccInboundPort;

	// l'id de application associe a ce repartiteur
	protected Integer appId;

	// compteur utilise pour differencier les ports du repartiteur vers chaque
	// VM
	protected Integer compteurPort = 0;

	// uri du port permettant la connexion dynamique
	protected String RepartiteurURIDCC;

	// Liste des ports des machines virtuelles
	/* A virer */protected List<RepartiteurOutboundPort> rbps;
	protected Map<RepartiteurOutboundPort, VMMessages> robps;
	
	// Port courant de la VM traitant la derniere requete recues
	protected Iterator<Map.Entry<RepartiteurOutboundPort, VMMessages>> robpIt;
	
	// Liste des requetes 
	protected ArrayList<Request> listR;
	
	/**
	 * Constructeur du repartiteur
	 * 
	 * @param outboundPortURI port de sortie du repartiteur
	 * @param appId id de l'application liée au repartiteur
	 */
	public Repartiteur(String outboundPortURI, Integer appId) throws Exception {
		this.addRequiredInterface(RequestArrivalI.class);
		this.listR = new ArrayList<Request>();
		this.appId = appId;
		this.RepartiteurURIDCC = outboundPortURI + "-dcc";
		/* A virer */this.rbps = new ArrayList<RepartiteurOutboundPort>();
		this.robps = new HashMap<RepartiteurOutboundPort, VMMessages>();
		this.robpIt = robps.entrySet().iterator();

		this.addOfferedInterface(DynamicallyConnectableComponentI.class);
		this.dccInboundPort = new DynamicallyConnectableComponentInboundPort(
				RepartiteurURIDCC, this);
		if (AbstractCVM.isDistributed) {
			this.dccInboundPort.publishPort();
		} else {
			this.dccInboundPort.localPublishPort();
		}
		this.addPort(dccInboundPort);
	}
	
	/**
	 * Ajoute un nouveau port vers une machine virtuelle
	 *
	 * @param portURI
	 *            base/prefixe de l'uri du port cree
	 * @return uri actuellement utilisee pour le port cree
	 * @throws Exception
	 */
	public String addNewPort(String portURI) throws Exception {
		RepartiteurOutboundPort rbp;
		String URIused = portURI + (compteurPort++);

		rbp = new RepartiteurOutboundPort(URIused + "-RepartiteurOutboundPort",
				this);
		this.addPort(rbp);
		rbp.localPublishPort();
		/* A virer */rbps.add(rbp);
		robps.put(rbp, null);

		return URIused;
	}

	@Override
	public void connectWith(String serverPortURI, String clientPortURI,
			String ccname) throws Exception {
		PortI uriConsumerPort = this.findPortFromURI(clientPortURI);
		uriConsumerPort.doConnection(serverPortURI, ccname);
	}

	@Override
	public void disconnectWith(String serverPortURI, String clientPortURI)
			throws Exception {
		PortI uriConsumerPort = this.findPortFromURI(clientPortURI);
		uriConsumerPort.doDisconnection();
	}

	/**
	 * @return l'id de application associee a ce repartiteur
	 */
	public int getAppId() {
		return appId;
	}
	
	/**
	 * 
	 * 
	 * @return uri du port permettant la connexion dynamique
	 */
	public String getRepartiteurURIDCC() {
		return RepartiteurURIDCC;
	}

	/**
	 * Notifie le repartiteur de requetes de l'etat d'une VM et/ou de la fin de
	 * traitement d'une requete
	 * 
	 * @param m Notification de la VM au repartiteur de requetes
	 * @throws Exception
	 */
	public void notifyStatus(VMMessages m) throws Exception {
		robps.put(m.getRepPort(), m);
	}

	/**
	 * Transmet la requete r a une machine virtuelle
	 * 
	 * Les machines virtuelles recoivent tour a tour les requetes transmises par
	 * le repartiteur de requetes (pour le moment).
	 *
	 * @param r requete a transmettre
	 * @return false si aucune machine virtuelle ne peut traiter la requete
	 * @throws Exception
	 */
	// TODO : Changer la methode d'attribution des requetes
	/*public boolean processRequest(Request r) throws Exception {
		for (RepartiteurOutboundPort rbp : rbps) {
			rbp.processRequest(r);
			return true;
		}
		System.out.println("No available mv for the application number: " + r.getAppId());
		return false;
	}*/
	public void processRequest(Request r) throws Exception {
		/*
		Map.Entry<RepartiteurOutboundPort, Status> vm = null;
		if (!robpIt.hasNext()) {
			robpIt = robps.entrySet().iterator();
			return false;
		}
		else {
			vm = robpIt.next();
			vm.getKey().processRequest(r);
		}
		*/
		for (RepartiteurOutboundPort robp : robps.keySet()) {
			robp.processRequest(r);
			return;
		}
		throw new NotEnoughCapacityVMException("No available mv for the " +
		"application number: " + r.getAppId());
	}
	
}