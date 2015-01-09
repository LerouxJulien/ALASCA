package fr.upmc.alasca.appgen.components;

import java.util.List;

import fr.upmc.alasca.appgen.interfaces.ApplicationGeneratorI;
import fr.upmc.alasca.appgen.ports.ApplicationGeneratorOutboundPort;
import fr.upmc.components.AbstractComponent;
import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.components.cvm.pre.dcc.DynamicComponentCreationI;
import fr.upmc.components.cvm.pre.dcc.DynamicallyConnectableComponentI;
import fr.upmc.components.exceptions.ComponentShutdownException;
import fr.upmc.components.exceptions.ComponentStartException;
import fr.upmc.components.ports.PortI;

/**
 * Classe <code>ApplicationGenerator</code>
 *
 * <p>
 * <strong>Description</strong>
 * </p>
 * 
 * La classe <code>ApplicationGenerator</code> permet de générer la liste 
 * d'applications à faire tourner par le centre de calcul.
 * 
 * <p>
 * Created on : 23 dec. 2014
 * </p>
 * 
 * @author <a href="mailto:Henri.Ng@etu.upmc.fr">Henri Ng/a>
 * @version $Name$ -- $Revision$ -- $Date$
 */
public class ApplicationGenerator extends AbstractComponent {
	
	protected static final String REQGEN_OUTBOUNDPORT_URI = "reqgen_o";
	
	// Liste des ID des applications à exécuter par le centre de calcul
	private final List<Integer> appID;
	
	// CVM dans lequel tourne le composant
	protected AbstractCVM cvm;
	
	// Port de sortie utilisé pour envoyer les ID des applications
	protected ApplicationGeneratorOutboundPort agport;

	public ApplicationGenerator(List<Integer> appID, String outboundPortURI,
			boolean isDistributed, AbstractCVM cvm) throws Exception {
		super(true, true);
		this.cvm   = cvm;
		this.appID = appID;
		
		this.addOfferedInterface(ApplicationGeneratorI.class);
		PortI p = new ApplicationGeneratorOutboundPort(outboundPortURI, this);
		this.addPort(p);
		if (isDistributed) {
			p.publishPort();
		} else {
			p.localPublishPort();
		}

		this.addRequiredInterface(DynamicComponentCreationI.class);
		this.addRequiredInterface(DynamicallyConnectableComponentI.class);
	}
	
	public void acceptApplication(Integer appID) throws Exception {
		this.agport.acceptApplication(appID);
	}

	@Override
	public void shutdown() throws ComponentShutdownException {
		super.shutdown();
	}
	
	@Override
	public void start() throws ComponentStartException {
		super.start();
	}
	
	/**
	 * Retourne la description du générateur d'application
	 * 
	 * @return string
	 */
	@Override
	public String toString() {
		return "ApplicationGenerator [appID=" + appID.toString() +
				", cvm=" + cvm + "]";
	}

}
