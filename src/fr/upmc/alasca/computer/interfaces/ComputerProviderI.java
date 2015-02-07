package fr.upmc.alasca.computer.interfaces;

import java.rmi.RemoteException;

import fr.upmc.alasca.computer.exceptions.BadDestroyException;
import fr.upmc.alasca.computer.exceptions.BadReinitialisationException;
import fr.upmc.components.interfaces.OfferedI;

/**
 * L'interface <code>ComputerProviderI</code>, offre des fonctions du composant <code>Computer</code>
 * 
 * <p>
 * Created on : 10 oct. 2014
 * </p>
 * 
 * @author <a href="mailto:henri.ng@etu.upmc.fr">Henri NG</a>
 */
public interface ComputerProviderI extends OfferedI {

	/**
	 * Retourne le nombre de coeurs de Computer qui ne sont pas utilises par
	 * une machine virtuelle
	 * 
	 * @return nbCoreFree
	 * @throws Exception
	 */
	public Integer availableCores() throws Exception;
	
	/**
	 * Deploie une machine virtuelle et la connecte au repartiteur de requete de
	 * l'application
	 * 
	 * @param nbCores Nombre de coeurs attribues � la machine virtuelle
	 * @param app ID de l'application
	 * @param URIRepartiteurFixe URI du port dans Repartiteur
	 * @param URIRepartiteurDCC URI du dcc dans Repartiteur
	 * @throws Exception
	 */
	public void deployVM(int nbCores, int app, String[] URIRepartiteurFixe,
			String URIRepartiteurDCC) throws Exception;

	/**
	 * Detruit une machine virtuelle via son URI
	 * 
	 * @param mv l'URI de la VM
	 * @throws BadDestroyException
	 * @throws RemoteException 
	 * @throws Exception
	 */
	public void destroyVM(String mv) throws BadDestroyException, RemoteException, Exception;

	public boolean isMaxed(int appid) throws Exception;	
	
	public void incFrequency(int appid) throws Exception;
	
	/**
	 * Initialise une VM via son URI en lui associant le r�partiteur de
	 * l'application donn�e
	 * 
	 * @param appID
	 * @param vm
	 * @throws Exception
	 */
	public void initVM(int appID, String vm) throws RemoteException, Exception;

	/**
	 * R�initialise une VM via son URI en lui d�sassociant le r�partiteur de
	 * l'application donn�e
	 * 
	 * @param vm
	 * @throws BadReinitialisationException
	 * @throws RemoteException
	 * @throws Exception
	 */
	public void reInitVM(String vm) throws BadReinitialisationException,
		RemoteException, Exception;
	
}
