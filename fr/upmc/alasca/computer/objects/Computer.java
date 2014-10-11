package fr.upmc.alasca.computer.objects;

import java.util.List;

public class Computer {

	private final int machineID;
	
	private int nbCores;
	
	private List<Float> frequencies;
	
	private float difference;
	
	private List<VirtualMachine> mvs;

	/**
	 * @param machineID
	 * @param nbCores
	 * @param frequencies
	 * @param difference
	 * @param cCVM
	 */
	public Computer(int machineID, int nbCores, List<Float> frequencies,
			float difference, List<VirtualMachine> mvs) {
		super();
		this.machineID   = machineID;
		this.nbCores     = nbCores;
		this.frequencies = frequencies;
		this.difference  = difference;
		this.mvs         = mvs;
	}

	public int getMachineID() {
		return machineID;
	}

	public int getNbCores() {
		return nbCores;
	}

	public List<Float> getFrequencies() {
		return frequencies;
	}
	
	public float getDifference() {
		return difference;
	}

	public List<VirtualMachine> getCCVM() {
		return mvs;
	}

	@Override
	public String toString() {
		return "Machine [machineID=" + machineID + ", nbCores=" + nbCores
				+ ", frequencies=" + frequencies + ", difference=" + difference
				+ ", mvs=" + mvs + "]";
	}

}
