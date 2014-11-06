package fr.upmc.alasca.computer.main;

import java.util.List;

import fr.upmc.alasca.computer.interfaces.ComputerProviderI;
import fr.upmc.alasca.requestgen.objects.Request;
import fr.upmc.components.connectors.AbstractConnector;

public class ComputerConnector 
extends		AbstractConnector
implements	ComputerProviderI{

	
	@Override
	public boolean destroyVM(String mv) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean deployVM(int nbCores, int app, String RepartiteurURI,
			String RepartiteurURIDCC) throws Exception {
		return ((ComputerProviderI)this.offering).deployVM(nbCores, app, RepartiteurURI,RepartiteurURIDCC);
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
		return ((ComputerProviderI)this.offering).availableCores();
	}

}
