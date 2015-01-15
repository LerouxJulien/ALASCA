package fr.upmc.alasca.computer.ports;

import fr.upmc.alasca.computer.interfaces.RefreshVMI;
import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;

public class ComputerToVMOutboundPort extends AbstractOutboundPort implements RefreshVMI {

	public ComputerToVMOutboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, RefreshVMI.class, owner);
	}

	@Override
	public void refreshVM(double freq) throws Exception {
		((RefreshVMI) this.connector).refreshVM(freq);
	}

}
