package fr.upmc.alasca.controleurAdmission.ports;

import java.util.List;

import fr.upmc.alasca.controleurAdmission.interfaces.ControleurConsumerComputerI;
import fr.upmc.alasca.requestgen.objects.Request;
import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;

public class URIControleurOutboundPort extends AbstractOutboundPort implements ControleurConsumerComputerI {
	/**
	 * create the port with the given URI and the given owner.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param uri	URI of the port.
	 * @param owner	owner of the port.
	 */
	public URIControleurOutboundPort(String uri, ComponentI owner) throws Exception{
		super(uri, ControleurConsumerComputerI.class, owner) ;
	}

	@Override
	public boolean deployVM(int nbCores, int app, String RepartiteurURI) throws Exception {
		return ((ControleurConsumerComputerI) this.connector).deployVM(nbCores, app, RepartiteurURI);
	}

	@Override
	public boolean destroyVM(String mv) throws Exception {
		return ((ControleurConsumerComputerI) this.connector).destroyVM(mv);
	}

	@Override
	public List<String> getListVM() throws Exception {
		return ((ControleurConsumerComputerI) this.connector).getListVM();
	}

	@Override
	public boolean getRequest(String mv, Request req) throws Exception {
		return ((ControleurConsumerComputerI) this.connector).getRequest(mv, req);
	}

	@Override
	public boolean reInit(String vm) throws Exception {
		return ((ControleurConsumerComputerI) this.connector).reInit(vm);
	}

	@Override
	public int nbCoreDispo() {
		return ((ControleurConsumerComputerI) this.connector).nbCoreDispo();
	}

	@Override
	public String getURINewVM() {
		return ((ControleurConsumerComputerI) this.connector).getURINewVM();
	}

}
