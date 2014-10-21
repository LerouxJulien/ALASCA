package fr.upmc.alasca.controleurAdmission;

import fr.upmc.alasca.controleurAdmission.interfaces.ControleurConsumerClientI;
import fr.upmc.alasca.requestgen.interfaces.RequestArrivalI;
import fr.upmc.alasca.requestgen.objects.Request;
import fr.upmc.components.connectors.AbstractConnector;

/**
 * La classe <code>ControleurClientConnector</code>
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

public class ControleurClientConnector extends AbstractConnector implements ControleurConsumerClientI{

	public Request getRequest() throws Exception {
		return ((RequestArrivalI) this.offering).sendRequest();
	}
	
}
