package fr.upmc.alasca.controleurAuto.ports;

import fr.upmc.alasca.controleur.interfaces.ControleurFromRepartiteurProviderI;
import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;

public class CAToControleurOutboundPort  extends AbstractOutboundPort
implements ControleurFromRepartiteurProviderI{
	
	public CAToControleurOutboundPort(String uri, ComponentI owner)
			throws Exception {
		super(uri, ControleurFromRepartiteurProviderI.class, owner);
	}

	@Override
	public void deployVM(int r, String[] uri, String RepartiteurURIDCC)
			throws Exception {
		((ControleurFromRepartiteurProviderI)this.connector).
			deployVM(r, uri, RepartiteurURIDCC);
	}

	@Override
	public void destroyVM(String uriComputerParent, String vm)
			throws Exception {
		((ControleurFromRepartiteurProviderI)this.connector).
			destroyVM(uriComputerParent, vm);
	}

	@Override
	public void incFrequency(int app) throws Exception {
		((ControleurFromRepartiteurProviderI)this.connector).incFrequency(app);
	}

	@Override
	public void initVM(int appID, String uriComputerParent, String vm) throws Exception {
		((ControleurFromRepartiteurProviderI)this.connector).
			initVM(appID, uriComputerParent, vm);
	}

	@Override
	public void reInitVM(String uriComputerParent, String vm) throws Exception {
		((ControleurFromRepartiteurProviderI)this.connector).
			reInitVM(uriComputerParent, vm);		
	}
	
}
