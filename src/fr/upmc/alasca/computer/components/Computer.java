package fr.upmc.alasca.computer.components;

import java.util.ArrayList;
import java.util.List;

import fr.upmc.alasca.computer.connectors.VMConnector;
import fr.upmc.alasca.computer.exceptions.BadDestroyException;
import fr.upmc.alasca.computer.exceptions.BadReinitialisationException;
import fr.upmc.alasca.computer.exceptions.NotEnoughCapacityVMException;
import fr.upmc.alasca.computer.interfaces.ComputerProviderI;
import fr.upmc.alasca.computer.ports.ComputerInboundPort;
import fr.upmc.alasca.computer.ports.ComputerToVMOutboundPort;
import fr.upmc.alasca.computer.ports.VMInboundPort;
import fr.upmc.alasca.controleurAuto.connectors.CAConnector;
import fr.upmc.components.AbstractComponent;
import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.components.cvm.pre.dcc.DynamicComponentCreationConnector;
import fr.upmc.components.cvm.pre.dcc.DynamicComponentCreationI;
import fr.upmc.components.cvm.pre.dcc.DynamicComponentCreationOutboundPort;
import fr.upmc.components.cvm.pre.dcc.DynamicallyConnectableComponentConnector;
import fr.upmc.components.cvm.pre.dcc.DynamicallyConnectableComponentI;
import fr.upmc.components.cvm.pre.dcc.DynamicallyConnectableComponentInboundPort;
import fr.upmc.components.cvm.pre.dcc.DynamicallyConnectableComponentOutboundPort;
import fr.upmc.components.cvm.pre.dcc.DynamicallyConnectableI;
import fr.upmc.components.ports.PortI;

/**
 * La classe <code>Computer</code> definit la machine qui gere la creation et la
 * destruction des machines virtuelles <code>VirtualMachine</code>.
 * 
 * <p>
 * Created on : 10 oct. 2014
 * </p>
 * 
 * @author <a href="mailto:nicolas.mounier@etu.upmc.fr">Nicolas Mounier</a>
 * @author <a href="mailto:henri.ng@etu.upmc.fr">Henri NG</a>
 * @version $Name$ -- $Revision$ -- $Date$
 */
public class Computer extends AbstractComponent implements DynamicallyConnectableI {

	// ID de la machine
	private final int machineID;

	// Frequence des coeurs
	private final ArrayList<Double> frequencies;
	
	protected ArrayList<ComputerToVMOutboundPort> listeVM = new ArrayList<ComputerToVMOutboundPort>();

	protected DynamicallyConnectableComponentInboundPort dccInboundPort;
	
	// Ecart de frequence maximal entre les differents coeurs
	private final double difference;

	// Nombre de coeurs monopolises par les machines virtuelles
	private int nbCoresUsed;
	
	private final Double freqMax;

	// Compteur utilise pour differencier les identifiants des machines
	// virtuelles (ne depasse pas 1000)
	private Integer cptVM;

	protected AbstractCVM cvm;
	private final String URIBaseComputer;
	/**
	 * Demarre une machine.
	 *
	 * @param machineID
	 * @param frequencies
	 *            Liste des coeurs physiques
	 * @param difference
	 *            Difference maximale entre les coeurs
	 * @param cvm
	 */
	public Computer(String port, int machineID, ArrayList<Double> frequencies,
			double difference, double freqMax, boolean isDistributed, AbstractCVM cvm)
			throws Exception {
		super(true);
		this.cvm = cvm;
		this.machineID = machineID;
		this.frequencies = frequencies;
		this.difference = difference;
		this.cptVM = 0;
		this.nbCoresUsed = 0;
		this.URIBaseComputer = port;
		this.freqMax = freqMax;
		
		
		
		this.addOfferedInterface(ComputerProviderI.class);
		PortI p = new ComputerInboundPort(port, this);
		this.addPort(p);
		if (isDistributed) {
			p.publishPort();
		} else {
			p.localPublishPort();
		}
		
		this.addOfferedInterface(DynamicallyConnectableComponentI.class);
		this.dccInboundPort = new DynamicallyConnectableComponentInboundPort(
				port + "-dcc", this);
		if (AbstractCVM.isDistributed) {
			this.dccInboundPort.publishPort();
		} else {
			this.dccInboundPort.localPublishPort();
		}
		this.addPort(dccInboundPort);

		this.addRequiredInterface(DynamicComponentCreationI.class);
		this.addRequiredInterface(DynamicallyConnectableComponentI.class);
	}

