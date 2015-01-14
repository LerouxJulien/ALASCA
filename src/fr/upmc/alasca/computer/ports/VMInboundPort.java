package fr.upmc.alasca.computer.ports;

import fr.upmc.alasca.computer.components.VirtualMachine;
import fr.upmc.alasca.computer.interfaces.VMConsumerI;
import fr.upmc.alasca.computer.interfaces.VMProviderI;
import fr.upmc.alasca.requestgen.objects.Request;
import fr.upmc.components.ComponentI;
import fr.upmc.components.exceptions.ComponentShutdownException;
import fr.upmc.components.ports.AbstractInboundPort;

public class VMInboundPort extends AbstractInboundPort implements VMProviderI {

	private static final long serialVersionUID = 5983071151387825406L;

	public VMInboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, VMProviderI.class, owner);
	}

	@Override
	public void processRequest(Request r) throws Exception {
		final VirtualMachine vm = (VirtualMachine) this.owner;
		vm.requestArrivalEvent(r);
	}

	@Override
	public String getVMURI() throws Exception {
		final VirtualMachine vm = (VirtualMachine) this.owner;
		
		return vm.getVMoport().getPortURI();
		
		
	}

	@Override
	public void startNotification() throws Exception {
		final VirtualMachine vm = (VirtualMachine) this.owner;
		vm.startNotification();
		
	}
	
	
	@Override
	public String getUriComputerParent() throws Exception {
		final VirtualMachine vm = (VirtualMachine) this.owner;
		return vm.getUriComputerParent();
	}

	@Override
	public int getNbCores() throws Exception {
		final VirtualMachine vm = (VirtualMachine) this.owner;
		return vm.getNbCores();
	}

	@Override
	public void shutdown() throws Exception {
		final VirtualMachine vm = (VirtualMachine) this.owner;
		vm.shutdown();
	}

}
