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

	public void deployVM(int r, String[] uri, String RepartiteurURIDCC) 
			throws Exception;
	
	public void destroyVM(String uriComputerParent, String vm) throws Exception;
	
}
