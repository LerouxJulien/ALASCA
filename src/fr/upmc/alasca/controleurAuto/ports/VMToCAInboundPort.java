package fr.upmc.alasca.controleurAuto.ports;

import fr.upmc.alasca.computer.objects.VMCarac;
import fr.upmc.alasca.computer.objects.VMMessages;
import fr.upmc.alasca.controleurAuto.components.ControleurAutonomique;
import fr.upmc.alasca.controleurAuto.interfaces.CANotificationProviderI;
import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractInboundPort;

public class VMToCAInboundPort extends AbstractInboundPort
implements  CANotificationProviderI{

	private static final long serialVersionUID = 8210006640377358437L;

	public VMToCAInboundPort(String uri, ComponentI owner)
			throws Exception {
		super(uri, CANotificationProviderI.class, owner);
	}

	@Override
	public void notifyStatus(VMMessages m) throws Exception {
		ControleurAutonomique con = (ControleurAutonomique) this.owner;
		con.notifyStatus(m);
	}

	@Override
	public void notifyCarac(String id,VMCarac c) throws Exception {
		ControleurAutonomique con = (ControleurAutonomique) this.owner;
		con.notifyCarac(id, c);
	}
}