package fr.upmc.alasca.controleur.interfaces;

import fr.upmc.components.interfaces.OfferedI;

/**
 * Interface <code>ControleurFromRepartiteurProviderI</code>
 *
 * <p>
 * <strong>Description</strong>
 * </p>
 * 
 * <p>La classe <code>ControleurFromRepartiteurProviderI</code> représente les
 * services offertes (déploiement et destruction de VM) par le contrôleur aux 
 * répartiteurs de requêtes.</p>
 * 
 * <p>Created on : 23 dec. 2014</p>
 * 
 * @author <a href="mailto:Nicolas.Mounier@etu.upmc.fr">Nicolas Mounier/a>
 *         <a href="mailto:Henri.Ng@etu.upmc.fr">Henri Ng/a>
 * @version $Name$ -- $Revision$ -- $Date$
 */
public interface ControleurFromRepartiteurProviderI extends OfferedI {

	/**
	 * Déploie une VM sur un Computer s'il y a de la place
	 *
	 * @param r Répartiteur connecté à la VM deployée
	 * @param repartiteurURIFixe URI du port sur lequel le répartiteur transmet 
	 * 							 les requêtes
	 * @return true Si une vm a effectivement été deployée
	 * @throws Exception
	 */
	public void deployVM(int r, String[] uri, String RepartiteurURIDCC) 
			throws Exception;
	
	/**
	 * Détruite une VM à partir de son URI et de celui de son parent
	 * 
	 * @param uriComputerParent
	 * @param mv
	 * @throws Exception
	 */
	public void destroyVM(String uriComputerParent, String vm) throws Exception;
	
	/**
	 * Incrémente la fréquence des coeurs d'une VM pour une application donnée
	 * 
	 * @param app
	 * @throws Exception
	 */
	public void incFrequency(int app) throws Exception;
	
	/**
	 * Initialise une VM via son URI en lui associant le répartiteur de
	 * l'application donnée
	 * 
	 * @param appID
	 * @param uriComputerParent
	 * @param vm
	 * @throws Exception
	 */
	public void initVM(int appID, String uriComputerParent, String vm) 
			throws Exception;

	/**
	 * Réinitialise une VM via son URI en lui désassociant le répartiteur de
	 * l'application donnée
	 * 
	 * @param uriComputerParent
	 * @param vm
	 * @throws Exception
	 */
	public void reInitVM(String uriComputerParent, String vm) throws Exception;
	
}
