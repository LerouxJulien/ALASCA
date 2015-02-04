package fr.upmc.alasca.controleurAuto.ports;

import fr.upmc.alasca.controleurAuto.components.ControleurAutonomique;
import fr.upmc.alasca.controleurAuto.interfaces.CAProviderI;
import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractInboundPort;

/**
 * Port d'entrée du repartiteur venant d'une VM
 * 
 * @author Julien Leroux
 *
 */
public class RepartiteurToCAInboundPort extends AbstractInboundPort
implements  CAProviderI{

	private static final long serialVersionUID = 82L;

	public RepartiteurToCAInboundPort(String uri, ComponentI owner)
			throws Exception {
		super(uri, CAProviderI.class, owner);
	}

	@Override
	public void deployFirstVM() throws Exception {
		ControleurAutonomique con = (ControleurAutonomique) this.owner;
		con.deployFirstVM();
	}
	
}
