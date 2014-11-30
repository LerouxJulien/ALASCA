package fr.upmc.alasca.computer.ports;

import fr.upmc.alasca.computer.components.VirtualMachine;
import fr.upmc.alasca.computer.interfaces.VMProviderI;
import fr.upmc.alasca.computer.objects.VMMessages;
import fr.upmc.alasca.requestgen.objects.Request;
import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractInboundPort;

public class VMInboudPort extends AbstractInboundPort implements VMProviderI {

	private static final long serialVersionUID = 5983071151387825406L;

	public VMInboudPort(String uri, ComponentI owner) throws Exception {
		super(uri, VMProviderI.class, owner);
	}

	@Override
	public void processRequest(Request r) throws Exception {
		final VirtualMachine vm = (VirtualMachine) this.owner;
		vm.requestArrivalEvent(r);
	}
	
	@Override
	public void notifyRR(VMMessages m) throws Exception {
		// TODO : Je te dois faire un truc la mais quoi ????
	}

}
