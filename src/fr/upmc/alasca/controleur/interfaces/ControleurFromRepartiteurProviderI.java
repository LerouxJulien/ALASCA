package fr.upmc.alasca.controleur.interfaces;

import fr.upmc.components.interfaces.OfferedI;

/**
 * Interface <code>ControleurFromRepartiteurProviderI</code>
 *
 * <p>
 * <strong>Description</strong>
 * </p>
 * 
 * <p>La classe <code>ControleurFromRepartiteurProviderI</code> repr�sente les
 * services offertes (d�ploiement et destruction de VM) par le contr�leur aux 
 * r�partiteurs de requ�tes.</p>
 * 
 * <p>Created on : 23 dec. 2014</p>
 * 
 * @author <a href="mailto:Nicolas.Mounier@etu.upmc.fr">Nicolas Mounier/a>
 *         <a href="mailto:Henri.Ng@etu.upmc.fr">Henri Ng/a>
 * @version $Name$ -- $Revision$ -- $Date$
 */
public interface ControleurFromRepartiteurProviderI extends OfferedI {

	/**
	 * D�ploie une VM sur un Computer s'il y a de la place
	 *
	 * @param r R�partiteur connect� � la VM deploy�e
	 * @param repartiteurURIFixe URI du port sur lequel le r�partiteur transmet 
	 * 							 les requ�tes
	 * @return true Si une vm a effectivement �t� deploy�e
	 * @throws Exception
	 */
	public void deployVM(int r, String[] uri, String RepartiteurURIDCC) 
			throws Exception;
	
	/**
	 * D�truite une VM � partir de son URI et de celui de son parent
	 * 
	 * @param uriComputerParent
	 * @param mv
	 * @throws Exception
	 */
	public void destroyVM(String uriComputerParent, String vm) throws Exception;
	
	/**
	 * Incr�mente la fr�quence des coeurs d'une VM pour une application donn�e
	 * 
	 * @param app
	 * @throws Exception
	 */
	public void incFrequency(int app) throws Exception;
	
	/**
	 * Initialise une VM via son URI en lui associant le r�partiteur de
	 * l'application donn�e
	 * 
	 * @param appID
	 * @param uriComputerParent
	 * @param vm
	 * @throws Exception
	 */
	public void initVM(int appID, String uriComputerParent, String vm) 
			throws Exception;

	/**
	 * R�initialise une VM via son URI en lui d�sassociant le r�partiteur de
	 * l'application donn�e
	 * 
	 * @param uriComputerParent
	 * @param vm
	 * @throws Exception
	 */
	public void reInitVM(String uriComputerParent, String vm) throws Exception;
	
}
