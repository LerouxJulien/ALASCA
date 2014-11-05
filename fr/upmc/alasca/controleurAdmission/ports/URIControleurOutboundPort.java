package fr.upmc.alasca.controleurAdmission.ports;

import java.util.List;

import fr.upmc.alasca.controleurAdmission.interfaces.ControleurConsumerComputerI;
import fr.upmc.alasca.requestgen.objects.Request;
import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;

public class URIControleurOutboundPort extends AbstractOutboundPort implements ControleurConsumerComputerI {
	/**
	 * La classe <code>URIControleurInboundPort</code> créer un port avec un URI et
	 * un composant <code>Controleur</code> et fournit les fonctions définit dans 
	 * l'interface requise <code>ControleurConsumerComputerI</code>
	 *
	 * <p><strong>Description</strong></p>
	 * 
	 * <p><strong>Invariant</strong></p>
	 * 
	 * <pre>
	 * invariant	true
	 * </pre>
	 * 
	 * <p>Created on : 10 oct. 2014</p>
	 * 
	 * @author	<a href="mailto:william.chasson@etu.upmc.fr">William CHASSON</a>
	 * @version	$Name$ -- $Revision$ -- $Date$
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
