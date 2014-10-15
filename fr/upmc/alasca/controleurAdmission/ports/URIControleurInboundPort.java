package fr.upmc.alasca.controleurAdmission.ports;

import fr.upmc.alasca.controleurAdmission.components.Controleur;
import fr.upmc.alasca.controleurAdmission.interfaces.URISortieControleurI;
import fr.upmc.components.ComponentI;
import fr.upmc.components.ComponentI.ComponentService;
import fr.upmc.components.examples.basic_cs.components.URIProvider;
import fr.upmc.components.ports.AbstractInboundPort;

public class URIControleurInboundPort extends AbstractInboundPort implements URISortieControleurI {
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
	public				URIControleurInboundPort(
		String uri,
		ComponentI owner
		) throws Exception
	{
		// the implemented interface is statically known
		super(uri, URISortieControleurI.class, owner) ;

		assert	uri != null && owner instanceof URIProvider ;
	}

	/**
	 * calls the service method of the owner object by executing a task
	 * using one of the component's threads.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true				// no more preconditions.
	 * post	true				// no more postconditions.
	 * </pre>
	 * 
	 * @see fr.upmc.components.examples.basic_cs.interfaces.URIProviderI#provideURI()
	 */
	@Override
	public String		provideURI() throws Exception
	{
		// a final variable is useful to reference the owner in the method
		// call.
		final Controleur c = (Controleur) this.owner ;
		// the handleRequestSync wait for the result before retunring to the
		// caller; hence it is a synchronous remote method invocation.
		return c.handleRequestSync(
				new ComponentService<String>() {
					@Override
					public String call() throws Exception {
						return c.provideURIService() ;
					}
				}) ;
	}
}
