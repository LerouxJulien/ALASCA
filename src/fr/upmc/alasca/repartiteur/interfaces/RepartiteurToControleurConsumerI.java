package fr.upmc.alasca.repartiteur.interfaces;

import fr.upmc.components.interfaces.RequiredI;

public interface RepartiteurToControleurConsumerI extends RequiredI{

	public void deployVM(int r, String[] uri, String RepartiteurURIDCC) throws Exception ;
	
	public void destroyVM(String uriComputerParent, String vm) throws Exception;
	
	public void incFrequency(int app)throws Exception;
	
}
