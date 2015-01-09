package fr.upmc.alasca.appgen.connectors;

import fr.upmc.alasca.appgen.interfaces.ApplicationGeneratorI;
import fr.upmc.components.connectors.AbstractConnector;

/**
 * Classe <code>ApplicationGeneratorConnector</code>
 *
 * <p>
 * <strong>Description</strong>
 * </p>
 * 
 * <p>La classe <code>ApplicationGeneratorConnector</code> impl�mente le
 * connecteur entre le port de sortie du g�n�rateur d'applications et le port
 * d'entr�e du contr�leur.</p>
 * 
 * <p>Created on : 23 dec. 2014</p>
 * 
 * @author <a href="mailto:Henri.Ng@etu.upmc.fr">Henri Ng/a>
 * @version $Name$ -- $Revision$ -- $Date$
 */
public class ApplicationGeneratorConnector extends AbstractConnector implements
	ApplicationGeneratorI {

	@Override
	public void acceptApplication(Integer appID) throws Exception {
		((ApplicationGeneratorI) this.offering).acceptApplication(appID);
	}
	
}
