package fr.upmc.alasca.repartiteur.interfaces;

import fr.upmc.alasca.repartiteur.components.Repartiteur;
import fr.upmc.components.interfaces.RequiredI;

public interface RepartiteurToControleurConsumerI extends RequiredI{

	public void deployVM(int r, String[] uri, String RepartiteurURIDCC) throws Exception ;
	
	
}
