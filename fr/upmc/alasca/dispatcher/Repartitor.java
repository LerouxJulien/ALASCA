package fr.upmc.alasca.dispatcher;


import java.util.ArrayList;

import fr.upmc.alasca.computer.components.VirtualMachine;
import fr.upmc.alasca.controleurAdmission.components.Controleur;
import fr.upmc.alasca.repartiteurBouchon.ports.RepartiteurOutboundPort;
import fr.upmc.alasca.requestgen.interfaces.RequestArrivalI;
import fr.upmc.alasca.requestgen.objects.Request;
import fr.upmc.components.AbstractComponent;
import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.components.cvm.pre.dcc.DynamicallyConnectableComponentI;
import fr.upmc.components.cvm.pre.dcc.DynamicallyConnectableComponentInboundPort;
import fr.upmc.components.cvm.pre.dcc.DynamicallyConnectableI;
import fr.upmc.components.ports.PortI;

/**
 * 
 * Classe <code> Repartitor </code> representant un repartiteur lie a une id d'application.
 * 
 * 
 * 
 * @author Julien Leroux
 *
 */
public class Repartitor extends AbstractComponent implements	DynamicallyConnectableI{

	// Controleur responsable du dispatcher
	Controleur control;
	// Liste des ports de sortie du repartiteur 1 port = 1 VM
	protected ArrayList<RepartiteurOutboundPort> Listrbp;
	//Port dynamique du répartiteur
	protected DynamicallyConnectableComponentInboundPort dccInboundPort ;
	
	/**
	 * 
	 * Creation du repartiteur
	 * 
	 * @param outboundPortURI
	 * @param controleur
	 * @throws Exception 
	 */
	
	public Repartitor(String outboundPortURI,Controleur controleur) throws Exception{
		this.addRequiredInterface(RequestArrivalI.class) ;
		control = controleur;
		Listrbp = new ArrayList<RepartiteurOutboundPort>();
		
		
		// partie Dynamique
		this.addOfferedInterface(DynamicallyConnectableComponentI.class) ;
		this.dccInboundPort =
			new DynamicallyConnectableComponentInboundPort(
					outboundPortURI + "-dcc", this) ;
		if (AbstractCVM.isDistributed) {
			this.dccInboundPort.publishPort() ;
		} else {
			this.dccInboundPort.localPublishPort() ;
		}
		this.addPort(dccInboundPort) ;
	}

	

	@Override
	public void connectWith(String serverPortURI, String clientPortURI,
			String ccname) throws Exception {
		PortI uriConsumerPort = this.findPortFromURI(clientPortURI) ;
		uriConsumerPort.doConnection(serverPortURI, ccname) ;
	}

	@Override
	public void disconnectWith(String serverPortURI, String clientPortURI)
			throws Exception {
		PortI uriConsumerPort = this.findPortFromURI(clientPortURI) ;
		uriConsumerPort.doDisconnection() ;
	}
	
	/**
	 * 
	 * Methode d'ajout d'un port au repartiteur en vue de connexion a une nouvelle VM 
	 * 
	 * @param portURI
	 * @throws Exception
	 */
	public void addNewPort(String portURI) throws Exception{
		RepartiteurOutboundPort rbp;
		
		
		rbp = new RepartiteurOutboundPort(portURI + "-RepartiteurOutboundPort", this) ;
		this.addPort(rbp) ;
		rbp.localPublishPort() ;
		
		Listrbp.add(rbp);
		
		
		
	}
	
	
	
	
	/**
	 * Methode de repartition des requettes sur la liste de port
	 * renvoi 0 si aucune creation de VM n'est requise
	 * un entier representant le nombre de VM a creer sinon
	 * 
	 * @param req
	 * @return int
	 * @throws Exception
	 */
	public int dispatch(Request req) throws Exception {
		for(RepartiteurOutboundPort rbp:Listrbp){
			
			if(!rbp.queueIsFull()){
				
				rbp.processRequest(req);
				return 0;
			}
		}
		return 1;
		}
	
		
		
		
	}

	


