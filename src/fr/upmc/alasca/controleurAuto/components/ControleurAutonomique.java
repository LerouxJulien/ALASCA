package fr.upmc.alasca.controleurAuto.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import fr.upmc.alasca.computer.objects.VMCarac;
import fr.upmc.alasca.computer.objects.VMMessages;
import fr.upmc.alasca.controleur.components.RingTask;
import fr.upmc.alasca.controleur.interfaces.ControleurFromRepartiteurProviderI;
import fr.upmc.alasca.controleur.interfaces.RingComponent;
import fr.upmc.alasca.controleur.ports.RingComponentOutboundPort;
import fr.upmc.alasca.controleurAuto.interfaces.ControleurAutoProviderI;
import fr.upmc.alasca.controleurAuto.ports.CAToControleurOutboundPort;
import fr.upmc.alasca.controleurAuto.ports.CAToRepartiteurOutboundPort;
import fr.upmc.alasca.controleurAuto.ports.RepartiteurToCAInboundPort;
import fr.upmc.alasca.controleurAuto.ports.VMToCAInboundPort;
import fr.upmc.alasca.repartiteur.interfaces.RepartiteurProviderI;
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
public class ControleurAutonomique extends AbstractComponent implements
	DynamicallyConnectableComponentI,DynamicallyConnectableI, RingComponent {

	// utilise pour la connexion dynamique aux VM
	protected DynamicallyConnectableComponentInboundPort dccInboundPort;

	// l'id de application associe a ce repartiteur
	protected Integer appId;

	// Paramètres de seuillages
	protected String thresholds;
	
	// compteur utilise pour differencier les ports du repartiteur vers chaque
	// VM
	protected Integer compteurPort = 0;

	// uri du port permettant la connexion dynamique
	protected String controleurAutoURIDCC;

	protected String controleurAutoURIBase_outbound;
	
	//deployVM, destroyVM ...
	protected CAToControleurOutboundPort cAToControleurOutboundPort;
	
	/* apres traitement de la requete, le repartiteur envoie au ca
	 * les infos concernant le temps d'execution de la requete
	*/
	protected RepartiteurToCAInboundPort repartiteurToCAInboundPort;

	//creation des ports entre repartiteur et nouvelles vm
	protected CAToRepartiteurOutboundPort cAToRepartiteurOutboundPort;
	
	protected List<VMToCAInboundPort> listVMToCAInboundPort = new ArrayList<VMToCAInboundPort>();

    // Liste des requetes
    protected ArrayList<Request> listR;

    // Liste des caractéristiques des VM
    protected HashMap<String,VMCarac> listCarac;
	
	// Paramètres de contrôles pour les modifications de performances
	protected double meanTimeProcess;
	protected double thFreqMin;
	protected double thFreqMax;
	protected double thCoreMin;
	protected double thCoreMax;
	protected double thVMMin;
	protected double thVMMax;

	protected RingComponentOutboundPort outboundPortNextControleurRing = null;
	protected String uriCARing = null;
	protected String outboundURINextControleurRing = null;
	
	/**
	 * Constructeur du repartiteur
	 * 
	 * 
	 * @param portURI Uri de base du repartiteur
	 * @param appId Id de l'application liée au repartiteur
	 * @param outboundURINextControleurRing 
	 * @param threshokds Seuils pour la gestion de deploiement/modification des VM
	 * @throws Exception
	 */
    public ControleurAutonomique(String portURI, Integer appId, String thresholds, 
    		String outboundURINextControleurRing) throws Exception {

		super(true, true);
		this.addOfferedInterface(ControleurAutoProviderI.class);

		//this.addRequiredInterface(ControleurAutoConsumerI.class);

		this.uriCARing = portURI + "ring";
		RingComponentOutboundPort p = new RingComponentOutboundPort(this.uriCARing, this);
		this.addPort(p);
		p.publishPort();
		this.outboundURINextControleurRing = outboundURINextControleurRing;
		this.outboundPortNextControleurRing = new RingComponentOutboundPort(this.outboundURINextControleurRing, this);
		this.addPort(this.outboundPortNextControleurRing);
		if (AbstractCVM.isDistributed) {
			this.outboundPortNextControleurRing.publishPort();
		} else {
			this.outboundPortNextControleurRing.localPublishPort();
		}
		
		this.appId = appId;
		this.thresholds = thresholds;
		this.controleurAutoURIDCC = portURI + "-dcc";
		this.controleurAutoURIBase_outbound = portURI + "-outboundPort";
        this.listR = new ArrayList<Request>();
        this.listCarac = new HashMap<String,VMCarac>();
		
		
		// Récupération des valeurs des seuils à partir de la chaîne de caractères
		String[] values = thresholds.split(";");		
		meanTimeProcess = Double.parseDouble(values[0]);
		thFreqMin = Double.parseDouble(values[1]);
		thFreqMax = Double.parseDouble(values[2]);
		thCoreMin = Double.parseDouble(values[3]);
		thCoreMax = Double.parseDouble(values[4]);
		thVMMin = Double.parseDouble(values[5]);
		thVMMax = Double.parseDouble(values[6]);


		this.addRequiredInterface(DynamicComponentCreationI.class);
		this.addRequiredInterface(DynamicallyConnectableComponentI.class);
		this.addOfferedInterface(DynamicallyConnectableComponentI.class);
		this.dccInboundPort = new DynamicallyConnectableComponentInboundPort(
				controleurAutoURIDCC, this);
		if (AbstractCVM.isDistributed) {
			this.dccInboundPort.publishPort();
		} else {
			this.dccInboundPort.localPublishPort();
		}
		this.addPort(dccInboundPort);

		this.addRequiredInterface(ControleurFromRepartiteurProviderI.class);
		this.cAToControleurOutboundPort = new CAToControleurOutboundPort("cAToControleurOutboundPort-"+this.getAppId(),
				this);
		this.addPort(cAToControleurOutboundPort);
        if (AbstractCVM.isDistributed) {
        	cAToControleurOutboundPort.publishPort();
        } else {
        	cAToControleurOutboundPort.localPublishPort();
        }
        
        this.addRequiredInterface(ControleurAutoProviderI.class);
        this.repartiteurToCAInboundPort = new RepartiteurToCAInboundPort("repartiteurToCAInboundPort-"+this.getAppId(),
				this);
		this.addPort(repartiteurToCAInboundPort);
        if (AbstractCVM.isDistributed) {
        	repartiteurToCAInboundPort.publishPort();
        } else {
        	repartiteurToCAInboundPort.localPublishPort();
        }

		this.addRequiredInterface(RepartiteurProviderI.class);
        this.cAToRepartiteurOutboundPort = new CAToRepartiteurOutboundPort("cAToRepartiteurOutboundPort-"+this.getAppId(),
				this);
		this.addPort(cAToRepartiteurOutboundPort);
        if (AbstractCVM.isDistributed) {
        	cAToRepartiteurOutboundPort.publishPort();
        } else {
        	cAToRepartiteurOutboundPort.localPublishPort();
        }
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
	 * Methode de notification et update du status des vm
	 * 
	 * @param m Notification de la VM au repartiteur de requetes
     * @throws Exception
     */
    public void notifyStatus(VMMessages m) throws Exception {
    	System.out.println("Repartitor " + this.getAppId() + " Received status "
    						+ m.getStatus() + " from VM " + m.getVmID()
    						+ " in time " + m.getTime());

		if (m.getTime() != 0) {
			VMCarac car = this.listCarac.get(m.getVmID());
			car.addTime(m.getTime());
			System.out.println("Temps moyen de traitement pour la VM " 
					+ m.getVmID() + " : " 
					+ this.listCarac.get(m.getVmID()).getMediumtime());
			
			// Création d'une nouvelle VM si dépassement du seuil thVMMax
			if (car.getMediumtime() > this.meanTimeProcess * (1 + this.thVMMax)) {
				System.out.println("******************************************************************************************************************");
				System.out.println("CREATION D'UNE NOUVELLE VM");
				String[] uri = this.addNewPorts("repartiteur"+this.getAppId());
				cAToControleurOutboundPort.deployVM(this.getAppId(), uri,uri[1]);
				System.out.println("******************************************************************************************************************");
			}
			/*
			// Augmentation du nombre de coeurs de la VM si dépassement du seuil thCoreMax
			else if (car.getMediumtime() > this.meanTimeProcess * (1 + this.thCoreMax)) {
				System.out.println("******************************************************************************************************************");
				System.out.println("AUGMENTER LE NOMBRE DE COEURS DE LA VM");
				// TODO
				System.out.println("******************************************************************************************************************");
			}
			
			// Augmentation de la fréquence des coeurs si dépassement du seuil thFreqMax
			else if (car.getMediumtime() > this.meanTimeProcess * (1 + this.thFreqMax)) {
				System.out.println("******************************************************************************************************************");
				System.out.println("AUGMENTER LA FREQUENCE DE LA VM");
				// TODO
				//control.incFrequency(this.getAppId());
				System.out.println("******************************************************************************************************************");
			}
			
			// Suppression d'une VM si dépassement du seuil thVMMin
			else if ((this.rbps.size() > 1) &&
					(car.getMediumtime() < this.meanTimeProcess * (1 + this.thVMMin))) {
				System.out.println("******************************************************************************************************************");
				System.out.println("SUPPRESSION D'UNE VM");
				// TODO
				RepartiteurToVMOutboundPort po = rbps.get(m.getRepPort());
				control.destroyVM(po.getUriComputerParent(), po.getVMInboundPortURI());
				System.out.println("******************************************************************************************************************");
			}
			
			// Diminution du nombre de coeurs de la VM si dépassement du seuil thCoreMin
			else if (car.getMediumtime() < this.meanTimeProcess * (1 + this.thCoreMin)) {
				System.out.println("******************************************************************************************************************");
				System.out.println("DIMINUER LE NOMBRE DE COEURS DE LA VM");
				// TODO
				System.out.println("******************************************************************************************************************");
			}
			
			// Diminution de la fréquence des coeurs si dépassement du seuil thFreqMin
			else if (car.getMediumtime() < this.meanTimeProcess * (1 + this.thFreqMin)) {
				System.out.println("******************************************************************************************************************");
				System.out.println("DIMINUER LA FREQUENCE DE LA VM");
				// TODO
				//control.incFrequency(this.getAppId());
				System.out.println("******************************************************************************************************************");
			}
			*/
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
    private String[] addNewPorts(String portURI) throws Exception {
    	String uriVMToCAInboundPort = "VMToCAInboundPort-" + this.appId + "-" + (compteurPort++);
    	VMToCAInboundPort tmp = new VMToCAInboundPort(uriVMToCAInboundPort, this);
    	this.addPort(tmp);
    	if (AbstractCVM.isDistributed) {
    		tmp.publishPort();
        } else {
        	tmp.localPublishPort();
        }
		String[] uris = this.cAToRepartiteurOutboundPort.addNewPorts(portURI);
		uris[2] = uriVMToCAInboundPort;
		return uris;
	}

	/**
     * Methode de reception d'un VMCarac
     * 
     * 
     * @param id
     * @param c
     */
    public void notifyCarac(String id, VMCarac c) {
    	System.out.println("Repartitor " + this.getAppId() + 
    			" Received features change from " + c.getVMid());
		if (!listCarac.containsKey(id))
			this.listCarac.put(id, c);
    }

	public void deployFirstVM() throws Exception {
		System.out.println("******************************************************************************************************************");
		System.out.println("CREATION D'UNE NOUVELLE VM");
		String[] uri = this.addNewPorts("repartiteur"+this.getAppId());
		cAToControleurOutboundPort.deployVM(this.getAppId(), uri,uri[1]);
		System.out.println("******************************************************************************************************************");
	}
	
	@Override
	public void sendTokenToNextComponent(ArrayList<String> freeVM) {
		System.out.println("[sendTokenToNextComponent] Controleur autonomique");
		new Thread(new RingTask(this.outboundPortNextControleurRing, freeVM)).start();
	}
	
}
