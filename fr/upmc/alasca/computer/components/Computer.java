package fr.upmc.alasca.computer.components;

import java.util.ArrayList;
import java.util.List;

import fr.upmc.alasca.computer.connectors.VMConnector;
import fr.upmc.alasca.computer.interfaces.ComputerProviderI;
import fr.upmc.alasca.computer.ports.ComputerInboundPort;
import fr.upmc.alasca.requestgen.objects.Request;
import fr.upmc.components.AbstractComponent;
import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.components.cvm.pre.dcc.DynamicComponentCreationConnector;
import fr.upmc.components.cvm.pre.dcc.DynamicComponentCreationI;
import fr.upmc.components.cvm.pre.dcc.DynamicComponentCreationOutboundPort;
import fr.upmc.components.cvm.pre.dcc.DynamicallyConnectableComponentConnector;
import fr.upmc.components.cvm.pre.dcc.DynamicallyConnectableComponentI;
import fr.upmc.components.cvm.pre.dcc.DynamicallyConnectableComponentOutboundPort;
import fr.upmc.components.ports.PortI;

/**
 * La classe <code>Computer</code> definit la machine qui recoit les requetes et
 * qui gere la creation et la destruction des <code>VirtualMachine</code>.
 * <p>
 * Created on : 10 oct. 2014
 * </p>
 */
public class Computer extends AbstractComponent implements ComputerProviderI {

	// id de la machine
	private final int machineID;

	// Frequence des coeurs
	private final List<Double> frequencies;

	// Ecart de frequence maximal entre les differents coeurs
	private final double difference;

	// Nombre de coeurs monopolises par les VM
	private int nbCoresUsed;
	
	// compteur utilise pour differencier les identifiants des vm (ne depasse pas 1000)
	private Integer cptVM;
	
	protected AbstractCVM cvm;

	/**
	 * Demarre une machine.
	 *
	 * @param machineID
	 * @param frequencies	Liste des coeurs physiques
	 * @param difference	Difference maximale entre les coeurs
	 * @param cvm
	 */
	public Computer(String port, int machineID, List<Double> frequencies,
			double difference, boolean isDistributed, AbstractCVM cvm) throws Exception {
		super(true);
		this.cvm = cvm;
		this.machineID = machineID;
		this.frequencies = frequencies;
		this.difference = difference;
		this.cptVM = 0;
		this.nbCoresUsed = 0;

		this.addOfferedInterface(ComputerProviderI.class);
		PortI p = new ComputerInboundPort(port, this);
		this.addPort(p);
		if (isDistributed) {
			p.publishPort();
		} else {
			p.localPublishPort();
		}
		
		this.addRequiredInterface(DynamicComponentCreationI.class);
		this.addRequiredInterface(DynamicallyConnectableComponentI.class);
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
	public List<Double> getFrequencies() {
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
	 * Retourne le nombre de coeurs utilisé par les machines virtuelles
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
				+ ", difference=" + difference + ", nbCoresUsed=" + nbCoresUsed + "]";
	}

	/* ---------------- Implementation of offered functions ----------------- */
	/**
	 * Deploie une mv et la connecte au repartiteur de l'application
	 */
	@Override
	public boolean deployVM(int nbCores, int app, String URIRepartiteurFixe, String URIRepartiteurDCC) throws Exception {
		// On verifie que le Computer a assez de coeurs pour allouer la machine virtuelle
		int nbCoresTotal = nbCores + nbCoresUsed;
		if (nbCoresTotal > getFrequencies().size()) {
			System.out.println("No more capacity for deploying "
					+ "a new virtual machine");
			return false;
		}
		String mvID = (machineID * 1000 + ((cptVM++)%1000)) + "";
		List<Double> coresFreq = new ArrayList<Double>(frequencies.subList(
				nbCoresUsed, nbCoresTotal));
		
		// Instanciation machine virtuelle
		DynamicComponentCreationOutboundPort newvm = new DynamicComponentCreationOutboundPort(this);
		newvm.localPublishPort();
		
		this.addPort(newvm);
		newvm.doConnection(""
				+ AbstractCVM.DYNAMIC_COMPONENT_CREATOR_INBOUNDPORT_URI,
				DynamicComponentCreationConnector.class.getCanonicalName());
		
		
		String randomString = this.getMachineID() + java.util.UUID.randomUUID().toString();
		newvm.createComponent(VirtualMachine.class.getCanonicalName(),
				new Object [] {randomString, mvID, app, 5, coresFreq});
		
		// Connexion machine virtuelle
		DynamicallyConnectableComponentOutboundPort p = new DynamicallyConnectableComponentOutboundPort(
				this);
		this.addPort(p);
		p.localPublishPort();
		p.doConnection(URIRepartiteurDCC,
				DynamicallyConnectableComponentConnector.class
						.getCanonicalName());
		p.connectWith(randomString, URIRepartiteurFixe
				+ "-RepartiteurOutboundPort",
				VMConnector.class.getCanonicalName());
		p.doDisconnection();
		nbCoresUsed += nbCores;
		System.out.println("Virtual Machine deployed");
		return true;
	}

	@Override
	public Integer availableCores() throws Exception {
		return getNbCores() - getNbCoresUsed() ;
	}

	@Override
	public boolean destroyVM(String vm) {
		// TODO
		return true;
	}

	@Override
	public boolean reInit(String vm) {
		// TODO
		return true;
	}

	@Override
	public List<String> getListVM() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
