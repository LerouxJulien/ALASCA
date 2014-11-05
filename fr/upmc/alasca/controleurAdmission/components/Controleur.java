package fr.upmc.alasca.controleurAdmission.components;

import java.util.ArrayList;

import fr.upmc.alasca.controleurAdmission.exceptions.NotEnoughRessourceException;
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
	protected Dispatcher dispatcher;
	protected ArrayList<String> listeURIComputer = new ArrayList<String>();
	protected ArrayList<String> listeURIVM = new ArrayList<String>();
	protected String uriPrefix;
	protected DynamicallyConnectableComponentInboundPort dccInboundPortClient;
	protected ArrayList<DynamicallyConnectableComponentInboundPort> listeDccInboundPortComputer = new ArrayList<DynamicallyConnectableComponentInboundPort>();
	
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
	
	public void transfertNouvelleApplication(int id) {
		dispatcher.createApplication(id);
	}
	
	public void demandeVM(int nbCoeur, int appId, Request r) throws Exception{
		int nbTrouvee = 0;
		
		/* TODO demande de coeur au lieu des VM */
		for(int i = 0; i < this.listeDccInboundPortComputer.size(); i++){
			nbTrouvee += ((ControleurConsumerComputerI) this.listeDccInboundPortComputer.get(i)).nbCoreDispo();
			if(nbTrouvee >= nbCoeur){
				break;
			}
		}
		if(nbTrouvee < nbCoeur){
			throw new NotEnoughRessourceException("Plus assez de VM disponible pour traiter la requ�te !");
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

	
