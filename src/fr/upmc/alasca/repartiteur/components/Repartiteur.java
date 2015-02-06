package fr.upmc.alasca.repartiteur.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import fr.upmc.alasca.computer.enums.Status;
import fr.upmc.alasca.computer.interfaces.VMProviderI;
import fr.upmc.alasca.computer.objects.VMCarac;
import fr.upmc.alasca.controleur.exceptions.BadDeploymentException;
import fr.upmc.alasca.controleurAuto.interfaces.CAProviderI;
import fr.upmc.alasca.repartiteur.interfaces.RepartiteurConsumerI;
import fr.upmc.alasca.repartiteur.interfaces.RepartiteurProviderI;
import fr.upmc.alasca.repartiteur.ports.CAToRepartiteurInboundPort;
import fr.upmc.alasca.repartiteur.ports.RepartiteurInboundPort;
import fr.upmc.alasca.repartiteur.ports.RepartiteurToCAOutboundPort;
import fr.upmc.alasca.repartiteur.ports.RepartiteurToVMOutboundPort;
import fr.upmc.alasca.requestgen.interfaces.RequestArrivalI;
import fr.upmc.alasca.requestgen.objects.Request;
import fr.upmc.components.AbstractComponent;
import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.components.cvm.pre.dcc.DynamicComponentCreationI;
import fr.upmc.components.cvm.pre.dcc.DynamicallyConnectableComponentI;
import fr.upmc.components.cvm.pre.dcc.DynamicallyConnectableComponentInboundPort;
import fr.upmc.components.cvm.pre.dcc.DynamicallyConnectableI;
import fr.upmc.components.ports.PortI;

/**
 * Le repartiteur de requete est charge de transmettre les requetes d'une unique
 * application aux VM auxquelles il est connecte. Il doit être vu comme un
 * sous-composant du Controleur.
 *
 */
