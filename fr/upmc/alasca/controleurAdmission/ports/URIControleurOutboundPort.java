package fr.upmc.alasca.controleurAdmission.ports;

import fr.upmc.alasca.controleurAdmission.interfaces.URIEntreeControleurI;
import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;

public class URIControleurOutboundPort extends AbstractOutboundPort implements URIEntreeControleurI {
	/**
	 * create the port with the given URI and the given owner.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param uri	URI of the port.
	 * @param owner	owner of the port.
	 */
	public				URIControleurOutboundPort(
		String uri,
		ComponentI owner
		) throws Exception
	{
		super(uri, URIEntreeControleurI.class, owner) ;
	}

	/**
	 * get an URI by calling the server component through the connector that
	 * implements the required interface.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true				// no more preconditions.
	 * post	true				// no more postconditions.
	 * </pre>
	 * 
	 * @see fr.upmc.components.examples.basic_cs.interfaces.URIConsumerI#getURI()
	 */
	@Override
	public String		getURI() throws Exception
	{
		return ((URIEntreeControleurI)this.connector).getURI() ;
	}
}
