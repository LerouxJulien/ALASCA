package fr.upmc.alasca.controleur.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;

import fr.upmc.alasca.computer.interfaces.VMProviderI;
import fr.upmc.alasca.controleur.connectors.RepartiteurControleurConnector;
import fr.upmc.alasca.controleur.interfaces.AppRequestI;
import fr.upmc.alasca.controleur.ports.ControleurFromRepartiteurInboundPort;
import fr.upmc.alasca.controleur.ports.ControleurInboundPort;
import fr.upmc.alasca.controleur.ports.ControleurOutboundPort;
import fr.upmc.alasca.repartiteur.components.Repartiteur;
import fr.upmc.alasca.requestgen.main.ClientArrivalConnector;
import fr.upmc.alasca.requestgen.objects.Request;
import fr.upmc.components.AbstractComponent;
import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.components.cvm.pre.dcc.DynamicComponentCreationConnector;
import fr.upmc.components.cvm.pre.dcc.DynamicComponentCreationI;
import fr.upmc.components.cvm.pre.dcc.DynamicComponentCreationOutboundPort;
import fr.upmc.components.cvm.pre.dcc.DynamicallyConnectableComponentConnector;
import fr.upmc.components.cvm.pre.dcc.DynamicallyConnectableComponentI;
import fr.upmc.components.cvm.pre.dcc.DynamicallyConnectableComponentOutboundPort;

/**
 * Classe <code>Controleur</code>
 * 
 * <p>
 * <strong>Description</strong>
 * </p>
 * 
 * Le Controleur est connecté au RequestGenerator et aux Computers.
 * Il reçoit les requêtes du générateur de requêtes.
 * Il fait des demandes de déploiement de VirtualMachine auprès des Computer
 * si necessaire.
 * 
 * <p>
 * Created on : 23 dec. 2014
 * </p>
 * 
 * @author <a href="mailto:William.Chasson@etu.upmc.fr">William Chasson/a>
 *         <a href="mailto:Henri.Ng@etu.upmc.fr">Henri Ng/a>
 * @version $Name$ -- $Revision$ -- $Date$
 */
public class Controleur extends AbstractComponent {

	// Ports par lesquels sont faites les demandes de deploiement de VM aux
	// Computers
	protected ArrayList<ControleurOutboundPort> portsToMachine = 
			new ArrayList<ControleurOutboundPort>();
	
	// Préfixe de l'URI pour tous les répartiteurs
	protected String repartiteurURIgenericName = "repartiteur";

	// Port par lequel sont reçues les requêtes du générateur de requêtes
	protected ControleurInboundPort port_i;
	
	// File d'attente de requêtes (asynchronisme à implementer plus tard)
	protected BlockingQueue<Request> queue;

	// Répartiteurs de requêtes du contrôleur
	protected HashMap<Integer,ControleurFromRepartiteurInboundPort> rbs = 
			new HashMap<Integer,ControleurFromRepartiteurInboundPort>();

	/**
	 * Constructeur du contrôleur
	 * 
	 * @param controleur_uri_outboundport
	 * @param controleur_uri_inboundport
	 * @param nb_computers
	 * @throws Exception
	 */
	public Controleur(String controleur_uri_outboundport,
			String controleur_uri_inboundport, Integer nb_computers)
			throws Exception {
		super(true, false);
		
		this.addRequiredInterface(VMProviderI.class);

		// Connexion du contrôleur à toutes les machines
		for (int i = 1; i <= nb_computers; ++i) {
			ControleurOutboundPort p = new ControleurOutboundPort(
					controleur_uri_outboundport + i, this);
			this.addPort(p);
			p.publishPort();
			portsToMachine.add(p);
		}
		
		this.addOfferedInterface(AppRequestI.class);
		port_i = new ControleurInboundPort(controleur_uri_inboundport, this);
		this.addPort(port_i);
		port_i.publishPort();
		
		this.addRequiredInterface(DynamicComponentCreationI.class);
		this.addRequiredInterface(DynamicallyConnectableComponentI.class);
	}
	
