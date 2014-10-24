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
	 * create the port under some given URI and for a given owner.
	 * 
	 * The constructor for <code>AbstractInboundPort</code> requires the
	 * interface that the port is implementing as an instance of
	 * <code>java.lang.CLass</code>, but this is statically known so
	 * the constructor does not need to receive the information as parameter.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	uri != null && owner instanceof URIProvider
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param uri	uri under which the port will be published.
	 * @param owner	component owning the port.
	 * @throws Exception
	 */
	public URIControleurInboundPort(String uri, ComponentI owner) throws Exception
	{
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
