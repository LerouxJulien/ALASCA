package fr.upmc.alasca.repartiteurBouchon.components;

import java.util.ArrayList;
import java.util.List;

import fr.upmc.alasca.repartiteurBouchon.ports.RepartiteurOutboundPort;
import fr.upmc.alasca.requestgen.interfaces.RequestArrivalI;
import fr.upmc.alasca.requestgen.objects.Request;
import fr.upmc.components.AbstractComponent;
import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.components.cvm.pre.dcc.DynamicallyConnectableComponentI;
import fr.upmc.components.cvm.pre.dcc.DynamicallyConnectableComponentInboundPort;
import fr.upmc.components.cvm.pre.dcc.DynamicallyConnectableI;
import fr.upmc.components.ports.PortI;

public class RepartiteurBouchon extends AbstractComponent implements
		DynamicallyConnectableI {

	// protected RepartiteurOutboundPort rbp;
	protected DynamicallyConnectableComponentInboundPort dccInboundPort;
	
	protected Integer appId;
	
	protected Integer compteurPort = 0;
	
	protected String RepartiteurURIDCC;

	protected List<RepartiteurOutboundPort> rbps = new ArrayList<RepartiteurOutboundPort>();

	public RepartiteurBouchon(String outboundPortURI, Integer appId) throws Exception {
		this.addRequiredInterface(RequestArrivalI.class);
		/*
		 * this.rbp = new RepartiteurOutboundPort(outboundPortURI +
		 * "-RepartiteurOutboundPort", this) ; this.addPort(this.rbp) ;
		 * this.rbp.localPublishPort() ;
		 */

		this.appId = appId;
		this.RepartiteurURIDCC = outboundPortURI + "-dcc";
		// partie Dynanimique
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

	public boolean processRequest(Request r) throws Exception {
		for (RepartiteurOutboundPort rbp : rbps) {

			if (!rbp.queueIsFull()) {

				rbp.processRequest(r);
				return true;
			}
		}
		System.out.println("ALL QUEUES FULL");
		return false;
	}

	//TODO ameliorer
	public String addNewPort(String portURI) throws Exception {
		RepartiteurOutboundPort rbp;
		String URIused = portURI + (compteurPort++);

		rbp = new RepartiteurOutboundPort(URIused +  "-RepartiteurOutboundPort",
				this);
		this.addPort(rbp);
		rbp.localPublishPort();
		rbps.add(rbp);
		
		return URIused;
	}

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

	public int getAppId() {
		return appId;
	}
}
