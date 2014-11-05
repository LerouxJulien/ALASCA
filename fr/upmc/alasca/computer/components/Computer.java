package fr.upmc.alasca.computer.components;

import java.util.ArrayList;
import java.util.List;

import fr.upmc.alasca.computer.interfaces.ComputerProviderI;
import fr.upmc.alasca.computer.interfaces.ManagementVMI;
import fr.upmc.alasca.computer.main.VMConnector;
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
 * qui gere la creation et la destruction des machines virtuelles
 * <code>VirtualMachine</code>.
 *
 * <p>
 * <strong>Description</strong>
 * </p>
 * 
 * <p>
 * <strong>Invariant</strong>
 * </p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 * 
 * <p>
 * Created on : 10 oct. 2014
 * </p>
 * 
 * @author <a href="mailto:henri.ng@etu.upmc.fr">Henri NG</a>
 * @version $Name$ -- $Revision$ -- $Date$
 */
public class Computer extends AbstractComponent implements ComputerProviderI {

	// ID de la machine
	private final int machineID;

	// Nombre de coeurs de la machine
	// private final int nbCores;

	// Frequence des coeurs
	private final List<Double> frequencies;

	// Ecart de frequence maximal entre les differents coeurs
	private final double difference;

	// Nombre de coeurs monopolises par les VM
	private int nbCoresUsed;

	// Liste des machines virtuelles allouees
	private List<String> listVM;
	
	protected AbstractCVM cvm;

	/**
	 * Demarre une machine.
	 *
	 * @param machineID
	 * @param nbCores
	 * @param frequencies
	 * @param difference
	 * @param cCVM
	 */
	public Computer(String port, int machineID, List<Double> frequencies,
			double difference, boolean isDistributed, AbstractCVM cvm) throws Exception {
		super(true);
		this.cvm = cvm;
		this.machineID = machineID;
		this.frequencies = frequencies;
		this.difference = difference;
		nbCoresUsed = 0;
		listVM = new ArrayList<String>();

		this.addOfferedInterface(ManagementVMI.class);
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
	 * Retourne le nombre de coeurs disponibles
	 * 
	 * @return nbCoresFree
	 */
	public int nbCoreDispo() {
		return getNbCores() - getNbCoresUsed();
	}

	/**
	 * Retourne le nombre de coeurs utilis� par les machines virtuelles
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
				+ ", listVM=" + listVM + "]";
	}

	/* ---------------- Implementation of offered functions ----------------- */
	/**
	 * Deploie une mv et la connecte au repartiteur de l'application
	 */
	@Override
	public boolean deployVM(int nbCores, int app, String RepartiteurURI) throws Exception {
		int nbCoresTotal = nbCores + nbCoresUsed;
		if (nbCoresTotal > getFrequencies().size()) {
			System.out.println("No more capacity for deploying "
					+ "a new virtual machine !");
			return false;
		}
		int mvID = machineID * 10 + listVM.size();
		List<Double> coresFreq = new ArrayList<Double>(frequencies.subList(
				nbCoresUsed, nbCoresTotal));
		
		///////////////////////////
		DynamicComponentCreationOutboundPort newvm = new DynamicComponentCreationOutboundPort(this);
		newvm.localPublishPort();
		
		this.addPort(newvm);
		newvm.doConnection(""
				+ AbstractCVM.DYNAMIC_COMPONENT_CREATOR_INBOUNDPORT_URI,
				DynamicComponentCreationConnector.class.getCanonicalName());
		
		/////////////////////////////
		String randomString = this.getMachineID() + java.util.UUID.randomUUID().toString();
		newvm.createComponent(VirtualMachine.class.getCanonicalName(),
				new Object [] {randomString, mvID, app, nbCores, 5, coresFreq});
		DynamicallyConnectableComponentOutboundPort p = new DynamicallyConnectableComponentOutboundPort(
				this);
		this.addPort(p);
		p.localPublishPort();
		p.doConnection(RepartiteurURI + "-dcc",
				DynamicallyConnectableComponentConnector.class
						.getCanonicalName());
		p.connectWith(randomString, RepartiteurURI
				+ "-RepartiteurOutboundPort",
				VMConnector.class.getCanonicalName());
		p.doDisconnection();
		nbCoresUsed += nbCores;
		System.out.println("Virtual Machine deployed !");
		return true;
	}

	@Override
	public boolean destroyVM(String vm) {
		// if (!vm.isIdle()) {
		// System.out.println("Virtual Machine is still processing !");
		// return false;
		// }
		// nbCoresUsed -= vm.getNbCores();
		// listVM.remove(vm);
		// System.out.println("Virtual Machine killed !");
		return true;
	}

	@Override
	public List<String> getListVM() {
		return listVM;
	}

	@Override
	public boolean getRequest(String vm, Request req) {
		// vm.addRequest(req);
		return true;
	}

	@Override
	public boolean reInit(String vm) {
		// if (!vm.isIdle()) {
		// System.out.println("Virtual Machine is still running !");
		// return false;
		// }
		// System.out.println("Virtual Machine re-initialized !");
		return true;
	}

}
