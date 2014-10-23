package fr.upmc.alasca.controleurBouchon.components;

import java.util.ArrayList;

import fr.upmc.alasca.computer.components.Computer;
import fr.upmc.alasca.computer.interfaces.VMProviderI;
import fr.upmc.alasca.controleurBouchon.ports.ControleurBouchonInboundPort;
import fr.upmc.alasca.controleurBouchon.ports.ControleurBouchonOutboundPort;
import fr.upmc.alasca.repartiteurBouchon.components.RepartiteurBouchon;
import fr.upmc.alasca.requestgen.interfaces.RequestArrivalI;
import fr.upmc.alasca.requestgen.objects.Request;
import fr.upmc.components.AbstractComponent;

public class Controleur extends AbstractComponent{
	protected ArrayList<ControleurBouchonOutboundPort> portsToMachine = new ArrayList<ControleurBouchonOutboundPort>();
	
	String controleurPortOutboundURI;
	
	protected String repartiteurURI = "repartiteur";
	
	
	protected ControleurBouchonOutboundPort port_o;
	protected ControleurBouchonInboundPort port_i;
	private RepartiteurBouchon rb;
	
	public Controleur(String controleur_uri_outboundport, String controleur_uri_inboundport, Integer nb_computers) throws Exception{
		super(true, false);
		this.addRequiredInterface(VMProviderI.class);
		
		for(int i = 0 ; i < nb_computers; ++i){
			ControleurBouchonOutboundPort p = new ControleurBouchonOutboundPort(controleur_uri_outboundport + i, this);
			this.addPort(p);
			p.publishPort();
			portsToMachine.add(p);
		}
		
		rb = new RepartiteurBouchon(repartiteurURI);
		
		this.addOfferedInterface(RequestArrivalI.class) ;
		port_i = new ControleurBouchonInboundPort(controleur_uri_inboundport, this) ;
		this.addPort(port_i) ;
		port_i.publishPort();
	}
	
	//deploie 2 vm
	public void testConnexion() throws Exception{
		this.portsToMachine.get(0).deployVM(4, 0, repartiteurURI);
		this.portsToMachine.get(2).deployVM(2, 0, repartiteurURI);
	}
	
	//demande au repartiteur bouchon d'envoyer la requete
	public void acceptRequest(Request r) throws Exception {
		System.out.println("recu par le controleur");
		this.rb.processRequest(r);
	}
	
	public ArrayList<ControleurBouchonOutboundPort> getPortsToMachine() {
		return portsToMachine;
	}
}

	
