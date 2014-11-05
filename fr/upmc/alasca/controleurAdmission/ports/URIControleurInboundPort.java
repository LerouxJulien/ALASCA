package fr.upmc.alasca.controleurAdmission.ports;

import fr.upmc.alasca.controleurAdmission.components.Controleur;
import fr.upmc.alasca.controleurAdmission.interfaces.ControleurProviderClientI;
import fr.upmc.alasca.requestgen.objects.Request;
import fr.upmc.components.ComponentI;
import fr.upmc.components.examples.basic_cs.components.URIProvider;
import fr.upmc.components.ports.AbstractInboundPort;

public class URIControleurInboundPort extends AbstractInboundPort implements ControleurProviderClientI {
	/** required by UnicastRemonteObject.									*/
	private static final long serialVersionUID = 1L;

	/**
	 * La classe <code>URIControleurInboundPort</code> créer un port avec un URI et
	 * un composant <code>Controleur</code> et fournit les fonctions définit dans 
	 * l'interface offerte <code>ControleurProviderClientI</code>
	 *
	 * <p><strong>Description</strong></p>
	 * 
	 * <p><strong>Invariant</strong></p>
	 * 
	 * <pre>
	 * invariant	true
	 * </pre>
	 * 
	 * <p>Created on : 10 oct. 2014</p>
	 * 
	 * @author	<a href="mailto:william.chasson@etu.upmc.fr">William CHASSON</a>
	 * @version	$Name$ -- $Revision$ -- $Date$
	 */
	public URIControleurInboundPort(String uri, ComponentI owner) throws Exception {
		// the implemented interface is statically known
		super(uri, ControleurProviderClientI.class, owner);

		assert	uri != null && owner instanceof URIProvider;
	}

	@Override
	public void acceptRequest(Request r) throws Exception {
		((Controleur) this.owner).transfertRequeteDispatcher(r);
	}

	@Override
	public void acceptApplication(int id) throws Exception {
		((Controleur) this.owner).transfertNouvelleApplication(id);
	}

}
