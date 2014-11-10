package fr.upmc.alasca.requestgen.main;

import fr.upmc.alasca.requestgen.interfaces.RequestArrivalI;
import fr.upmc.alasca.requestgen.objects.Request;
import fr.upmc.components.connectors.AbstractConnector;

/**
 * The class <code>ClientArrivalConnector</code> implements the connector
 * between the outbound port of a component sending requests with the inbound
 * port of another component servicing them.
 *
 * <p><strong>Description</strong></p>
 * 
 * Simply pass the request to the offering inbound port.
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 * 
 * <p>Created on : 3 sept. 2014</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public class			ClientArrivalConnector
extends		AbstractConnector
implements	RequestArrivalI
{
	/**
	 * @see fr.upmc.alasca.ssqueue.interfaces.RequestArrivalI#acceptRequest(fr.upmc.alasca.ssqueue.objects.Request)
	 */
	@Override
	public void			acceptRequest(Request c) throws Exception
	{
		((RequestArrivalI)this.offering).acceptRequest(c) ;
	}
}
