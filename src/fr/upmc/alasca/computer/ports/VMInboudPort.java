package fr.upmc.alasca.computer.ports;

import fr.upmc.alasca.computer.components.VirtualMachine;
import fr.upmc.alasca.computer.interfaces.VMProviderI;
import fr.upmc.alasca.requestgen.objects.Request;
import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractInboundPort;

public class VMInboudPort extends AbstractInboundPort implements VMProviderI {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public VMInboudPort(String uri, ComponentI owner) throws Exception {
		super(uri, VMProviderI.class, owner);
	}

	@Override
	public void processRequest(Request r) throws Exception {
		final VirtualMachine vm = (VirtualMachine) this.owner;
		vm.requestArrivalEvent(r);
	}

	@Override
	public boolean queueIsFull() {
		final VirtualMachine vm = (VirtualMachine) this.owner;
		return vm.queueIsFull();
	}

}
