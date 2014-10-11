package fr.upmc.alasca.computer.interfaces;

import java.util.List;

import fr.upmc.alasca.requestgen.objects.Request;
import fr.upmc.alasca.computer.objects.VirtualMachine;

public interface ComputerProviderI {
	
	// Fonctions offertes
	
	// Envoie la liste des machines virtuelles allou�es
	public List<VirtualMachine> getListMV();
	
	// Allouer une machine virtuelle
	public boolean deployMV(int nbCores, String app);
	
	// D�truire une machine virtuelle
	public boolean destroyMV(VirtualMachine mv);
	
	// R�initialiser une machine virtuelle
	public boolean reInit(VirtualMachine mv);
	
	// Recuperer une requete envoyee par le repartiteur de requetes
	public boolean getRequest(VirtualMachine mv, Request req);

}
