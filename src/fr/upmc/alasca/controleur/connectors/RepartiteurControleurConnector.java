package fr.upmc.alasca.controleur.connectors;

import java.io.Serializable;
import fr.upmc.alasca.controleur.interfaces.ControleurFromRepartiteurProviderI;
import fr.upmc.components.connectors.AbstractConnector;

/**
 * Classe <code>RepartiteurControleurConnector</code>
 *
 * <p>
 * <strong>Description</strong>
 * </p>
 * 
 * La classe <code>RepartiteurControleurConnector</code> permet de connecter les
 * répartiteurs de requêtes et le contrôleur.
 * 
 * <p>
 * Created on : 23 dec. 2014
 * </p>
 * 
 * @author <a href="mailto:Henri.Ng@etu.upmc.fr">Henri Ng/a>
 * @version $Name$ -- $Revision$ -- $Date$
 */
public class RepartiteurControleurConnector extends AbstractConnector implements
ControleurFromRepartiteurProviderI, Serializable {
	
	private static final long serialVersionUID = -5590289981158924917L;

	@Override
	public void deployVM(int r, String[] uri, String RepartiteurURIDCC) 
			throws Exception {
		((ControleurFromRepartiteurProviderI)this.offering).
		deployVM(r, uri, RepartiteurURIDCC);
		
	}
	
	@Override
	public void destroyVM(String uriComputerParent, String vm) 
			throws Exception {
		((ControleurFromRepartiteurProviderI)this.offering).
		destroyVM(uriComputerParent, vm);
	}
	
}
