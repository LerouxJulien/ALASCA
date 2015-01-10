package fr.upmc.alasca.controleur.interfaces;

import fr.upmc.components.interfaces.OfferedI;
import fr.upmc.components.interfaces.RequiredI;

/**
 * L'interface <code>AppRequestI</code> définit le protocol pour 
 * envoyer une demande d'exécution d'application au contrôleur du centre de 
 * calcul.
 *
 * <p>
 * <strong>Description</strong>
 * </p>
 * 
 * L'interface ne contient qu'une seule méthode, <code>acceptApplication</code> 
 * qui passe l'id d'une application en paramètre.
 * 
 * <p>
 * Created on : 23 dec. 2014
 * </p>
 * 
 * @author <a href="mailto:Nicolas.Mounier@etu.upmc.fr">Nicolas Mounier/a>
 *         <a href="mailto:Henri.Ng@etu.upmc.fr">Henri Ng/a>
 * @version $Name$ -- $Revision$ -- $Date$
 */
public interface AppRequestI extends OfferedI, RequiredI{
	
	public void acceptApplication(Integer application,
			String uri_new_requestGenerator) throws Exception;
	
}
