package fr.upmc.alasca.controleur.components;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;

import fr.upmc.alasca.computer.interfaces.VMProviderI;
import fr.upmc.alasca.controleur.interfaces.AppRequestI;
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
 * Le Controleur est connecte au RequestGenerator et aux Computers.
 * Il reçoit les requetes du generateur de requetes.
 * Il fait des demandes de deploiement de VirtualMachine aupres des Computer
 * si necessaire: c'est-a-dire en ce moment si les queues de toutes
 * les VM dediees a une application sont  pleines.
 *
 */
public class Controleur extends AbstractComponent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	// ports par lesquels sont faites les demandes de deploiement de vm aux
	// Computers
	protected ArrayList<ControleurOutboundPort> portsToMachine = new ArrayList<ControleurOutboundPort>();

	
	// prefixe d'uri pour tous les repartiteurs
	protected String repartiteurURIgenericName = "repartiteur";

	// port par lequel sont recues les requetes du generateur de requetes
	protected ControleurInboundPort port_i;
	
	// File d'attente de requetes (asynchronisme a implement plus tard)
	protected BlockingQueue<Request> queue;

	// les repartiteurs de requetes du controleur
	protected HashMap<Integer,Repartiteur> rbs = new HashMap<Integer,Repartiteur>();

	public Controleur(String controleur_uri_outboundport,
			String controleur_uri_inboundport, Integer nb_computers)
			throws Exception {
		super(true, false);
		this.addRequiredInterface(VMProviderI.class);

		for (int i = 0; i < nb_computers; ++i) {
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
	 * Deploie une vm sur un Computer s'il y a de la place
	 *
	 * @param r
	 *            repartiteur connecte a la vm deployee
	 * @param repartiteurURIFixe
	 *            uri du port sur lequel le repartiteur transmet les requetes
	 * @return true si une vm a effectivement ete deployee
	 * @throws Exception
	 */
	public boolean deployVM(Repartiteur r, String repartiteurURIFixe)
			throws Exception {
		// TODO Modifier politique de deploiement
		String uri;
		for (ControleurOutboundPort cbop : portsToMachine) {
			if (cbop.availableCores() >= 2) {
				uri = r.addNewPort();
				System.out.println("deployvm passed parameters : appid = " + r.getAppId()
						+ " urifixe = " + uri + " uridcc = "
						+ r.getRepartiteurURIDCC());
				if (cbop.availableCores() >= 4)
					cbop.deployVM(4, r.getAppId(), uri,
							r.getRepartiteurURIDCC());
				else
					cbop.deployVM(2, r.getAppId(), uri,
							r.getRepartiteurURIDCC());
				return true;
			}
		}
		return false;
	}

	/**
	 * Accepte une requete du generateur de requete
	 *
	 * @param r			Requete reçue par le Controleur
	 * @throws Exception
	 */
	/*
	public void acceptRequest(Request r) throws Exception {
		if(rbs.containsKey(r.getAppId())){

            Repartiteur rr = rbs.get(r.getAppId());

            if (!rr.processRequest(r)) {
                String URInewPortRepartiteur = repartiteurURIgenericName
							+ rr.getAppId();
                if (this.deployVM(rr, URInewPortRepartiteur))
						rr.processRequest(r);
                else
						System.out
								.println("Rejected request: all queues full and maximal number of mv reached");
            }
            return;

		}else{
		System.out
				.println("Rejected request: no dispatcher dedicated to the application number: "
						+ r.getAppId());
		}
	}*/

	/**
	 * Cree un repartiteur de requete dedie a l'application dont l'id est passe
	 * en parametre
	 *
	 * @param appId
	 *            Id de l'application
	 * @throws Exception
	 */
	public void acceptApplication(Integer appId, String uri_new_rg) throws Exception {
		DynamicComponentCreationOutboundPort dcco = new DynamicComponentCreationOutboundPort(
				this);
		dcco.publishPort();

		this.addPort(dcco);
		dcco.doConnection("request_generator_jvm_uri" //"controleur_jvm_uri" // TODO a changer
				+ AbstractCVM.DYNAMIC_COMPONENT_CREATOR_INBOUNDPORT_URI,
				DynamicComponentCreationConnector.class.getCanonicalName());

		String newNameURI = repartiteurURIgenericName + appId;
		dcco.createComponent(Repartiteur.class.getCanonicalName(),
				new Object[] { newNameURI,
			appId});
		
		
		/*rbs.put(appId,new Repartiteur(repartiteurURIgenericName + appId, appId));
		Repartiteur r = rbs.get(appId);
		String uriNewPort = r.addNewPort();*/
		

		
		DynamicallyConnectableComponentOutboundPort p = new DynamicallyConnectableComponentOutboundPort(
				this);
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
	 * @return ports par lesquels sont faites les communications entre le
	 *         controleur et les Computers (deploiement d'une machine virtuelle
	 *         par exemple)
	 */
	public ArrayList<ControleurOutboundPort> getPortsToMachine() {
		return portsToMachine;
	}
}
