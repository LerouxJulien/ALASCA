package fr.upmc.alasca.controleur.ports;



import fr.upmc.alasca.controleur.components.Controleur;
import fr.upmc.alasca.controleur.interfaces.ControleurToRepartiteurProviderI;
import fr.upmc.alasca.repartiteur.components.Repartiteur;
import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractInboundPort;

public class ControleurToRepartiteurInboundPort extends AbstractInboundPort implements ControleurToRepartiteurProviderI{

	

	/**
	 * 
	 */
	private static final long serialVersionUID = -4847181006642813480L;

	
	
	public ControleurToRepartiteurInboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, ControleurToRepartiteurProviderI.class, owner);
	}

	
	@Override
	public void deployVM(int r, String[] uri, String RepartiteurURIDCC) throws Exception {
		final Controleur c = (Controleur) this.owner;
		c.deployVM(r, uri, RepartiteurURIDCC);
		
	}

}