	public Double getFreqMax() {
		return freqMax;
	}

	/**
	 * Retourne l'ecart de frequence maximal entre les differents coeurs
	 * 
	 * @return difference
	 */
	public double getDifference() {
		return difference;
	}

	/**
	 * Retourne la liste de frequence des coeurs
	 * 
	 * @return frequencies
	 */
	public ArrayList<Double> getFrequencies() {
		return frequencies;
	}

	/**
	 * Retourne l'ID de la machine
	 * 
	 * @return machineID
	 */
	public int getMachineID() {
		return machineID;
	}

	/**
	 * Retourne le nombre de coeurs
	 * 
	 * @return nbCores
	 */
	public int getNbCores() {
		return getFrequencies().size();
	}

	/**
	 * Retourne le nombre de coeurs utilisÃ¯Â¿Â½ par les machines virtuelles
	 * 
	 * @return nbCoresUsed
	 */
	public int getNbCoresUsed() {
		return nbCoresUsed;
	}

	/**
	 * Retourne la description de la machine en l'etat
	 * 
	 * @return string
	 */
	@Override
	public String toString() {
		return "Computer [machineID=" + machineID + ", nbCores="
				+ getFrequencies().size() + ", frequencies=" + frequencies
				+ ", difference=" + difference + ", nbCoresUsed=" + nbCoresUsed
				+ "]";
	}

	/* ---------------- Implementation of offered functions ----------------- */
	
	/**
	 * Retourne le nombre de coeurs de Computer qui ne sont pas utilises par
	 * une machine virtuelle
	 * 
	 * @return nbCoreFree
	 * @throws Exception
	 */
	public Integer availableCores() throws Exception {
		return getNbCores() - getNbCoresUsed();
	}
	
