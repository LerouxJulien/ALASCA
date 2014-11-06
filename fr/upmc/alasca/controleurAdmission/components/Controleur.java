package fr.upmc.alasca.controleurAdmission.components;

import java.util.ArrayList;

import fr.upmc.alasca.controleurAdmission.exceptions.NotEnoughCoreException;
import fr.upmc.alasca.controleurAdmission.interfaces.ControleurConsumerComputerI;
import fr.upmc.alasca.controleurAdmission.interfaces.ControleurProviderClientI;
import fr.upmc.alasca.controleurAdmission.ports.URIControleurInboundPort;
import fr.upmc.alasca.controleurAdmission.ports.URIControleurOutboundPort;
import fr.upmc.alasca.dispatcher.Dispatcher;
import fr.upmc.alasca.requestgen.objects.Request;
import fr.upmc.components.AbstractComponent;
import fr.upmc.components.cvm.pre.dcc.DynamicallyConnectableI;
import fr.upmc.components.ports.PortI;

/**
 * La classe <code>Controleur</code> reçoit les demandes de creation d'application et de requêtes des
 * <code>Client</code> et les transmet au <code>Dispatcher</code> qui les traitera.
 * Il se connecte a chaque <code>Computer</code> afin de maintenir sa liste de <code>VirtualMachine</code>
 * a jour.
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
 * @author	<a href="mailto:william.chasson@etu.upmc.fr">William CHASSON</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */

public class Controleur extends AbstractComponent implements DynamicallyConnectableI{
	//Le Dispatcher qui traitera les applications et requêtes
	protected Dispatcher dispatcher;
	
	//La liste des URI des Computer
	protected ArrayList<String> listeURIComputer = new ArrayList<String>();
	
	//URI du composant Controleur
	protected String uriPrefix;
	
	//Port de liaison avec le Client (unique pour l'instant)
	protected URIControleurInboundPort inboundPortClient;
	
	//Ports de liaison avec les différents Computer
	protected ArrayList<URIControleurOutboundPort> listeInboundPortComputer = new ArrayList<URIControleurOutboundPort>();
	
	
	/**
	 * Création du controleur d'admission
	 * @param uriPrefix l'URI du controleur
	 * @param listeURIComputer la liste des URI des différents Computer
	 * @throws Exception
	 */
	public Controleur(String uriPrefix, ArrayList<String> listeURIComputer) throws Exception{
		super(true, false);
		this.uriPrefix = uriPrefix;
		this.listeURIComputer = listeURIComputer;
		this.dispatcher = new Dispatcher(this);
		
		this.addOfferedInterface(ControleurProviderClientI.class);
		this.addRequiredInterface(ControleurConsumerComputerI.class);
		
		this.inboundPortClient = new URIControleurInboundPort(uriPrefix + "client-dcc", this);
		for(int i = 0; i < listeURIComputer.size(); i++){
			listeInboundPortComputer.add(new URIControleurOutboundPort(uriPrefix + "computer" + i + "-dcc", this));
		}
		
		this.addPort(this.inboundPortClient);
		for(int i = 0; i < listeInboundPortComputer.size(); i++){
			this.addPort(listeInboundPortComputer.get(i));
		}
		
		this.inboundPortClient.publishPort();
		for(int i = 0; i < listeInboundPortComputer.size(); i++){
			listeInboundPortComputer.get(i).publishPort();
		}
	}
	
	/**
	 * fonction de récupération et d'envoi d'une requête Client vers le Dispatcher
	 * @param r la requête a transférer au Dispatcher
	 * @throws Exception
	 */
	public void transfertRequeteDispatcher(Request r) throws Exception{
		dispatcher.processRequest(r);
	}
	
	/**
	 * fonction de récupération et d'envoi d'une application Client vers le Dispatcher
	 * @param id l'application a transférer au Dispatcher
	 * @throws Exception 
	 */
	public void transfertNouvelleApplication(int id) throws Exception {
		dispatcher.createApplication(id);
	}
	
	/**
	 * fonction parcourant la liste des Computer pour demander le deploiement de nouvelles VM
	 * @param nbCoeur le nombre de coeur requis pour le traitement de la requête
	 * @param appId l'application de la requête a traiter
	 * @param r la requête a traiter
	 * @throws Exception
	 */
	public void demandeVM(int nbCoeur, int appId, Request r, String URIRepartiteur) throws Exception {
		int nbTrouvee = 0;
		
		for(int i = 0; i < this.listeInboundPortComputer.size(); i++){
			nbTrouvee += this.listeInboundPortComputer.get(i).nbCoreDispo();
			if(nbTrouvee >= nbCoeur){
				break;
			}
		}
		if(nbTrouvee < nbCoeur){
			throw new NotEnoughCoreException("Plus assez de coeurs disponible pour traiter la requête !");
		}
		nbTrouvee = 0;
		for(int i = 0; i < this.listeInboundPortComputer.size(); i++){
			int nbCoreAPrendre;
			if(this.listeInboundPortComputer.get(i).nbCoreDispo() > nbCoeur - nbTrouvee){
				nbCoreAPrendre = nbCoeur - nbTrouvee;
			} else {
				nbCoreAPrendre = this.listeInboundPortComputer.get(i).nbCoreDispo();
			}
			if(nbCoreAPrendre > 0){
				this.listeInboundPortComputer.get(i).deployVM(nbCoreAPrendre, appId, URIRepartiteur);
				nbTrouvee += nbCoreAPrendre;
			}
			if(nbTrouvee == nbCoeur){
				break;
			}
		}
		transfertRequeteDispatcher(r);
	}

	@Override
	public void connectWith(String serverPortURI, String clientPortURI, String ccname) throws Exception {
		PortI uriConsumerPort = this.findPortFromURI(clientPortURI) ;
		uriConsumerPort.doConnection(serverPortURI, ccname);
	}

	@Override
	public void disconnectWith(String serverPortURI, String clientPortURI) throws Exception {
		PortI uriConsumerPort = this.findPortFromURI(clientPortURI) ;
		uriConsumerPort.doDisconnection();
	}
}

	
