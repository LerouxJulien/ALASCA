package fr.upmc.alasca.controleurAuto.connectors;

import java.io.Serializable;

import fr.upmc.alasca.computer.objects.VMCarac;
import fr.upmc.alasca.computer.objects.VMMessages;
import fr.upmc.alasca.controleurAuto.interfaces.CANotificationProviderI;
import fr.upmc.alasca.controleurAuto.interfaces.CAProviderI;
import fr.upmc.components.connectors.AbstractConnector;

public class CAConnector extends AbstractConnector implements 
CAProviderI, CANotificationProviderI, Serializable {

	private static final long serialVersionUID = -651699635097926566L;

	@Override
	public void deployFirstVM() throws Exception { 
		((CAProviderI) this.offering).deployFirstVM();
	}

	@Override
	public void notifyStatus(VMMessages m) throws Exception {
		((CANotificationProviderI) this.offering).notifyStatus(m);
	}

	@Override
	public void notifyCarac(String id, VMCarac c) throws Exception {
		((CANotificationProviderI) this.offering).notifyCarac(id, c);
	}

}