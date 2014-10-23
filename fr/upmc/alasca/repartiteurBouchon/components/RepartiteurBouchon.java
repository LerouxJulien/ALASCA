package fr.upmc.alasca.repartiteurBouchon.components;

import fr.upmc.alasca.repartiteurBouchon.ports.RepartiteurOutboundPort;
import fr.upmc.alasca.requestgen.interfaces.RequestArrivalI;
import fr.upmc.alasca.requestgen.objects.Request;
import fr.upmc.components.AbstractComponent;
import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.components.cvm.pre.dcc.DynamicallyConnectableComponentI;
import fr.upmc.components.cvm.pre.dcc.DynamicallyConnectableComponentInboundPort;
import fr.upmc.components.cvm.pre.dcc.DynamicallyConnectableI;
import fr.upmc.components.ports.PortI;

public class RepartiteurBouchon extends AbstractComponent implements	DynamicallyConnectableI{
	
	protected RepartiteurOutboundPort rbp;
	protected DynamicallyConnectableComponentInboundPort dccInboundPort ;
	
	public RepartiteurBouchon(String outboundPortURI) throws Exception{
		this.addRequiredInterface(RequestArrivalI.class) ;
		this.rbp = new RepartiteurOutboundPort(outboundPortURI + "-RepartiteurOutboundPort", this) ;
		this.addPort(this.rbp) ;
		this.rbp.localPublishPort() ;
		
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

	public void processRequest(Request r) throws Exception {
		this.rbp.processRequest(r);
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
}
