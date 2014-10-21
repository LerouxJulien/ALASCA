package fr.upmc.alasca.controleurAdmission.components;

import java.util.ArrayList;

import fr.upmc.alasca.computer.objects.Computer;
import fr.upmc.alasca.controleurAdmission.interfaces.ControleurConsumerClientI;
import fr.upmc.alasca.controleurAdmission.interfaces.ControleurConsumerComputerI;
import fr.upmc.alasca.controleurAdmission.interfaces.URISortieControleurI;
import fr.upmc.alasca.controleurAdmission.ports.URIControleurInboundPort;
import fr.upmc.alasca.controleurAdmission.ports.URIControleurOutboundPort;
import fr.upmc.alasca.dispatcher.Dispatcher;
import fr.upmc.alasca.requestgen.objects.Request;
import fr.upmc.components.AbstractComponent;
import fr.upmc.components.cvm.pre.dcc.DynamicallyConnectableComponentI;
import fr.upmc.components.cvm.pre.dcc.DynamicallyConnectableComponentInboundPort;
import fr.upmc.components.cvm.pre.dcc.DynamicallyConnectableI;
import fr.upmc.components.ports.PortI;

/**
 * La classe <code>Controleur</code>
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
	Dispatcher dispatcher;
	ArrayList<String> listePortComputer = new ArrayList<String>();
	String uriPrefix;
	protected DynamicallyConnectableComponentInboundPort dccInboundPortClient;
	protected DynamicallyConnectableComponentInboundPort dccInboundPortComputer;
	
	public Controleur(String uriPrefix) throws Exception{
		super(true, false);
		this.uriPrefix = uriPrefix;
		this.addOfferedInterface(DynamicallyConnectableComponentI.class);
		this.addRequiredInterface(ControleurConsumerClientI.class);
		this.addRequiredInterface(ControleurConsumerComputerI.class);
		this.dccInboundPortClient = new DynamicallyConnectableComponentInboundPort(uriPrefix + "client-dcc", this);
		this.dccInboundPortComputer = new DynamicallyConnectableComponentInboundPort(uriPrefix + "computer-dcc", this);
		this.addPort(this.dccInboundPortClient);
		this.addPort(this.dccInboundPortComputer);
		this.dccInboundPortClient.publishPort();
		this.dccInboundPortComputer.publishPort();
	}
	
	public void transfertRequeteDispatcher(Request r){
		dispatcher.sendApplication(r, new ArrayList<String>());
	}
	
	public ArrayList<String> demandeRessource(int nbRessource){
		int nbTrouvee = 0;
		ArrayList<String> listePortVM = new ArrayList<String>();
		
		for(int i = 0; i < this.listePortComputer.size(); i++){
			Computer c = new Computer(i, i, null, i);
			/* connexion au composant ordinateur, ca reste flou sur le fonctionnement */
			this.getRequiredInterfaces();
		}
	}
	
	public void allouerComputer(){
		
	}
	
	public String provideURIService() {
		return uriPrefix;
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

	
