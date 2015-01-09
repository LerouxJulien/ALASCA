package fr.upmc.alasca.repartiteur.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import fr.upmc.alasca.computer.enums.Status;
import fr.upmc.alasca.computer.interfaces.VMProviderI;
import fr.upmc.alasca.computer.objects.VMMessages;
import fr.upmc.alasca.controleur.components.Controleur;
import fr.upmc.alasca.repartiteur.ports.RepartiteurInboundPort;
import fr.upmc.alasca.repartiteur.ports.RepartiteurToVMInboundPort;
import fr.upmc.alasca.repartiteur.ports.RepartiteurToVMOutboundPort;
import fr.upmc.alasca.requestgen.interfaces.RequestArrivalI;
import fr.upmc.alasca.requestgen.objects.Request;
import fr.upmc.components.AbstractComponent;
import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.components.cvm.pre.dcc.DynamicComponentCreationI;
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
	protected String repartiteurURIDCC;
	
	protected String repartiteurURIBase_outbound;
	

	// Liste des ports des machines virtuelles
	/* A virer */protected List<RepartiteurToVMOutboundPort> rbps;
	protected Map<RepartiteurToVMOutboundPort, Status> robps;
	
	//port par lequel le repartiteur recoit directement les requetes
	protected RepartiteurToVMInboundPort rip;
	
	protected RepartiteurInboundPort rgToRepartiteurInboundPort;
	
	// Port courant de la VM traitant la derniere requete recues
	protected Iterator<Map.Entry<RepartiteurToVMOutboundPort, Status>> robpIt;
	
	/**
	 * Constructeur du repartiteur
	 * 
	 * @param outboundPortURI port de sortie du repartiteur
	 * @param appId id de l'application liée au repartiteur
	 */
	public Repartiteur(String portURI, Integer appId) throws Exception {
		this.addRequiredInterface(RequestArrivalI.class);

		this.appId = appId;
		this.repartiteurURIDCC = portURI + "-dcc";
		this.repartiteurURIBase_outbound = portURI + "-outboundPort";
		/* A virer */this.rbps = new ArrayList<RepartiteurToVMOutboundPort>();
		this.robps = new HashMap<RepartiteurToVMOutboundPort, Status>();
		this.robpIt = robps.entrySet().iterator();
		PortI p = this.rgToRepartiteurInboundPort;
		
		this.addOfferedInterface(RequestArrivalI.class);
		p = new RepartiteurInboundPort(portURI, this);
		
		this.addRequiredInterface(VMProviderI.class);

		this.addOfferedInterface(DynamicallyConnectableComponentI.class);
		this.dccInboundPort = new DynamicallyConnectableComponentInboundPort(
				repartiteurURIDCC, this);
		if (AbstractCVM.isDistributed) {
			p.publishPort();
			this.dccInboundPort.publishPort();
		} else {
			p.localPublishPort();
			this.dccInboundPort.localPublishPort();
		}
		this.addPort(dccInboundPort);
		
		this.addRequiredInterface(DynamicComponentCreationI.class);
		this.addRequiredInterface(DynamicallyConnectableComponentI.class);
	}
	
	/**
	 * Ajoute un nouveau port vers une machine virtuelle
	 *
	 * @param portURI
	 *            base l'uri du port cree : repartiteur<numeroAppId>
	 * @return uri actuellement utilisee pour le port cree
	 * @throws Exception
	 */
	public String addNewPort() throws Exception {
		RepartiteurToVMOutboundPort rbp;
		String URIused = repartiteurURIBase_outbound + (compteurPort++);

		rbp = new RepartiteurToVMOutboundPort(URIused + "-RepartiteurOutboundPort",
				this);
		this.addPort(rbp);
		if (AbstractCVM.isDistributed) {
			rbp.publishPort();
		} else {
			rbp.localPublishPort();
		}
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
		return repartiteurURIDCC;
	}

	public String RepartiteurURIBase_outbound() {
		return repartiteurURIBase_outbound;
	}

	/**
	 * Notifie le repartiteur de requetes de l'etat d'une VM et/ou de la fin de
	 * traitement d'une requete
	 * 
	 * @param m Notification de la VM au repartiteur de requetes
	 * @throws Exception
	 */
	public void notifyRR(VMMessages m) throws Exception {
		
	}
	
	public void acceptRequest(Request r) throws Exception {
		this.processRequest(r);
		/*if(rbs.containsKey(r.getAppId())){

            Repartiteur rr = rbs.get(r.getAppId());

            if (!rr.processRequest(r)) {
                String URInewPortRepartiteur = repartiteurURIgenericName
							+ rr.getAppId();
                if (this.deployVM(rr, URInewPortRepartiteur))
						rr.processRequest(r);
                else
						System.out
								.println("Rejected request: all queues full and maximal number of mv reached");
            }
            return;

		}else{
		System.out
				.println("Rejected request: no dispatcher dedicated to the application number: "
						+ r.getAppId());
		}*/
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
	public boolean processRequest(Request r) throws Exception {
		for (RepartiteurToVMOutboundPort robp : robps.keySet()) {
			robp.processRequest(r);
			return true;
		}
		System.out.println("No available mv for the application number: " + r.getAppId());
		return false;
	}
	
}
