package fr.upmc.alasca.controleurAuto.ports;

import fr.upmc.alasca.computer.objects.VMCarac;
import fr.upmc.alasca.computer.objects.VMMessages;
import fr.upmc.alasca.controleurAuto.components.ControleurAutonomique;
import fr.upmc.alasca.controleurAuto.interfaces.ControleurAutoProviderI;
import fr.upmc.alasca.repartiteur.interfaces.RepartiteurProviderI;
import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractInboundPort;

/**
 * Port d'entrée du repartiteur venant d'une VM
 * 
 * @author Julien Leroux
 *
 */
public class RepartiteurToCAInboundPort extends AbstractInboundPort
implements  RepartiteurProviderI{

	private static final long serialVersionUID = 8210006640377358437L;

	public RepartiteurToCAInboundPort(String uri, ComponentI owner)
			throws Exception {
		super(uri, ControleurAutoProviderI.class, owner);
	}

	@Override
	public void notifyStatus(VMMessages m) throws Exception {
		//m.setRepPort(this);
		ControleurAutonomique con = (ControleurAutonomique) this.owner;
		con.notifyStatus(m);
	}

	@Override
	public void notifyCarac(String id,VMCarac c) throws Exception {
		ControleurAutonomique con = (ControleurAutonomique) this.owner;
		con.notifyCarac(id, c);
	}

	@Override
	public String[] addNewPorts(String portURI) throws Exception {
		// leave empty
		return null;
	}

	@Override
	public void setVMConnection(String URIRep) throws Exception {
		// leave empty
	}
	
}
