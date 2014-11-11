package fr.upmc.alasca.requestgen.interfaces;

import fr.upmc.alasca.requestgen.objects.Request;
import fr.upmc.components.interfaces.OfferedI;
import fr.upmc.components.interfaces.RequiredI;

/**
 * The interface <code>RequestArrivalI</code> defines the protocol to send a
 * request between an sender and a receiver
 *
 * <p>
 * <strong>Description</strong>
 * </p>
 * 
 * The interface can be both required and offered, and it has only one method
 * <code>acceptRequest</code> passing a request object as parameter.
 * 
 * <p>
 * Created on : 2 sept. 2014
 * </p>
 * 
 * @author <a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version $Name$ -- $Revision$ -- $Date$
 */
public interface RequestArrivalI extends OfferedI, RequiredI {
	/**
	 * accept a new request for servicing.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	r != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param r
	 *            request to be serviced.
	 * @throws Exception
	 */
	public void acceptRequest(Request r) throws Exception;
}
