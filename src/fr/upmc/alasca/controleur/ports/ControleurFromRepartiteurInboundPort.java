package fr.upmc.alasca.controleur.ports;

import fr.upmc.alasca.controleur.components.Controleur;
import fr.upmc.alasca.controleur.interfaces.ControleurFromRepartiteurProviderI;
import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractInboundPort;

/**
 * Classe <code>ControleurFromRepartiteurInboundPort</code>
 *
 * <p>
 * <strong>Description</strong>
 * </p>
 * 
 * <p>La classe <code>ControleurFromRepartiteurInboundPort</code> implémente le 
 * port par lequel le répartiteur de requêtes demande au contrôleur le
 * déploiement ou la destruction de VM.
 * 
 * <p>Created on : 23 dec. 2014</p>
 * 
 * @author <a href="mailto:William.Chasson@etu.upmc.fr">William Chasson/a>
 *         <a href="mailto:Henri.Ng@etu.upmc.fr">Henri Ng/a>
 * @version $Name$ -- $Revision$ -- $Date$
 */
public class ControleurFromRepartiteurInboundPort extends AbstractInboundPort
implements ControleurFromRepartiteurProviderI{

	private static final long serialVersionUID = -4847181006642813480L;

	public ControleurFromRepartiteurInboundPort(String uri, ComponentI owner) 
			throws Exception {
		super(uri, ControleurFromRepartiteurProviderI.class, owner);
	}

	@Override
	public void deployVM(int r, String[] uri, String RepartiteurURIDCC) 
			throws Exception {
		final Controleur c = (Controleur) this.owner;
		c.deployVM(r, uri, RepartiteurURIDCC);
		
	}

	@Override
	public void destroyVM(String uriComputerParent, String vm) 
			throws Exception {
		final Controleur c = (Controleur) this.owner;
		c.destroyVM(uriComputerParent, vm);
	}
	
	@Override
	public void incFrequency(int app) throws Exception {
		final Controleur c = (Controleur) this.owner;
		c.incFrequency(app);
	}
	
}
