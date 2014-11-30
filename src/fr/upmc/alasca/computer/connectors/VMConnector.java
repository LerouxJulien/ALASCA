package fr.upmc.alasca.computer.connectors;

import java.io.Serializable;

import fr.upmc.alasca.computer.interfaces.VMProviderI;
import fr.upmc.alasca.requestgen.objects.Request;
import fr.upmc.components.connectors.AbstractConnector;

/**
 * La classe VMConnector implemente un connecteur entre un Repartiteur et une
 * VirtualMachine
 * */
public class VMConnector extends AbstractConnector implements VMProviderI,
		Serializable {

	private static final long serialVersionUID = 1L;

	@Override
	public void processRequest(Request r) throws Exception {
		((VMProviderI) this.offering).processRequest(r);
	}

}
