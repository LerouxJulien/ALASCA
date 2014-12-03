package fr.upmc.alasca.computer.components;

import java.util.ArrayList;
import java.util.List;

import fr.upmc.alasca.computer.connectors.VMConnector;
import fr.upmc.alasca.computer.interfaces.ComputerProviderI;
import fr.upmc.alasca.computer.ports.ComputerInboundPort;
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
public class Computer extends AbstractComponent implements ComputerProviderI {

	// ID de la machine
	private final int machineID;

	// Frequence des coeurs
	private final List<Double> frequencies;

	// Ecart de frequence maximal entre les differents coeurs
	private final double difference;

	// Nombre de coeurs monopolises par les machines virtuelles
	private int nbCoresUsed;

	// Compteur utilise pour differencier les identifiants des machines
	// virtuelles (ne depasse pas 1000)
	private Integer cptVM;

	protected AbstractCVM cvm;

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
	public Computer(String port, int machineID, List<Double> frequencies,
			double difference, boolean isDistributed, AbstractCVM cvm)
			throws Exception {
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
	@Override
	public Integer availableCores() throws Exception {
		return getNbCores() - getNbCoresUsed();
	}
	
	/**
	 * Deploie une machine virtuelle et la connecte au repartiteur de requete de
	 * l'application
	 * 
	 * @param nbCores
	 * 			  Nombre de coeurs attribues � la machine virtuelle
	 * @param app
	 *			  ID de l'application
	 * @param URIRepartiteurFixe
	 *            URI du port dans Repartiteur
	 * @param URIRepartiteurDCC
	 *            URI du dcc dans Repartiteur
	 * @return boolean
	 */
	public boolean deployVM(int nbCores, int app, String URIRepartiteurFixe,
			String URIRepartiteurDCC) throws Exception {
		// On verifie que le Computer a assez de coeurs pour allouer la machine
		// virtuelle.
		int nbCoresTotal = nbCores + nbCoresUsed;
		if (nbCoresTotal > getFrequencies().size()) {
			System.out.println("No more capacity for deploying "
					+ "a new virtual machine");
			return false;
		}
		String mvID = (machineID * 1000 + ((cptVM++) % 1000)) + "";
		List<Double> coresFreq = new ArrayList<Double>(frequencies.subList(
				nbCoresUsed, nbCoresTotal));

		// Instanciation machine virtuelle
		DynamicComponentCreationOutboundPort newvm = new DynamicComponentCreationOutboundPort(
				this);
		newvm.localPublishPort();

		this.addPort(newvm);
		newvm.doConnection("request_generator_jvm_uri" //TODO a changer
				+ AbstractCVM.DYNAMIC_COMPONENT_CREATOR_INBOUNDPORT_URI,
				DynamicComponentCreationConnector.class.getCanonicalName());

		String randomString = this.getMachineID()
				+ java.util.UUID.randomUUID().toString();
		newvm.createComponent(VirtualMachine.class.getCanonicalName(),
				new Object[] { randomString, mvID, app, 5, coresFreq });

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

	/**
	 * Detruit une machine virtuelle via son URI
	 * 
	 * @param vm
	 * @return boolean
	 */
	@Override
	public boolean destroyVM(String vm) {
		// TODO
		return true;
	}

	/**
	 * Reinitialise une machine virtuelle via son URI
	 * 
	 * @param vm
	 * @return boolean
	 */
	@Override
	public boolean reInit(String vm) {
		// TODO
		return true;
	}

}
