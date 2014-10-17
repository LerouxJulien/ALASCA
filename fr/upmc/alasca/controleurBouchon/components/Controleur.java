package fr.upmc.alasca.controleurBouchon.components;

import java.util.ArrayList;

import fr.upmc.alasca.computer.components.Computer;
import fr.upmc.alasca.computer.interfaces.ManagementVMI;
import fr.upmc.alasca.controleurBouchon.ports.ControleurBouchonInboundPort;
import fr.upmc.alasca.controleurBouchon.ports.ControleurBouchonOutboundPort;
import fr.upmc.alasca.requestgen.interfaces.RequestArrivalI;
import fr.upmc.components.AbstractComponent;
import fr.upmc.components.ports.PortI;

public class Controleur extends AbstractComponent{
	ArrayList<Computer> listeMachineLibre = new ArrayList<Computer>();
	String controleurPortOutboundURI;
	protected ControleurBouchonOutboundPort port_o;
	protected ControleurBouchonInboundPort port_i;
	
	public Controleur(String controleur_uri_outboundport, String controleur_uri_inboundport) throws Exception{
		super(true, false);
		this.addRequiredInterface(ManagementVMI.class);
		this.port_o = new ControleurBouchonOutboundPort(controleur_uri_outboundport, this);
		this.addPort(port_o);
		port_o.publishPort();
		
		this.addOfferedInterface(RequestArrivalI.class) ;
		port_i = new ControleurBouchonInboundPort(controleur_uri_inboundport, this) ;
		this.addPort(port_i) ;
		port_i.publishPort();
	}
	
	public void testConnexion() throws Exception{
		this.port_o.deployVM(4, 0);
	}
}

	
