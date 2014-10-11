package fr.upmc.alasca.computer.objects;

import java.util.List;

public class VirtualMachine {

	private final int mvID;
	
	private int nbCores;
	
	private List<Float> frequencies;
	
	private float difference;
	/**
	 * @param mvID
	 * @param nbCores
	 * @param frequencies
	 * @param difference
	 */
	public VirtualMachine(int mvID, int nbCores, List<Float> frequencies,
			float difference) {
		super();
		this.mvID = mvID;
		this.nbCores = nbCores;
		this.frequencies = frequencies;
		this.difference = difference;
	}

	public int getNbCores() {
		return nbCores;
	}

	public void setNbCores(int nbCores) {
		this.nbCores = nbCores;
	}

	public List<Float> getFrequencies() {
		return frequencies;
	}

	public void setFrequencies(List<Float> frequencies) {
		this.frequencies = frequencies;
	}

	public float getDifference() {
		return difference;
	}

	public void setDifference(float difference) {
		this.difference = difference;
	}

	public int getMvID() {
		return mvID;
	}

	@Override
	public String toString() {
		return "MachineVirtuelle [mvID=" + mvID + ", nbCores=" + nbCores
				+ ", frequencies=" + frequencies + ", difference=" + difference
				+ "]";
	}
	
}
