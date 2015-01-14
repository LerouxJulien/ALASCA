package fr.upmc.alasca.computer.components;

import java.util.ArrayList;
import java.util.List;

import fr.upmc.alasca.computer.connectors.VMConnector;
import fr.upmc.alasca.computer.exceptions.BadDestroyException;
import fr.upmc.alasca.computer.exceptions.BadReinitialisationException;
import fr.upmc.alasca.computer.exceptions.NotEnoughCapacityVMException;
import fr.upmc.alasca.computer.interfaces.ComputerProviderI;
import fr.upmc.alasca.computer.ports.ComputerInboundPort;
import fr.upmc.alasca.computer.ports.VMInboundPort;
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
	private final ArrayList<Double> frequencies;

	// Ecart de frequence maximal entre les differents coeurs
	private final double difference;

	// Nombre de coeurs monopolises par les machines virtuelles
	private int nbCoresUsed;

	// Compteur utilise pour differencier les identifiants des machines
	// virtuelles (ne depasse pas 1000)
	private Integer cptVM;

	protected AbstractCVM cvm;
	private String uriInboundComputer;
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
			double difference, boolean isDistributed, AbstractCVM cvm)
			throws Exception {
		super(true);
		this.cvm = cvm;
		this.machineID = machineID;
		this.frequencies = frequencies;
		this.difference = difference;
		this.cptVM = 0;
		this.nbCoresUsed = 0;
		this.uriInboundComputer = port;
		
		
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
	 * Retourne le nombre de coeurs utilisï¿½ par les machines virtuelles
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
	 * 			  Nombre de coeurs attribues ï¿½ la machine virtuelle
	 * @param app
	 *			  ID de l'application
	 * @param URIRepartiteurFixe
	 *            URI du port dans Repartiteur
	 * @param URIRepartiteurDCC
	 *            URI du dcc dans Repartiteur
	 * @throws Exception 
	 */
	@Override
	public void deployVM(int nbCores, int app, String[] URIRepartiteurFixe,
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
				DynamicComponentCreationOutboundPort newvm = new DynamicComponentCreationOutboundPort(
						this);
				newvm.localPublishPort();

				this.addPort(newvm);
				newvm.doConnection("request_generator_jvm_uri" //TODO a changer
						+ AbstractCVM.DYNAMIC_COMPONENT_CREATOR_INBOUNDPORT_URI,
						DynamicComponentCreationConnector.class.getCanonicalName());

				String randomString = this.getMachineID()
						+ java.util.UUID.randomUUID().toString();
				Integer appi = app;
				Integer num = 5;
				String uriCPU = this.uriInboundComputer;
				newvm.createComponent(VirtualMachine.class.getCanonicalName(),
						new Object[] {uriCPU, randomString, mvID,  appi,num, coresFreq });
				
				
				
				
				
				
				// Connexion machine virtuelle
				DynamicallyConnectableComponentOutboundPort p = new DynamicallyConnectableComponentOutboundPort(
						this);
				this.addPort(p);
				p.localPublishPort();
				
				p.doConnection(URIRepartiteurDCC,
						DynamicallyConnectableComponentConnector.class
								.getCanonicalName());
				p.connectWith(randomString, URIRepartiteurFixe[0]
						+ "-RepartiteurOutboundPort",
						VMConnector.class.getCanonicalName());
				p.doDisconnection();
			
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
	@Override
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
	@Override
	public void reInit(String vm) throws BadReinitialisationException {
		// TODO : Reinitialisation a implementer
		/*boolean m = true; 
		if (m) {
			System.out.println("On détruit la machine.");
		} else {
			throw new BadReinitialisationException("Impossible de r�initialiser la VM : " + vm);
		}*/
	}

}
