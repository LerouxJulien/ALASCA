package fr.upmc.alasca.computer.interfaces;

import fr.upmc.components.interfaces.OfferedI;
import fr.upmc.components.interfaces.RequiredI;


public interface RefreshVMI extends OfferedI, RequiredI{
	
	public void refreshVM(double freq) throws Exception;
}
