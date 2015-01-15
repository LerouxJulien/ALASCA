package fr.upmc.alasca.appgenerator.components;

import java.util.List;
import java.util.concurrent.Future;

import org.apache.commons.math3.random.RandomDataGenerator;

import fr.upmc.alasca.appgenerator.ports.AppGeneratorOutboundPort;
import fr.upmc.alasca.controleur.interfaces.AppRequestI;
import fr.upmc.alasca.requestgen.components.RequestGenerator;
import fr.upmc.components.AbstractComponent;
import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.components.cvm.pre.dcc.DynamicComponentCreationConnector;
import fr.upmc.components.cvm.pre.dcc.DynamicComponentCreationI;
import fr.upmc.components.cvm.pre.dcc.DynamicComponentCreationOutboundPort;
import fr.upmc.components.cvm.pre.dcc.DynamicallyConnectableComponentI;

/**
 * Classe <code>ApplicationRequestGenerator</code>
 *
 * <p>
 * <strong>Description</strong>
 * </p>
 * 
 * La classe <code>ApplicationRequestGenerator</code> permet d'envoyer la liste 
 * des applications � faire tourner par le centre de calcul et de g�n�rer les
 * <code>RequestGenerator</code> ou g�n�rateurs de requ�tes correspondants.
 * 
 * <p>
 * Created on : 23 dec. 2014
 * </p>
 * 
 * @author <a href="mailto:Nicolas.Mounier@etu.upmc.fr">Nicolas Mounier/a>
 *         <a href="mailto:Henri.Ng@etu.upmc.fr">Henri Ng/a>
 * @version $Name$ -- $Revision$ -- $Date$
 */
public class ApplicationRequestGenerator extends AbstractComponent {

	// Liste des applications � lancer
	List<Integer> appsToLaunch;

	// Param�tres utilis�s pour tous les g�n�rateurs de requ�tes
	protected double meanInterArrivalTime;
	protected RandomDataGenerator rng;
	protected Future<?> nextRequestTaskFuture;
	protected int standardDeviation;
	protected int meanNumberInstructions;
	protected String thresholds;
	protected String baseURIRequestGenerator;

	// Suffixe pour diff�rencier l'URI des g�n�rateurs de requ�tes
	protected Integer compteurDeRG;

	// Port de sortie des g�n�rateurs de requ�tes
	protected AppGeneratorOutboundPort arg_outboundPort;

	/**
	 * Constructeur du g�n�rateur d'applications
	 * 
	 * @param appsToLaunch
	 * @param meanInterArrivalTime
	 * @param meanNumberInstructions
	 * @param standardDeviation
	 * @param thresholds
	 * @param outboundPortURI
	 * @throws Exception
	 */
	public ApplicationRequestGenerator(List<Integer> appsToLaunch,
			double meanInterArrivalTime, int meanNumberInstructions,
			int standardDeviation, String thresholds,
			String outboundPortURI) throws Exception {
		super(true);

		this.appsToLaunch = appsToLaunch;
		this.meanInterArrivalTime = meanInterArrivalTime;
		this.meanNumberInstructions = meanNumberInstructions;
		this.standardDeviation = standardDeviation;
		this.thresholds = thresholds;
		this.rng = new RandomDataGenerator();
		this.rng.reSeed();
		this.nextRequestTaskFuture = null;
		this.baseURIRequestGenerator = outboundPortURI;
		this.compteurDeRG = 0;

		// Component management
		this.addRequiredInterface(AppRequestI.class);
		this.arg_outboundPort = new AppGeneratorOutboundPort(outboundPortURI, this);
		this.addPort(this.arg_outboundPort);
		this.arg_outboundPort.publishPort();

		this.addRequiredInterface(DynamicComponentCreationI.class);
		this.addRequiredInterface(DynamicallyConnectableComponentI.class);
	}

	/**
	 * R�cup�re la prochaine demande d'application � lancer
	 * 
	 * @throws Exception
	 */
	public void generateNextRequest() throws Exception {
		//while (!appsToLaunch.isEmpty()) {
		//	this.arg_outboundPort.acceptApplication(appsToLaunch.remove(0));
		this.askNewApplication(appsToLaunch.remove(0));
		/*	long interArrivalDelay = (long) this.rng
					.nextExponential(this.meanInterArrivalTime);
			this.nextRequestTaskFuture = this.scheduleTask(new ComponentTask() {
				@Override
				public void run() {
					try {

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}, interArrivalDelay, TimeUnit.MILLISECONDS);
		}*/
		// this.shutdown();
	}

	/**
	 * Demande le traitement des requ�tes de l'application donn�e en ID
	 * 
	 * @param appId
	 * @throws Exception
	 */
	public void askNewApplication(Integer appId)
			throws Exception {
		DynamicComponentCreationOutboundPort dcco =
				new DynamicComponentCreationOutboundPort(this);
		dcco.publishPort();
		
		this.addPort(dcco);
		// TODO A changer
//		dcco.doConnection("controleur_jvm_uri"
//				+ AbstractCVM.DYNAMIC_COMPONENT_CREATOR_INBOUNDPORT_URI,
//				DynamicComponentCreationConnector.class.getCanonicalName());
		dcco.doConnection("request_generator_jvm_uri"
				+ AbstractCVM.DYNAMIC_COMPONENT_CREATOR_INBOUNDPORT_URI,
				DynamicComponentCreationConnector.class.getCanonicalName());

		String uriNewRequestGenerator = getUriNewRequestGenerator();

		// Cr�ation du nouveau g�n�rateur de requ�tes
		dcco.createComponent(RequestGenerator.class.getCanonicalName(),
				new Object[] { this.meanInterArrivalTime,
						this.meanNumberInstructions, this.standardDeviation,
						appId, uriNewRequestGenerator });

		this.arg_outboundPort.acceptApplication(appId, thresholds,
				uriNewRequestGenerator);
	}

	/**
	 * R�cup�re l'URI du nouveau g�n�rateur de requ�tes
	 * 
	 * @return uriNewRequestGenerator
	 */
	private String getUriNewRequestGenerator() {
		String uriNewRequestGenerator =
				this.baseURIRequestGenerator + compteurDeRG;
		this.compteurDeRG++;
		return uriNewRequestGenerator;
	}
	
}
