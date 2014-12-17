package fr.upmc.alasca.computer.ports;

import fr.upmc.alasca.computer.components.VirtualMachine;
import fr.upmc.alasca.computer.interfaces.VMConsumerI;
import fr.upmc.alasca.requestgen.objects.Request;
import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractInboundPort;

public class VMInboundPort extends AbstractInboundPort implements VMConsumerI {

	private static final long serialVersionUID = 5983071151387825406L;

	public VMInboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, VMConsumerI.class, owner);
	}

	@Override
	public void processRequest(Request r) throws Exception {
		final VirtualMachine vm = (VirtualMachine) this.owner;
		vm.requestArrivalEvent(r);
	}

}
