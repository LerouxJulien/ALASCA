package fr.upmc.alasca.controleurAdmission.components;

import java.util.ArrayList;

import fr.upmc.alasca.controleurAdmission.exceptions.NotEnoughCoreException;
import fr.upmc.alasca.controleurAdmission.interfaces.ControleurConsumerComputerI;
import fr.upmc.alasca.controleurAdmission.interfaces.ControleurProviderClientI;
import fr.upmc.alasca.dispatcher.Dispatcher;
import fr.upmc.alasca.requestgen.objects.Request;
import fr.upmc.components.AbstractComponent;
import fr.upmc.components.cvm.pre.dcc.DynamicallyConnectableComponentI;
import fr.upmc.components.cvm.pre.dcc.DynamicallyConnectableComponentInboundPort;
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
	
	//La liste des URI des VM libres
	protected ArrayList<String> listeURIVM = new ArrayList<String>();
	
	//URI du composant Controleur
	protected String uriPrefix;
	
	//Port de liaison avec le Client (unique pour l'instant)
	protected DynamicallyConnectableComponentInboundPort dccInboundPortClient;
	
	//Ports de liaison avec les différents Computer
	protected ArrayList<DynamicallyConnectableComponentInboundPort> listeDccInboundPortComputer = new ArrayList<DynamicallyConnectableComponentInboundPort>();
	
	
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
		
		this.addOfferedInterface(DynamicallyConnectableComponentI.class);
		this.addOfferedInterface(ControleurProviderClientI.class);
		this.addRequiredInterface(ControleurConsumerComputerI.class);
		
		this.dccInboundPortClient = new DynamicallyConnectableComponentInboundPort(uriPrefix + "client-dcc", this);
		for(int i = 0; i < listeURIComputer.size(); i++){
			listeDccInboundPortComputer.add(new DynamicallyConnectableComponentInboundPort(uriPrefix + "computer" + i + "-dcc", this));
		}
		
		this.addPort(this.dccInboundPortClient);
		for(int i = 0; i < listeDccInboundPortComputer.size(); i++){
			this.addPort(listeDccInboundPortComputer.get(i));
		}
		
		this.dccInboundPortClient.publishPort();
		for(int i = 0; i < listeDccInboundPortComputer.size(); i++){
			listeDccInboundPortComputer.get(i).publishPort();;
		}
	}
	
	/**
	 * fonction de récupération et d'envoi d'une requête Client vers le Dispatcher
	 * @param r la requête a transférer au Dispatcher
	 * @throws Exception
	 */
	public void transfertRequeteDispatcher(Request r) throws Exception{
		this.listeURIVM.clear();
		for(int i = 0; i < this.listeDccInboundPortComputer.size(); i++){
			try {
				this.listeURIVM.addAll(((ControleurConsumerComputerI) listeDccInboundPortComputer.get(i)).getListVM());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		dispatcher.processRequest(r, listeURIVM);
	}
	
	/**
	 * fonction de récupération et d'envoi d'une application Client vers le Dispatcher
	 * @param id l'application a transférer au Dispatcher
	 */
	public void transfertNouvelleApplication(int id) {
		dispatcher.createApplication(id);
	}
	
	/**
	 * fonction parcourant la liste des Computer pour demander le deploiement de nouvelles VM
	 * @param nbCoeur le nombre de coeur requis pour le traitement de la requête
	 * @param appId l'application de la requête a traiter
	 * @param r la requête a traiter
	 * @throws Exception
	 */
	public void demandeVM(int nbCoeur, int appId, Request r) throws Exception {
		int nbTrouvee = 0;
		
		/* TODO demande de coeur au lieu des VM */
		for(int i = 0; i < this.listeDccInboundPortComputer.size(); i++){
			nbTrouvee += ((ControleurConsumerComputerI) this.listeDccInboundPortComputer.get(i)).nbCoreDispo();
			if(nbTrouvee >= nbCoeur){
				break;
			}
		}
		if(nbTrouvee < nbCoeur){
			throw new NotEnoughCoreException("Plus assez de coeurs disponible pour traiter la requête !");
		}
		nbTrouvee = 0;
		for(int i = 0; i < this.listeDccInboundPortComputer.size(); i++){
			int nbVMAPrendre;
			if(((ControleurConsumerComputerI) this.listeDccInboundPortComputer.get(i)).nbCoreDispo() > nbCoeur - nbTrouvee){
				nbVMAPrendre = nbCoeur - nbTrouvee;
			} else {
					nbVMAPrendre = ((ControleurConsumerComputerI) this.listeDccInboundPortComputer.get(i)).nbCoreDispo();
				}
				for(int j = 0; j < nbVMAPrendre; j++){
					nbTrouvee++;
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

	
