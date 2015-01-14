package fr.upmc.alasca.repartiteur.components;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import fr.upmc.alasca.computer.objects.VMCarac;
import fr.upmc.alasca.computer.objects.VMMessages;
import fr.upmc.alasca.computer.enums.Status;
import fr.upmc.alasca.computer.exceptions.BadDestroyException;
import fr.upmc.alasca.computer.exceptions.NotEnoughCapacityVMException;
import fr.upmc.alasca.repartiteur.connectors.RepartiteurConnector;
import fr.upmc.alasca.repartiteur.interfaces.RepartiteurConsumerI;
import fr.upmc.alasca.repartiteur.interfaces.RepartiteurProviderI;
import fr.upmc.alasca.computer.interfaces.VMProviderI;
import fr.upmc.alasca.computer.objects.VMMessages;
import fr.upmc.alasca.controleur.components.Controleur;
import fr.upmc.alasca.controleur.exceptions.BadDeploymentException;
import fr.upmc.alasca.repartiteur.ports.RepartiteurInboundPort;
import fr.upmc.alasca.repartiteur.ports.RepartiteurToControleurOutboundPort;
import fr.upmc.alasca.repartiteur.ports.RepartiteurToVMInboundPort;
import fr.upmc.alasca.repartiteur.ports.RepartiteurToVMOutboundPort;
import fr.upmc.alasca.requestgen.interfaces.RequestArrivalI;
import fr.upmc.alasca.requestgen.objects.Request;
import fr.upmc.alasca.requestgen.ports.RequestGeneratorOutboundPort;
import fr.upmc.components.AbstractComponent;
import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.components.cvm.pre.dcc.DynamicComponentCreationI;
import fr.upmc.components.cvm.pre.dcc.DynamicallyConnectableComponentConnector;
import fr.upmc.components.cvm.pre.dcc.DynamicallyConnectableComponentI;
import fr.upmc.components.cvm.pre.dcc.DynamicallyConnectableComponentInboundPort;
import fr.upmc.components.cvm.pre.dcc.DynamicallyConnectableComponentOutboundPort;
import fr.upmc.components.cvm.pre.dcc.DynamicallyConnectableI;
import fr.upmc.components.ports.PortI;


/**
 * Le repartiteur de requete est charge de transmettre les requetes d'une unique
 * application aux VM auxquelles il est connecte. Il doit être vu comme un
 * sous-composant du Controleur, d'ou les appels directs permis du Controleur
 * aux methodes du repartiteur sans passer par des ports (processRequest et
 * addNewPort par exemple).
 *
 */
