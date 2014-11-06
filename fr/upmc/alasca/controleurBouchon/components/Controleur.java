package fr.upmc.alasca.controleurBouchon.components;

import java.util.ArrayList;
import java.util.List;

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
	
	protected String repartiteurURIgenericName = "repartiteur";
	
	
	protected ControleurBouchonOutboundPort port_o;
	protected ControleurBouchonInboundPort port_i;
	//private RepartiteurBouchon rb;
	protected List<RepartiteurBouchon> rbs = new ArrayList<RepartiteurBouchon>();
	
	public Controleur(String controleur_uri_outboundport, String controleur_uri_inboundport, Integer nb_computers) throws Exception{
		super(true, false);
		this.addRequiredInterface(VMProviderI.class);
		
		for(int i = 0 ; i < nb_computers; ++i){
			ControleurBouchonOutboundPort p = new ControleurBouchonOutboundPort(controleur_uri_outboundport + i, this);
			this.addPort(p);
			p.publishPort();
			portsToMachine.add(p);
		}
		
		//rb = new RepartiteurBouchon(repartiteurURI + "0", 0);
		/*rbs.add(new RepartiteurBouchon(repartiteurURI + "0", 0));
		rbs.add(new RepartiteurBouchon(repartiteurURI + "1", 1));*/
		
		this.addOfferedInterface(RequestArrivalI.class) ;
		port_i = new ControleurBouchonInboundPort(controleur_uri_inboundport, this) ;
		this.addPort(port_i) ;
		port_i.publishPort();
	}
	
	/**
	 * Deploie une vm sur un computer s'il y a de la place
	 * @param r repartiteur connecte a la vm deployee
	 * @param repartiteurURIFixe uri du port sur lequel le repartiteur transmet les requetes
	 * @return
	 * @throws Exception
	 */
	public boolean deployVM(RepartiteurBouchon r, String repartiteurURIFixe) throws Exception{
		//TODO actuellement c'est n'importe quoi
		String uri;
		for(ControleurBouchonOutboundPort cbop : portsToMachine){
			if(cbop.availableCores() >= 4 ) {
				uri = r.addNewPort(repartiteurURIFixe);
				System.out.println("DEPLOY VM args : appid = " + r.getAppId() + " urifixe = " + uri + " uridcc = " + r.getRepartiteurURIDCC());
				cbop.deployVM(4, r.getAppId(), uri, r.getRepartiteurURIDCC());
				return true;
			}
		}
		return false;
	}
	
	/**
	 *
	 * @param r
	 * @throws Exception
	 */
	public void acceptRequest(Request r) throws Exception {
		System.out.println("recu par le controleur : requete no = " + r.getUri() + " app = " + r.getAppId());
		for(RepartiteurBouchon rr : rbs){
			if(r.getAppId() == rr.getAppId()){
				if(!rr.processRequest(r)) {
					String URInewPortRepartiteur = repartiteurURIgenericName + rr.getAppId();
					if(this.deployVM(rr, URInewPortRepartiteur)) rr.processRequest(r);
					else System.out.println("Requete rejetee : queues pleines et nombre de vm maximum atteint");
				}
				return;
			}
		}
		System.out.println("Requête rejetée : Pas de répartiteur dédié à cette application --- Numero app = " + r.getAppId());
	}
	
	public void acceptApplication(Integer appId) throws Exception {
		rbs.add(new RepartiteurBouchon(repartiteurURIgenericName + appId, appId));
		System.out.println("Application nouvellement acceptee : " + appId);
	}
	
	public ArrayList<ControleurBouchonOutboundPort> getPortsToMachine() {
		return portsToMachine;
	}
}

	