public class Repartiteur extends AbstractComponent implements
	DynamicallyConnectableComponentI,DynamicallyConnectableI {

	// utilise pour la connexion dynamique aux VM
	protected DynamicallyConnectableComponentInboundPort dccInboundPort;

	// l'id de application associe a ce repartiteur
	protected Integer appId;

	// Paramètres de seuillages
	protected String thresholds;
	
	// compteur utilise pour differencier les ports du repartiteur vers chaque
	// VM
	protected Integer compteurPort = 0;
	
	protected Integer cptModulo = 0;

	// uri du port permettant la connexion dynamique
	protected String repartiteurURIDCC;

	protected String repartiteurURIBase;
    
    protected List<RepartiteurToVMOutboundPort> listPortToVm = new ArrayList<RepartiteurToVMOutboundPort>();

    // Liste des requetes
    protected ArrayList<Request> listR;
	protected RepartiteurInboundPort rgToRepartiteurInboundPort;

	
	protected RepartiteurToCAOutboundPort repartiteurToCAOutboundPort;
	protected CAToRepartiteurInboundPort cAToRepartiteurInboundPort;

    // Liste des caractéristiques des VM
    protected HashMap<String,VMCarac> listCarac;

	// Port courant de la VM traitant la derniere requete recues
	protected Iterator<Map.Entry<RepartiteurToVMOutboundPort, Status>> robpIt;


	
	/**
	 * 
	 * Constructeur du repartiteur
	 * 
	 * 
	 * @param portURI Uri de base du repartiteur
	 * @param appId Id de l'application liée au repartiteur
	 * @param threshokds Seuils pour la gestion de deploiement/modification des VM
	 * @throws Exception
	 */
    public Repartiteur(String portURI, Integer appId, String thresholds)
    		throws Exception {
        this.addRequiredInterface(RequestArrivalI.class);

       // this.addRequiredInterface(RepartiteurProviderI.class); // TODO attention
        this.addRequiredInterface(RepartiteurConsumerI.class);

		this.appId = appId;
		this.thresholds = thresholds;
		this.repartiteurURIDCC = portURI + "-dcc";
		this.repartiteurURIBase = portURI;
        this.listR = new ArrayList<Request>();
        this.listCarac = new HashMap<String,VMCarac>();
		
        PortI p = this.rgToRepartiteurInboundPort;
		
		this.addOfferedInterface(RequestArrivalI.class);
		p = new RepartiteurInboundPort(portURI, this);

		this.addRequiredInterface(VMProviderI.class);

		this.addOfferedInterface(DynamicallyConnectableComponentI.class);
		this.dccInboundPort = new DynamicallyConnectableComponentInboundPort(
				repartiteurURIDCC, this);
		if (AbstractCVM.isDistributed) {
			p.publishPort();
			this.dccInboundPort.publishPort();
		} else {
			p.localPublishPort();
			this.dccInboundPort.localPublishPort();
		}
		this.addPort(dccInboundPort);

		this.addRequiredInterface(DynamicComponentCreationI.class);
		this.addRequiredInterface(DynamicallyConnectableComponentI.class);

		// TODO a enlever
		/*control = new RepartiteurToControleurOutboundPort("repartiteurTOcontroleur"+this.getAppId(),
				this);
		this.addPort(control);

        if (AbstractCVM.isDistributed) {
            control.publishPort();
        } else {
            control.localPublishPort();
        }*/
        //FIN enlever
        
		this.addRequiredInterface(CAProviderI.class);
		repartiteurToCAOutboundPort = new RepartiteurToCAOutboundPort("repartiteurToCAOutboundPort-"+this.getAppId(),
				this);
		this.addPort(repartiteurToCAOutboundPort);
		if (AbstractCVM.isDistributed) {
        	repartiteurToCAOutboundPort.publishPort();
        } else {
        	repartiteurToCAOutboundPort.localPublishPort();
        }

        this.addOfferedInterface(RepartiteurProviderI.class);
		cAToRepartiteurInboundPort = new CAToRepartiteurInboundPort("cAToRepartiteurInboundPort-"+this.getAppId(),
				this);
		this.addPort(cAToRepartiteurInboundPort);
		if (AbstractCVM.isDistributed) {
			cAToRepartiteurInboundPort.publishPort();
        } else {
        	cAToRepartiteurInboundPort.localPublishPort();
        }
	}

	/**
	 * Ajoute un nouveau port vers une machine virtuelle
	 *
	 * @param portURI
	 *            base l'uri du port cree : repartiteur<numeroAppId>
	 * @return uri actuellement utilisee pour le port cree
	 * @throws Exception
	 */
    public String[] addNewPorts(String portURI) throws Exception {

        String[] uritab = new String[3];

        RepartiteurToVMOutboundPort rbp;
		String URIRepartiteurOutboundPort = repartiteurURIBase + (compteurPort++) + "-RepartiteurOutboundPort";
        uritab[0] = URIRepartiteurOutboundPort;

		rbp = new RepartiteurToVMOutboundPort(URIRepartiteurOutboundPort,
				this);
		this.addPort(rbp);

        if (AbstractCVM.isDistributed) {
            rbp.publishPort();
        } else {
            rbp.localPublishPort();
        }
        this.listPortToVm.add(rbp);
        
        uritab[1] = this.getRepartiteurURIDCC(); //nécessaire pour le controleur auto
        return uritab;
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

	/**
	 * @return l'id de application associee a ce repartiteur
	 */
	public int getAppId() {
		return appId;
	}

	/**
	 *
	 *
	 * @return uri du port permettant la connexion dynamique
	 */
	public String getRepartiteurURIDCC() {
		return repartiteurURIDCC;
	}

	public String RepartiteurURIBase() {
		return repartiteurURIBase;
	}
    
    /**
     * Methode de reception d'une requete provenant du generateur de requete
     * 
     * 
     * @param r
     * @throws Exception
     */
	public void acceptRequest(Request r) throws Exception {

		try {
        	processRequest(r);
        } catch (Exception e) {
        	//String URInewPortRepartiteur = "repartiteur" + this.getAppId();
            try {
            	System.out.println("ERREUR -> CREATION VM : " + e.getMessage() );
            	this.repartiteurToCAOutboundPort.deployFirstVM();
			} catch (BadDeploymentException e2) {
				System.out.println("Rejected request : all queues full and "
						+ "maximal number of mv reached");
			} catch (Exception e2) {
				/*System.out.println("Echec de processRequest ! requête : "
						+ r.toString());*/
				e2.printStackTrace();
			}
        }
        //throw new NoRepartitorException("Rejected request: no dispatcher "
        //		+ "dedicated to the application number: " + r.getAppId());

	}

	/**
	 * Methode de stockage des requetes
	 * 
	 * Si le repatiteur ne possède pas de VM => création d'une vm apres stockage
	 * Sinon stockage de la requete et demande de notification des VM  
	 * 
	 *
	 * @param r requete a stoquer
	 * 
	 * @throws Exception
     */
    public void processRequest(Request r) throws Exception {
    	if (!this.listPortToVm.isEmpty()) {
    		for (RepartiteurToVMOutboundPort e: listPortToVm) {
    			e.startNotification();
    		}
    	}
    	int VMNumber = (cptModulo++) % this.listPortToVm.size();
    	this.listPortToVm.get(VMNumber).processRequest(r);
    	String vm = this.listPortToVm.get(VMNumber).toString();
    	
    	System.out.println("-------------------------------------------------------");
    	System.out.println("Sending Request " + r + " from Repartitor " + 
    			this.getAppId() + " to VM " + VMNumber + " (" + vm + ")");
    	System.out.println("-------------------------------------------------------");
    }
    
    /**
     * Methode de destruction d'une VM
     * 
     * 
     * @param vm
     * @throws Exception
     */
    /*private void destroyVM(String vm) throws Exception {
    	RepartiteurToVMOutboundPort p = 
    			(RepartiteurToVMOutboundPort) this.findPortFromURI(vm);
    	String uriComputerParent = p.getUriComputerParent();
    	this.control.destroyVM(uriComputerParent, vm);
    }*/

}
