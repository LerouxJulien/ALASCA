package fr.upmc.alasca.controleur.interfaces;

import fr.upmc.alasca.computer.components.VirtualMachine;
import fr.upmc.alasca.controleur.components.Controleur;
import fr.upmc.alasca.repartiteur.components.Repartiteur;
import fr.upmc.components.interfaces.OfferedI;

public interface ControleurToRepartiteurProviderI extends OfferedI{

	
	public void deployVM(int r, String[] uri,String RepartiteurURIDCC) throws Exception;
	
	
}
