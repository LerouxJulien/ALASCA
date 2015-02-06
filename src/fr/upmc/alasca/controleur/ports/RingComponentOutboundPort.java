package fr.upmc.alasca.controleur.ports;

import java.util.ArrayList;

import fr.upmc.alasca.controleur.interfaces.RingComponent;
import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;

public class RingComponentOutboundPort extends AbstractOutboundPort 
implements RingComponent{

	public RingComponentOutboundPort(String uri, ComponentI owner) 
			throws Exception {
		super(uri, RingComponent.class, owner);
	}

	@Override
	public void sendTokenToNextComponent(ArrayList<String> freeVM) {
		((RingComponent) this.connector).sendTokenToNextComponent(freeVM);
	}
	
}

