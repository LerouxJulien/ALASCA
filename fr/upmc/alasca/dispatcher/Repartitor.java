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
 * Classe <code> Repartitor </code> repr�sentant un r�partiteur li� a une if d'application.
 * 
 * 
 * 
 * @author Julien Leroux
 *
 */
public class Repartitor extends AbstractComponent implements	DynamicallyConnectableI{

	//private static long VMMAXINST = 20000000;
	Controleur control;
	protected ArrayList<RepartiteurOutboundPort> Listrbp;
	protected DynamicallyConnectableComponentInboundPort dccInboundPort ;
	
	public Repartitor(String outboundPortURI,Controleur controleur) throws Exception{
		this.addRequiredInterface(RequestArrivalI.class) ;
		control = controleur;
		Listrbp = new ArrayList<RepartiteurOutboundPort>();
		
		
		// partie Dynanimique
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
	 * Cr�ation du r�partiteur
	 * 
	 * @param controleur
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
	 * Methode de repartition des requettes sur une liste de VM
	 * renvoi 0 si aucune cr�ation de VM n'est requise
	 * un entier repr�sentant le nombre a creer sinon
	 * 
	 * @param req
	 * @param sendingList
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

	


