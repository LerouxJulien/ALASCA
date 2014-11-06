package fr.upmc.alasca.controleurBouchon.ports;

import java.util.List;

import fr.upmc.alasca.computer.interfaces.ComputerProviderI;
import fr.upmc.alasca.requestgen.objects.Request;
import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;

public class  ControleurBouchonOutboundPort extends		AbstractOutboundPort
implements	ComputerProviderI {

	public ControleurBouchonOutboundPort(String uri,
		ComponentI owner) throws Exception {
		super(uri, ComputerProviderI.class, owner) ;

		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean deployVM(int nbCores, int app, String RepartiteurURI, String RepartiteurURIDCC) throws Exception {
		return ((ComputerProviderI)this.connector).deployVM(nbCores, app, RepartiteurURI, RepartiteurURIDCC) ;
	}

	@Override
	public boolean destroyVM(String mv) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<String> getListVM() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean getRequest(String mv, Request req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean reInit(String vm) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Integer availableCores() throws Exception {
		return ((ComputerProviderI)this.connector).availableCores() ;
	}

}
