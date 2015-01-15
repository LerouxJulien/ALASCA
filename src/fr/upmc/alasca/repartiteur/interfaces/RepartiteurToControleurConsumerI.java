package fr.upmc.alasca.repartiteur.interfaces;

import java.rmi.RemoteException;

import fr.upmc.alasca.computer.exceptions.BadDestroyException;
import fr.upmc.alasca.repartiteur.components.Repartiteur;
import fr.upmc.components.interfaces.RequiredI;

public interface RepartiteurToControleurConsumerI extends RequiredI{

	public void deployVM(int r, String[] uri, String RepartiteurURIDCC) throws Exception ;
	
	public void destroyVM(String uriComputerParent, String vm) throws Exception;
	
	public void incFrequency(int app)throws Exception;
	
}
