package fr.upmc.alasca.computer.ports;

import fr.upmc.alasca.computer.components.VirtualMachine;
import fr.upmc.alasca.computer.interfaces.RefreshVMI;
import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractInboundPort;

public class ComputerToVMInboundPort extends AbstractInboundPort implements RefreshVMI {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ComputerToVMInboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, RefreshVMI.class, owner);
	}

	@Override
	public void refreshVM(double freq) throws Exception {
		final VirtualMachine vm = (VirtualMachine) this.owner;
		vm.refreshFrequencyVM(freq);
	}

}
