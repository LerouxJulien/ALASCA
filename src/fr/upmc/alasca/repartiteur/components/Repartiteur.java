package fr.upmc.alasca.repartiteur.components;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import fr.upmc.alasca.computer.enums.Status;
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
 * application aux VM auxquelles il est connecte. Il doit �tre vu comme un
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
	
	// Liste des machines virtuelles du repartiteur de requetes
	protected Map<RepartiteurOutboundPort, Status> rbps = new HashMap<RepartiteurOutboundPort, Status>();
	
	// Derniere machine virtuelle a avoir recu une requete du repartiteur
	protected Iterator<Entry<RepartiteurOutboundPort, Status>> current_rbp = rbps.entrySet().iterator();
	
	/**
	 * Constructeur du repartiteur
	 * 
	 * @param outboundPortURI port de sortie du repartiteur
	 * @param appId id de l'application li�e au repartiteur
	 */
	public Repartiteur(String outboundPortURI, Integer appId) throws Exception {
		this.addRequiredInterface(RequestArrivalI.class);

		this.appId = appId;
		this.RepartiteurURIDCC = outboundPortURI + "-dcc";

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
	 * Transmet la requete r a une machine virtuelle
	 * Les machines virtuelles recoivent tour a tour les requetes envoyes par
	 * le repartiteur de requetes. 
	 *
	 * @param r requete a transmettre
	 * @return false si toutes les machines virtuelle ont une queue pleine
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public boolean processRequest(Request r) throws Exception {
		Map.Entry rbp;
		if (rbps.isEmpty())
			return false;
		// Fin de la liste de machines virtuelles du repartiteur de requetes
		if (!current_rbp.hasNext())
			current_rbp = rbps.entrySet().iterator();
		rbp = (Map.Entry)current_rbp.next();
		((RepartiteurOutboundPort)rbp.getKey()).processRequest(r);
		System.out.println("No available mv for the application number: " + r.getAppId());
		return true;
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
		rbps.put(rbp, Status.NEW);

		return URIused;
	}

	/**
	 * 
	 * 
	 * @return uri du port permettant la connexion dynamique
	 */
	public String getRepartiteurURIDCC() {
		return RepartiteurURIDCC;
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
}