public class Repartiteur extends AbstractComponent implements
	DynamicallyConnectableComponentI,DynamicallyConnectableI {

	// utilise pour la connexion dynamique aux VM
	protected DynamicallyConnectableComponentInboundPort dccInboundPort;

	// l'id de application associe a ce repartiteur
	protected Integer appId;

	// compteur utilise pour differencier les ports du repartiteur vers chaque
	// VM
	protected Integer compteurPort = 0;

	// uri du port permettant la connexion dynamique
	protected String repartiteurURIDCC;

	protected String repartiteurURIBase_outbound;

	// Liste des ports des machines virtuelles
    protected Map<RepartiteurToVMInboundPort,RepartiteurToVMOutboundPort> rbps;
    protected Map<RepartiteurToVMInboundPort, VMMessages> robps;


	//port par lequel le repartiteur recoit directement les requetes
	protected RepartiteurToVMInboundPort rip;

    // Liste des requetes
    protected ArrayList<Request> listR;
	protected RepartiteurInboundPort rgToRepartiteurInboundPort;

    // Liste des caractéristiques des VM
    protected HashMap<String,VMCarac> listCarac;

	// Port courant de la VM traitant la derniere requete recues
	protected Iterator<Map.Entry<RepartiteurToVMOutboundPort, Status>> robpIt;

	protected RepartiteurToControleurOutboundPort control;

	protected double s1,s2;
	

	/**
	 * Constructeur du repartiteur
	 *
	 * @param outboundPortURI port de sortie du repartiteur
	 * @param appId id de l'application liée au repartiteur
	 */
    public Repartiteur(String portURI, Integer appId/*,double seuil1,double seuil2*/) throws Exception {
        this.addRequiredInterface(RequestArrivalI.class);

        this.addOfferedInterface(RepartiteurProviderI.class);
        this.addRequiredInterface(RepartiteurConsumerI.class);

		this.appId = appId;
		this.repartiteurURIDCC = portURI + "-dcc";
		this.repartiteurURIBase_outbound = portURI + "-outboundPort";
        this.listR = new ArrayList<Request>();
        this.rbps = new HashMap<RepartiteurToVMInboundPort, RepartiteurToVMOutboundPort>();
        this.robps = new HashMap<RepartiteurToVMInboundPort, VMMessages>();
        this.listCarac = new HashMap<String,VMCarac>();
		PortI p = this.rgToRepartiteurInboundPort;

		s1 = 0.025;
		s2 = 0.05;
		
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

		control = new RepartiteurToControleurOutboundPort("repartiteurTOcontroleur"+this.getAppId(),
				this);
		this.addPort(control);

        if (AbstractCVM.isDistributed) {
            control.publishPort();
        } else {
            control.localPublishPort();
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

        String[] uritab = new String[2];

        RepartiteurToVMOutboundPort rbp;
		String URIused = repartiteurURIBase_outbound + (compteurPort++);
        uritab[0] = URIused;

		rbp = new RepartiteurToVMOutboundPort(URIused + "-RepartiteurOutboundPort",
				this);
		this.addPort(rbp);

        if (AbstractCVM.isDistributed) {
            rbp.publishPort();
        } else {
            rbp.localPublishPort();
        }

        RepartiteurToVMInboundPort rip = null;
        String URIusedi = portURI + (compteurPort++);
        uritab[1] = URIusedi;
        try{
        	rip = new RepartiteurToVMInboundPort(URIusedi + "-RepartiteurInboundPort", this);
        }catch(Exception e){
            System.out.println("Probleme new " );e.printStackTrace();
        }

        this.addPort(rip);
		if (AbstractCVM.isDistributed) {
			rip.publishPort() ;
		} else {
			rip.localPublishPort() ;
		}

        rbps.put(rip,rbp);
        robps.put(rip, null);


            return uritab;

	}


    public void SetVMConnection(String URIRep) throws Exception{


		//System.out.println("Setting up connection from "+ URIRep);

		RepartiteurToVMOutboundPort portout = null;

		for(Entry<RepartiteurToVMInboundPort,RepartiteurToVMOutboundPort> entry : rbps.entrySet()){
			//System.out.println(entry.getKey().getPortURI());
			if(entry.getKey().getPortURI().equals(URIRep)){
				//System.out.println("port found");
				portout = entry.getValue();
			}


		}


		//System.out.println("outbound = "+portout.getPortURI());

		String VMUri = portout.getVMURI();
		System.out.println("Connecting "+ URIRep+" to " + VMUri);


		DynamicallyConnectableComponentOutboundPort p= new DynamicallyConnectableComponentOutboundPort(this);
		this.addPort(p);
		p.localPublishPort();
		String in = VMUri.replace("outbound", "inbound");
		//System.out.println("DCC IN = "+in);
		p.doConnection(in+"-dcc",DynamicallyConnectableComponentConnector.class.getCanonicalName());
		p.connectWith(URIRep,VMUri,RepartiteurConnector.class.getCanonicalName());
		p.doDisconnection();
		portout.startNotification();

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

	public String RepartiteurURIBase_outbound() {
		return repartiteurURIBase_outbound;
	}

	/**
	 * @param m Notification de la VM au repartiteur de requetes
     * @throws Exception
     */
    public void notifyStatus(VMMessages m) throws Exception {
    	System.out.println("Repartitor "+ this.getAppId() +" Received status "+m.getStatus() +" from "+ m.getVmID() + " in time " + m.getTime());
		robps.put(m.getRepPort(), m);



		if (m.getStatus()== Status.NEW  ){

			RepartiteurToVMOutboundPort po = rbps.get(m.getRepPort());
			int nb = po.getNbCore();
			while(nb!=0){
			sendNextRequest(po);
			nb--;
			
			}

		}
		if (m.getStatus() == Status.FREE){
			
			RepartiteurToVMOutboundPort po = rbps.get(m.getRepPort());
			sendNextRequest(po);
			
		}

		if (m.getTime()!=0){

			VMCarac car = this.listCarac.get(m.getVmID());
			car.addTime(m.getTime());
			System.out.println("Temps moyen de traitement pour la vm "+this.listCarac.get(m.getVmID()).getMediumtime());

			// time > seuil 2 => nouvelle VM
			if((car.getMediumtime()*this.s2 + car.getMediumtime())<m.getTime()){
				System.out.println("******************************************************************************************************************");
				System.out.println("CREATION DUNE NOUVELLE VM");
				String[] uri=addNewPorts("repartiteur"+this.getAppId());
				control.deployVM(this.getAppId(), uri,this.repartiteurURIDCC);
				SetVMConnection(uri[1]+"-RepartiteurInboundPort");
				System.out.println("******************************************************************************************************************");

			}else{
			// time > seuil 1 => augmentation de fréquence
			if((car.getMediumtime()*this.s1 + car.getMediumtime())<m.getTime()){
				
				System.out.println("MODIFIER LA FREQUENCE DE LA VM");
				
			}
			}
		}
	}

    private void sendNextRequest(RepartiteurToVMOutboundPort po) throws Exception {
    	if(!this.listR.isEmpty()){
    		System.out.println("Envoi de la requette "+ listR.get(0).toString() + "par le répartiteur "+ this.getAppId());
    		po.processRequest(this.listR.remove(0));}
    }

	public void acceptRequest(Request r) throws Exception {

		try {
        	processRequest(r);
        } catch (Exception e) {
        	String URInewPortRepartiteur = "repartiteur" + this.getAppId();
            try {
            	String[] uri=addNewPorts(URInewPortRepartiteur);
				control.deployVM(this.getAppId(), uri,this.repartiteurURIDCC);
				SetVMConnection(uri[1]+"-RepartiteurInboundPort");

			} catch (BadDeploymentException e2) {
				System.out.println("Rejected request: all queues full and "
						+ "maximal number of mv reached");
			} catch (Exception e2) {
				/*System.out.println("Echec de processRequest ! requête : "
						+ r.toString());*/e2.printStackTrace();
			}
        }
        //throw new NoRepartitorException("Rejected request: no dispatcher "
        //		+ "dedicated to the application number: " + r.getAppId());

	}

	/**
	 * Transmet la requete r a une machine virtuelle
	 *
	 * Les machines virtuelles recoivent tour a tour les requetes transmises par
	 * le repartiteur de requetes (pour le moment).
	 *
	 * @param r requete a transmettre
	 * @return false si aucune machine virtuelle ne peut traiter la requete
	      * @throws Exception
     */
    // TODO : Changer la methode d'attribution des requetes
    /*public boolean processRequest(Request r) throws Exception {
        for (RepartiteurToVMOutboundPort robp : robps.keySet()) {
            robp.processRequest(r);
            return true;
        }
        System.out.println("No available mv for the application number: " + r.getAppId());
        return false;
    }*/
    public void processRequest(Request r) throws Exception {


    	if(this.rbps.isEmpty()){
			this.listR.add(r);
			System.out.println("Stockage de requette "+ r.getAppId()+ " - " + listR.size() + " mais pas de VM dispo");
			throw new NotEnoughCapacityVMException("No available mv for the " +
					"application number: " + r.getAppId());

		}
    	this.listR.add(r);
    	System.out.println("Stockage de requette "+ r.getAppId()+ " - " + listR.size());
    	System.out.println("-------------------------------------------------------------------------------------");
    	System.out.println("Demande de notification du repartiteur "+ this.getAppId() + " a ses VM");
    	for(Entry<RepartiteurToVMInboundPort,RepartiteurToVMOutboundPort> entry :rbps.entrySet()){

			entry.getValue().startNotification();

		}
    	
    	System.out.println("-------------------------------------------------------------------------------------");

    }

    public void notifyCarac(String id, VMCarac c) {
    	System.out.println("Repartitor "+ this.getAppId() +" Received carac change from "+ c.getVMid());
		if(!listCarac.containsKey(id))
		this.listCarac.put(id, c);

    }
    
    private void destroyVM(String vm) throws Exception{
    	RepartiteurToVMOutboundPort p = (RepartiteurToVMOutboundPort) this.findPortFromURI(vm);
    	String uriComputerParent = p.getUriComputerParent();
    	
    	this.control.destroyVM(uriComputerParent, vm);
    }

}
