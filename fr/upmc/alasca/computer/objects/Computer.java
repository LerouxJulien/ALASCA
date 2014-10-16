package fr.upmc.alasca.computer.objects;

import java.util.ArrayList;
import java.util.List;

import fr.upmc.alasca.computer.interfaces.ComputerProviderI;
import fr.upmc.alasca.requestgen.objects.Request;

/**
 * La classe <code>Computer</code> definit la machine qui recoit les requetes et
 * qui gere la creation et la destruction des machines virtuelles
 * <code>VirtualMachine</code>.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 * 
 * <p>Created on : 10 oct. 2014</p>
 * 
 * @author	<a href="mailto:henri.ng@etu.upmc.fr">Henri NG</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public class Computer implements ComputerProviderI {

	// ID de la machine
	private final int machineID;
	
	// Nombre de coeurs de la machine
	private final int nbCores;
	
	// Frequence des coeurs
	private final List<Float> frequencies;
	
	// Ecart de frequence maximal entre les differents coeurs
	private final float difference;
	
	// Nombre de coeurs monopolises par les VM
	private int nbCoresUsed;
	
	// Liste des machines virtuelles allouees
	private List<String> listVM;

	/**
	 * Demarre une machine.
	 *
	 * @param machineID
	 * @param nbCores
	 * @param frequencies
	 * @param difference
	 * @param cCVM
	 */
	public Computer(int machineID, int nbCores,
			List<Float> frequencies, float difference) {
		super();
		this.machineID   = machineID;
		this.nbCores     = nbCores;
		this.frequencies = frequencies;
		this.difference  = difference;
		nbCoresUsed      = 0;
		listVM           = new ArrayList<String>();
	}

	/**
	 * Retourne l'ecart de frequence maximal entre les differents coeurs
	 * 
	 * @return difference
	 */
	public float getDifference() {
		return difference;
	}
	
	/**
	 * Retourne la liste de frequence des coeurs
	 * 
	 * @return frequencies
	 */
	public List<Float> getFrequencies() {
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
		return nbCores;
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
		return "Computer [machineID=" + machineID + ", nbCores=" + nbCores
				+ ", frequencies=" + frequencies + ", difference=" + difference
				+ ", nbCoresUsed=" + nbCoresUsed + ", listVM=" + listVM + "]";
	}

	/* ---------------- Implementation of offered functions ----------------- */

	@Override
	public boolean deployVM(int nbCores, int app) {
		int nbCoresTotal = nbCores + nbCoresUsed;
		if (nbCoresTotal > this.nbCores) {
			System.out.println("No more capacity for deploying "
							   + "a new virtual machine !");
			return false;
		}
		int mvID = machineID * 10 + listVM.size();
		List<Float> coresFreq = new ArrayList<Float>(frequencies.
				subList(nbCoresUsed, nbCoresTotal));
		// Taille de la file d'attente des VM a changer
		int queueMax = 20;
		// Appelle la methode de creation de l'uri du composant VM.
		// Pas d'initialisation de l'objet
//		VirtualMachine vm = new VirtualMachine(
//				mvID, nbCores, app, queueMax, coresFreq);
//		listVM.add(vm);
		nbCoresUsed += nbCores;
		System.out.println("Virtual Machine deployed !");
		return true;
	}

	@Override
	public boolean destroyVM(String vm) {
//		if (!vm.isIdle()) {
//			System.out.println("Virtual Machine is still processing !");
//			return false;
//		}
//		nbCoresUsed -= vm.getNbCores();
//		listVM.remove(vm);
//		System.out.println("Virtual Machine killed !");
		return true;
	}
	
	@Override
	public List<String> getListVM() {
		return listVM;
	}

	@Override
	public boolean getRequest(String vm, Request req) {
//		vm.addRequest(req);
		return true;
	}

	@Override
	public boolean reInit(String vm) {
//		if (!vm.isIdle()) {
//			System.out.println("Virtual Machine is still running !");
//			return false;
//		}
//		System.out.println("Virtual Machine re-initialized !");
		return true;
	}

}