	/**
	 * Deploie une machine virtuelle et la connecte au repartiteur de requete de
	 * l'application
	 * 
	 * @param nbCores
	 * 			  Nombre de coeurs attribues Ã¯Â¿Â½ la machine virtuelle
	 * @param app
	 *			  ID de l'application
	 * @param URIRepartiteurFixe
	 *            URI du port dans Repartiteur
	 *            0 : 
	 *            1 : URI Repariteur dcc
	 *            2 : URI CA
	 * @param URIRepartiteurDCC
	 *            URI du dcc dans Repartiteur
	 * @throws Exception 
	 */
	public void deployVM(int nbCores, int app, String[] URIToConnect,
			String URIRepartiteurDCC) throws Exception {
		// On verifie que le Computer a assez de coeurs pour allouer la machine
		// virtuelle.
		int nbCoresTotal = nbCores + nbCoresUsed;
		if (nbCoresTotal > getFrequencies().size()) {
			throw new NotEnoughCapacityVMException("No more capacity for " +
		"deploying a new virtual machine");
		}
		String mvID = (machineID * 1000 + ((cptVM++) % 1000)) + "";
		List<Double> coresFreq = new ArrayList<Double>(frequencies.subList(
				nbCoresUsed, nbCoresTotal));
		try{
		// Instanciation machine virtuelle
		DynamicComponentCreationOutboundPort newvm = 
				new DynamicComponentCreationOutboundPort(this);
		newvm.localPublishPort();

		this.addPort(newvm);
		if (AbstractCVM.isDistributed) {
			newvm.doConnection("request_generator_jvm_uri" 
					+ AbstractCVM.DYNAMIC_COMPONENT_CREATOR_INBOUNDPORT_URI,
					DynamicComponentCreationConnector.class.getCanonicalName());
		} else {
			newvm.doConnection(""
					+ AbstractCVM.DYNAMIC_COMPONENT_CREATOR_INBOUNDPORT_URI,
					DynamicComponentCreationConnector.class.getCanonicalName());
		}

		String randomString = this.getMachineID()
				+ java.util.UUID.randomUUID().toString();
		Integer appi = app;
		Integer num = 5;
		String uriCPU = this.URIBaseComputer;
		newvm.createComponent(VirtualMachine.class.getCanonicalName(),
				new Object[] {uriCPU, randomString, mvID,  appi,num, coresFreq });
		
		
		
		
		
		
		// Connexion Repartiteur -> VM
		DynamicallyConnectableComponentOutboundPort p = new DynamicallyConnectableComponentOutboundPort(
				this);
		this.addPort(p);
		p.localPublishPort();
		
		p.doConnection(URIRepartiteurDCC,
				DynamicallyConnectableComponentConnector.class
						.getCanonicalName());
		p.connectWith(randomString, URIToConnect[0],
				VMConnector.class.getCanonicalName());
		p.doDisconnection();
		// FIN Connexion Repartiteur -> VM
		
		// TODO Connexion VM -> CA
		DynamicallyConnectableComponentOutboundPort p1 = new DynamicallyConnectableComponentOutboundPort(
				this);
		this.addPort(p);
		p1.localPublishPort();
		
		p1.doConnection(randomString + AbstractCVM.DYNAMIC_COMPONENT_CREATOR_INBOUNDPORT_URI,
				DynamicallyConnectableComponentConnector.class
						.getCanonicalName());
		p1.connectWith(URIToConnect[2], randomString + "-VMToCAOutboundPort",
				CAConnector.class.getCanonicalName());
		p1.doDisconnection();
		// FIN Connexion VM -> CA
		
		ComputerToVMOutboundPort port_to_vm = new ComputerToVMOutboundPort(URIBaseComputer
				+ "-" + app
				+ "-ComputerToVMOutboundPort", this);
		this.addPort(port_to_vm);
		if (AbstractCVM.isDistributed) {
			port_to_vm.publishPort() ;
		} else {
			port_to_vm.localPublishPort() ;
		}
		listeVM.add(port_to_vm);
		
//		p.doConnection(URIBaseComputer + "-dcc",
//				DynamicallyConnectableComponentConnector.class
//						.getCanonicalName());
//		p.connectWith(randomString + "-ComputerToVMInboundPort", URIBaseComputer
//				+ "-" + app
//				+ "-ComputerToVMOutboundPort",
//				CompVMConnector.class.getCanonicalName());
//		p.doDisconnection();
	
		}catch(Exception e){
			
			e.printStackTrace();
			
		}
		nbCoresUsed += nbCores;
		System.out.println("Virtual Machine deployed");
	}

	/**
	 * Detruit une machine virtuelle via son URI
	 * 
	 * @param vm
	 * @throws Exception 
	 */
	public void destroyVM(String vm) throws Exception {
		VMInboundPort portVM = (VMInboundPort) this.findPortFromURI(vm);
		int nbCoreALiberer = portVM.getNbCores();
		
		try {
			portVM.shutdown();
			portVM.doDisconnection();
			portVM.unpublishPort();
			this.nbCoresUsed -= nbCoreALiberer;
		} catch (Exception e) {
			throw new BadDestroyException("Impossible de supprimer la VM : " + vm);
		}
	}

	/**
	 * Reinitialise une machine virtuelle via son URI
	 * 
	 * @param vm
	 * @throws BadReinitialisationException 
	 */
	public void reInit(String vm) throws BadReinitialisationException {
		// TODO : Reinitialisation a implementer
		/*boolean m = true; 
		if (m) {
			System.out.println("On dÃ©truit la machine.");
		} else {
			throw new BadReinitialisationException("Impossible de rï¿½initialiser la VM : " + vm);
		}*/
	}
	
	public boolean isMaxed(int appid){
		/*if(this.mapVM.isEmpty()){
			return false;
		}
		ArrayList<ComputerToVMOutboundPort> l = this.mapVM.get(appid);
		for(ComputerToVMOutboundPort p : l){
			if
		}*/
		for(Double d: frequencies){
			if(d < freqMax) return false;
		}
		return true;
	}
	
	public void incFrequency(int appid) throws Exception{
		System.out.println("Modification coeurs physiques");
		for(int i = 0 ; i < frequencies.size(); ++i){
			frequencies.set(i, frequencies.get(i) + 0.5);
			if(frequencies.get(i) > freqMax){
				frequencies.set(i, freqMax);
			}
		}
		for(ComputerToVMOutboundPort p : this.listeVM){
			p.refreshVM(this.frequencies.get(0));
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
	

}
