package fr.upmc.alasca.requestgen.ports;

import fr.upmc.alasca.requestgen.interfaces.RequestArrivalI;
import fr.upmc.alasca.requestgen.objects.Request;
import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;

/**
 * The class <code>RequestGeneratorOutboundPort</code> implements the outbound
 * port for a component sending requests to another component.
 *
 * <p>
 * <strong>Description</strong>
 * </p>
 * 
 * The port implements the <code>RequestArrivalI</code> interface as required
 * and upon a call, passes it to the connector that must also implement the same
 * interface.
 * 
 * <p>
 * <strong>Invariant</strong>
 * </p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 * 
 * <p>
 * Created on : 2 sept. 2014
 * </p>
 * 
 * @author <a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version $Name$ -- $Revision$ -- $Date$
 */
public class RequestGeneratorOutboundPort extends AbstractOutboundPort
		implements RequestArrivalI {
	/**
	 * create the port with its URI and owner component.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	uri != null && owner != null &&
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param uri
	 * @param owner
	 * @throws Exception
	 */
	public RequestGeneratorOutboundPort(String uri, ComponentI owner)
			throws Exception {
		super(uri, RequestArrivalI.class, owner);

		assert uri != null;
		assert owner.isRequiredInterface(RequestArrivalI.class);
	}

	/**
	 * pass the request to the connector.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	true				// no more preconditions.
	 * post	true				// no more postconditions.
	 * </pre>
	 */
	@Override
	public void acceptRequest(Request r) throws Exception {
		((RequestArrivalI) this.connector).acceptRequest(r);
	}
}
