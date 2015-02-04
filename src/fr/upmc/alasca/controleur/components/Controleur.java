package fr.upmc.alasca.controleur.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import fr.upmc.alasca.computer.interfaces.VMProviderI;
import fr.upmc.alasca.controleur.connectors.RepartiteurControleurConnector;
import fr.upmc.alasca.controleur.interfaces.AppRequestI;
import fr.upmc.alasca.controleur.ports.CAToControleurInboundPort;
import fr.upmc.alasca.controleur.ports.ControleurInboundPort;
import fr.upmc.alasca.controleur.ports.ControleurOutboundPort;
import fr.upmc.alasca.controleurAuto.components.ControleurAutonomique;
import fr.upmc.alasca.controleurAuto.connectors.CAConnector;
import fr.upmc.alasca.repartiteur.components.Repartiteur;
import fr.upmc.alasca.repartiteur.connectors.RepartiteurConnector;
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

	// Préfixe de l'URI pour tous les CA
	protected String CAURIgenericName = "controleurauto";
	
	protected Map<Integer, ArrayList<ControleurOutboundPort>> mapVM = new HashMap<Integer, ArrayList<ControleurOutboundPort>>();

	// Port par lequel sont reçues les requêtes du générateur de requêtes
	protected ControleurInboundPort port_i;
	
	// File d'attente de requêtes (asynchronisme à implementer plus tard)
	protected BlockingQueue<Request> queue;

	// Ports : ca -> controleur
	protected HashMap<Integer,CAToControleurInboundPort> rbs = 
			new HashMap<Integer,CAToControleurInboundPort>();

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
		if(AbstractCVM.isDistributed)
			dcco.doConnection("request_generator_jvm_uri"
					+ AbstractCVM.DYNAMIC_COMPONENT_CREATOR_INBOUNDPORT_URI,
					DynamicComponentCreationConnector.class.getCanonicalName());
		else
			dcco.doConnection(""
					+ AbstractCVM.DYNAMIC_COMPONENT_CREATOR_INBOUNDPORT_URI,
					DynamicComponentCreationConnector.class.getCanonicalName());

		// Création du répartiteur
		String newNameRepartiteurURI = repartiteurURIgenericName + appId;
		dcco.createComponent(Repartiteur.class.getCanonicalName(),
				new Object[] { newNameRepartiteurURI, appId, thresholds });
		
		//Cr�ation du CA
		String newNameCAURI = CAURIgenericName + appId;
		dcco.createComponent(ControleurAutonomique.class.getCanonicalName(),
				new Object[] { newNameCAURI, appId, thresholds });
		
		/* Connexion entre le répartiteur et le contrôleur (necessaire pour le moment car
		 * 1er deployVM)
		 */
		/*ControleurFromRepartiteurInboundPort pcz = 
				new ControleurFromRepartiteurInboundPort(
						"controlTOrep" + appId, this);
		//rbs.put(appId, pcr);
		this.addPort(pcz);
		if (AbstractCVM.isDistributed) {
			pcz.publishPort() ;
		} else {
			pcz.localPublishPort() ;
		}*/
				
		/*DynamicallyConnectableComponentOutboundPort pd = 
				new DynamicallyConnectableComponentOutboundPort(this);
		this.addPort(pd);
		pd.localPublishPort();
		
		pd.doConnection(newNameRepartiteurURI + "-dcc",
				DynamicallyConnectableComponentConnector.class
						.getCanonicalName());
		pd.connectWith(pcz.getPortURI(), "repartiteurTOcontroleur" + appId, 
				RepartiteurControleurConnector.class.getCanonicalName());
		pd.doDisconnection();*/
		
		
		
		// Connexion entre le CA et le contrôleur
		CAToControleurInboundPort pcr = 
				new CAToControleurInboundPort(
						"cAToControleurInboundPort-" + appId, this); //controlTOrep
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
		
		pr.doConnection(newNameCAURI + "-dcc",
				DynamicallyConnectableComponentConnector.class
						.getCanonicalName());
		pr.connectWith(pcr.getPortURI(), "cAToControleurOutboundPort-" + appId, 
				RepartiteurControleurConnector.class.getCanonicalName()); //TODO change name connector
		pr.doDisconnection();
		
		// Lien entre le générateur de requêtes et le répartiteur
		DynamicallyConnectableComponentOutboundPort p =
				new DynamicallyConnectableComponentOutboundPort(this);
		this.addPort(p);
		p.publishPort();
		
		p.doConnection(uri_new_rg + "-dcc",
				DynamicallyConnectableComponentConnector.class
						.getCanonicalName());
		
		p.connectWith(newNameRepartiteurURI, uri_new_rg,
				ClientArrivalConnector.class.getCanonicalName());
		p.doDisconnection();
		
		// port : ca -> repartiteur (addNewports necessaire)
		DynamicallyConnectableComponentOutboundPort p1 =
				new DynamicallyConnectableComponentOutboundPort(this);
		this.addPort(p1);
		p1.publishPort();
		
		p1.doConnection(newNameCAURI + "-dcc",
				DynamicallyConnectableComponentConnector.class
						.getCanonicalName());
		
		p1.connectWith("cAToRepartiteurInboundPort-" + appId, "cAToRepartiteurOutboundPort-" + appId,
				RepartiteurConnector.class.getCanonicalName());
		p1.doDisconnection();
		
		// port : repartiteur -> ca (deployFirstVM)
		DynamicallyConnectableComponentOutboundPort p2 =
						new DynamicallyConnectableComponentOutboundPort(this);
		this.addPort(p2);
		p2.publishPort();
				
		p2.doConnection(newNameRepartiteurURI + "-dcc",
						DynamicallyConnectableComponentConnector.class
								.getCanonicalName());
				
		p2.connectWith("repartiteurToCAInboundPort-" + appId, "repartiteurToCAOutboundPort-" + appId,
						CAConnector.class.getCanonicalName());
		p2.doDisconnection();
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
	
	public void incFrequency(int appid) throws Exception {
		System.out.println("incFrequency dans Controleur");
		List<ControleurOutboundPort> l = this.mapVM.get(appid);
		for(int i = 0 ; i < l.size(); ++i){
			if(!l.get(i).isMaxed(appid))
				l.get(i).incFrequency(appid);
		}
	}
}
