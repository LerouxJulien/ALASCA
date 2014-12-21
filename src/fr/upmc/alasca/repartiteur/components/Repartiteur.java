package fr.upmc.alasca.repartiteur.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import fr.upmc.alasca.computer.objects.VMCarac;
import fr.upmc.alasca.computer.objects.VMMessages;
import fr.upmc.alasca.computer.enums.Status;
import fr.upmc.alasca.computer.exceptions.NotEnoughCapacityVMException;
import fr.upmc.alasca.repartiteur.interfaces.RepartiteurConsumerI;
import fr.upmc.alasca.repartiteur.interfaces.RepartiteurProviderI;
import fr.upmc.alasca.repartiteur.ports.RepartiteurInboundPort;
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
		DynamicallyConnectableComponentI,DynamicallyConnectableI {

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
	protected Map<RepartiteurInboundPort,RepartiteurOutboundPort> rbps;
	protected Map<RepartiteurInboundPort, VMMessages> robps;
	
	// Liste des requetes 
	protected ArrayList<Request> listR;
	
	// Liste des caractéristiques des VM
	protected HashMap<String,VMCarac> listCarac;
	
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
		this.rbps = new HashMap<RepartiteurInboundPort, RepartiteurOutboundPort>();
		this.robps = new HashMap<RepartiteurInboundPort, VMMessages>();
		this.listCarac = new HashMap<String,VMCarac>();

		this.addOfferedInterface(RepartiteurProviderI.class);
		this.addRequiredInterface(RepartiteurConsumerI.class);
		
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
	public String[] addNewPorts(String portURI) throws Exception {
		
		String[] uritab = new String[2];
		
		RepartiteurOutboundPort rbp;
		String URIused = portURI + (compteurPort++);
		uritab[0] = URIused;
		
		rbp = new RepartiteurOutboundPort(URIused + "-RepartiteurOutboundPort",
				this);
		this.addPort(rbp);
		rbp.localPublishPort();
		
		RepartiteurInboundPort rip = null;
		String URIusedi = portURI + (compteurPort++);
		uritab[1] = URIusedi;
		try{
		rip = new RepartiteurInboundPort(URIusedi + "-RepartiteurInboundPort", this);
		}catch(Exception e){
			System.out.println("Probleme new " );e.printStackTrace();
			
		}
		
		this.addPort(rip);
		
		rip.localPublishPort();
		
		rbps.put(rip,rbp);
		robps.put(rip, null);

		return uritab;
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
		System.out.println("Received status from "+ m.getVmID());
		robps.put(m.getRepPort(), m);
		
		
		
		if (m.getStatus()== Status.NEW || m.getStatus() == Status.FREE){
			
			RepartiteurOutboundPort po = rbps.get(m.getRepPort());
			
			sendNextRequest(po);
			
		}
			
			
		if (m.getTime()!=0){
			
			
			this.listCarac.get(m.getVmID()).addTime(m.getTime());
			System.out.println("Temps moyen de traitement pour la vm"+this.listCarac.get(m.getVmID()).getMediumtime());
			
		}
	}
		
		
	

	private void sendNextRequest(RepartiteurOutboundPort po) throws Exception {
		po.processRequest(this.listR.remove(0));
		
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
		/*for (RepartiteurOutboundPort robp : robps.keySet()) {
			robp.processRequest(r);
			return;
		}
		throw new NotEnoughCapacityVMException("No available mv for the " +
		"application number: " + r.getAppId());*/
		if(this.rbps.isEmpty()){
			
			throw new NotEnoughCapacityVMException("No available mv for the " +
					"application number: " + r.getAppId());
			
		}
		this.listR.add(r);
		System.out.println("Stockage de requette "+ r.getAppId()+ " - " + listR.size());
		
	}
	
	
	

	public void notifyCarac(String id, VMCarac c) {
		this.listCarac.put(id, c);
		
	}
	
}