	/**
	 * Crée un répartiteur de requêtes dédié à l'application dont l'ID est
	 * passée en paramètre
	 *
	 * @param appId ID de l'application
	 * @throws Exception
	 */
	public void acceptApplication(Integer appId, String thresholds,
			String uri_new_rg) throws Exception {
		DynamicComponentCreationOutboundPort dcco = new DynamicComponentCreationOutboundPort(
				this);
		dcco.publishPort();

		this.addPort(dcco);
		// TODO A changer
//		dcco.doConnection("controleur_jvm_uri"
//				+ AbstractCVM.DYNAMIC_COMPONENT_CREATOR_INBOUNDPORT_URI,
//				DynamicComponentCreationConnector.class.getCanonicalName());
		dcco.doConnection("request_generator_jvm_uri"
				+ AbstractCVM.DYNAMIC_COMPONENT_CREATOR_INBOUNDPORT_URI,
				DynamicComponentCreationConnector.class.getCanonicalName());

		// Création du répartiteur
		String newNameURI = repartiteurURIgenericName + appId;
		dcco.createComponent(Repartiteur.class.getCanonicalName(),
				new Object[] { newNameURI, appId, thresholds });
		
		// Connexion entre le répartiteur et le contrôleur
		ControleurFromRepartiteurInboundPort pcr = 
				new ControleurFromRepartiteurInboundPort(
						"controlTOrep" + appId, this);
		rbs.put(appId, pcr);
		this.addPort(pcr);
		if (AbstractCVM.isDistributed) {
			pcr.publishPort() ;
		} else {
			pcr.localPublishPort() ;
		}
				
		DynamicallyConnectableComponentOutboundPort pr = 
				new DynamicallyConnectableComponentOutboundPort(this);
		this.addPort(pr);
		pr.localPublishPort();
		
		pr.doConnection(newNameURI + "-dcc",
				DynamicallyConnectableComponentConnector.class
						.getCanonicalName());
		pr.connectWith(pcr.getPortURI(), "repartiteurTOcontroleur" + appId, 
				RepartiteurControleurConnector.class.getCanonicalName());
		pr.doDisconnection();
		
		// Lien entre le générateur de requêtes et le répartiteur
		DynamicallyConnectableComponentOutboundPort p =
				new DynamicallyConnectableComponentOutboundPort(this);
		this.addPort(p);
		p.publishPort();
		
		p.doConnection(uri_new_rg + "-dcc",
				DynamicallyConnectableComponentConnector.class
						.getCanonicalName());
		
		p.connectWith(newNameURI, uri_new_rg,
				ClientArrivalConnector.class.getCanonicalName());
		p.doDisconnection();
	}

	/**
	 * Déploie une VM sur un Computer s'il y a de la place
	 *
	 * @param r Répartiteur connecté à la VM deployée
	 * @param repartiteurURIFixe URI du port sur lequel le répartiteur transmet 
	 * 							 les requêtes
	 * @return true Si une vm a effectivement été deployée
	 * @throws Exception
	 */
	public void deployVM(int appid, String[] uri,String RepartiteurURIDCC)
			throws Exception {
		// TODO Modifier la politique de déploiement
		for (ControleurOutboundPort cbop : portsToMachine) {
			if (cbop.availableCores() >= 2) {
				System.out.println("deployvm passed parameters : appid = " 
						+ appid + " urifixe = " + uri[0] + " and " + uri[1] 
						+ " uridcc = " + RepartiteurURIDCC);
				if (cbop.availableCores() >= 4)
					cbop.deployVM(4, appid, uri, RepartiteurURIDCC);
				else
					cbop.deployVM(2, appid, uri, RepartiteurURIDCC);
				break;
			}
		}
	}
	
	/**
	 * Détruite une VM à partir de son URI et de celui de son parent
	 * 
	 * @param uriComputerParent
	 * @param mv
	 * @throws Exception
	 */
	public void destroyVM(String uriComputerParent, String mv)
			throws Exception {
		ControleurOutboundPort p = (ControleurOutboundPort) 
				this.findPortFromURI(uriComputerParent);
		p.destroyVM(mv);
	}

	/**
	 * Retourne les orts par lesquels sont faites les communications entre le
	 * contrôleur et les computers (déploiement d'une machine virtuelle par
	 * exemple)
	 * 
	 * @return portsToMachine
	 */
	public ArrayList<ControleurOutboundPort> getPortsToMachine() {
		return portsToMachine;
	}
	
}
