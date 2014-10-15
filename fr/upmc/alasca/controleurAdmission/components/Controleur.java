package fr.upmc.alasca.controleurAdmission.components;

import java.util.ArrayList;

import fr.upmc.alasca.computer.objects.Computer;
import fr.upmc.alasca.controleurAdmission.interfaces.URISortieControleurI;
import fr.upmc.alasca.controleurAdmission.ports.URIControleurInboundPort;
import fr.upmc.alasca.controleurAdmission.ports.URIControleurOutboundPort;
import fr.upmc.alasca.dispatcher.Dispatcher;
import fr.upmc.alasca.requestgen.objects.Request;
import fr.upmc.components.AbstractComponent;
import fr.upmc.components.ports.PortI;

public class Controleur extends AbstractComponent{
	Dispatcher dispatcher;
	ArrayList<Computer> listeMachineLibre = new ArrayList<Computer>();
	String uriPrefix;
	String controleurPortInboundURI;
	String controleurPortOutboundURI;
	
	public Controleur(String uriPrefix, String controleurPortInboundURI, String controleurPortOutboundURI, String fichierConfig) throws Exception{
		super(true, false);
		this.uriPrefix = uriPrefix;
		this.controleurPortInboundURI = controleurPortInboundURI;
		this.controleurPortOutboundURI = controleurPortOutboundURI;
		remplirListeOrdinateur(fichierConfig);
		this.addOfferedInterface(URISortieControleurI.class);
		this.addRequiredInterface(URISortieControleurI.class);
		PortI p1 = new URIControleurInboundPort(controleurPortInboundURI, this);
		PortI p2 = new URIControleurOutboundPort(controleurPortOutboundURI, this);
		this.addPort(p1);
		this.addPort(p2);
		p1.publishPort();
		p2.publishPort();
	}
	
	private void remplirListeOrdinateur(String config){
		ArrayList<String> uriOrdinateurs = new ArrayList<String>();
	}
	
	public void transfertRequeteDispatcher(Request r){
		/* requeteExistante renvoi vrai si le dispatcheur a déja reçu cette requete
		 * faux sinon
		 */
		if(dispatcher.requeteExistante()){
			dispatcher.ajouterRequete(r);
		} else {
			dispatcher.nouvelleRequete(r);
		}
	}
	
	public void allouerComputer(){
		
	}
	
	public String provideURIService() {
		return uriPrefix;
	}
}

	
