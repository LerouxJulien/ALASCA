package fr.upmc.alasca.controleurAdmission.interfaces;

import fr.upmc.alasca.requestgen.objects.Request;
import fr.upmc.components.interfaces.RequiredI;

/**
 * L'interface <code>ControleurConsumerClientI</code>
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

public interface ControleurConsumerClientI extends RequiredI {
	
	public Request getRequest() throws Exception;
}
