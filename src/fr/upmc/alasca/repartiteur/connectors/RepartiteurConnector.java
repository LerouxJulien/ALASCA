package fr.upmc.alasca.repartiteur.connectors;

import java.io.Serializable;

import fr.upmc.alasca.computer.interfaces.VMConsumerI;
import fr.upmc.alasca.computer.interfaces.VMProviderI;
import fr.upmc.alasca.computer.objects.VMCarac;
import fr.upmc.alasca.computer.objects.VMMessages;
import fr.upmc.alasca.repartiteur.interfaces.RepartiteurConsumerI;
import fr.upmc.alasca.repartiteur.interfaces.RepartiteurProviderI;
import fr.upmc.alasca.requestgen.objects.Request;
import fr.upmc.components.connectors.AbstractConnector;

public class RepartiteurConnector extends AbstractConnector implements RepartiteurProviderI,
RepartiteurConsumerI, Serializable {

	@Override
	public void processRequest(Request r) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getVMURI() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void startNotification() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void notifyStatus(VMMessages m) throws Exception { 
		((RepartiteurProviderI) this.offering).notifyStatus(m);
	}

	@Override
	public void notifyCarac(String id, VMCarac c) throws Exception{
		((RepartiteurProviderI) this.offering).notifyCarac(id, c);
		
	}

}
