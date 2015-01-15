package fr.upmc.alasca.controleur.interfaces;

import fr.upmc.components.interfaces.OfferedI;
import fr.upmc.components.interfaces.RequiredI;

/**
 *  Interface <code>AppRequestI</code> 
 *
 * <p>
 * <strong>Description</strong>
 * </p>
 * 
 * <p>L'interface <code>AppRequestI</code> d�finit le protocol pour envoyer une 
 * demande d'ex�cution d'application au contr�leur du centre de calcul.
 * L'interface ne contient qu'une seule m�thode, <code>acceptApplication</code> 
 * qui passe l'id d'une application en param�tre.</p>
 * 
 * <p>
 * Created on : 23 dec. 2014
 * </p>
 * 
 * @author <a href="mailto:Nicolas.Mounier@etu.upmc.fr">Nicolas Mounier/a>
 *         <a href="mailto:Henri.Ng@etu.upmc.fr">Henri Ng/a>
 * @version $Name$ -- $Revision$ -- $Date$
 */
public interface AppRequestI extends OfferedI, RequiredI {
	
	public void acceptApplication(Integer application, String thresholds,
			String uri_new_requestGenerator) throws Exception;
	
}
