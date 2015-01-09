package fr.upmc.alasca.appgenerator.components;

import java.util.List;
import java.util.concurrent.Future;

import org.apache.commons.math3.random.RandomDataGenerator;

import fr.upmc.alasca.appgenerator.ports.AppGeneratorOutboundPort;
import fr.upmc.alasca.controleur.interfaces.AppRequestI;
import fr.upmc.alasca.requestgen.components.RequestGenerator;
import fr.upmc.alasca.requestgen.main.ClientArrivalConnector;
import fr.upmc.components.AbstractComponent;
import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.components.cvm.pre.dcc.DynamicComponentCreationConnector;
import fr.upmc.components.cvm.pre.dcc.DynamicComponentCreationI;
import fr.upmc.components.cvm.pre.dcc.DynamicComponentCreationOutboundPort;
import fr.upmc.components.cvm.pre.dcc.DynamicallyConnectableComponentConnector;
import fr.upmc.components.cvm.pre.dcc.DynamicallyConnectableComponentI;
import fr.upmc.components.cvm.pre.dcc.DynamicallyConnectableComponentOutboundPort;

public class ApplicationRequestGenerator extends AbstractComponent {

	List<Integer> appsToLaunch;

	// parametres utilises pour tous les RequestGenerator
	protected double meanInterArrivalTime;
	protected RandomDataGenerator rng;
	protected Future<?> nextRequestTaskFuture;
	protected int standardDeviation;
	protected int meanNumberInstructions;
	// FIN parametres utilises pour tous les RequestGenerator

	protected String baseURIRequestGenerator;

	// pour differencier leurs URIS
	protected Integer compteurDeRG;

	protected AppGeneratorOutboundPort arg_outboundPort;

	public ApplicationRequestGenerator(List<Integer> appsToLaunch,
			double meanInterArrivalTime, int meanNumberInstructions,
			int standardDeviation, String outboundPortURI) throws Exception {
		super(true);

		this.appsToLaunch = appsToLaunch;

		this.meanInterArrivalTime = meanInterArrivalTime;
		this.meanNumberInstructions = meanNumberInstructions;
		this.standardDeviation = standardDeviation;
		this.rng = new RandomDataGenerator();
		this.rng.reSeed();
		this.nextRequestTaskFuture = null;
		this.baseURIRequestGenerator = outboundPortURI;
		compteurDeRG = 0;

		// Component management
		this.addRequiredInterface(AppRequestI.class);
		this.arg_outboundPort = new AppGeneratorOutboundPort(outboundPortURI, this);
		this.addPort(this.arg_outboundPort);
		this.arg_outboundPort.publishPort();

		this.addRequiredInterface(DynamicComponentCreationI.class);
		this.addRequiredInterface(DynamicallyConnectableComponentI.class);

	}

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

	public void askNewApplication(Integer appId)
			throws Exception {
		DynamicComponentCreationOutboundPort dcco = new DynamicComponentCreationOutboundPort(
				this);
		dcco.publishPort();

		this.addPort(dcco);
		dcco.doConnection("request_generator_jvm_uri" //"controleur_jvm_uri" // TODO a changer
				+ AbstractCVM.DYNAMIC_COMPONENT_CREATOR_INBOUNDPORT_URI,
				DynamicComponentCreationConnector.class.getCanonicalName());

		String uriNewRequestGenerator = getUriNewRequestGenerator();

		dcco.createComponent(RequestGenerator.class.getCanonicalName(),
				new Object[] { this.meanInterArrivalTime,
						this.meanNumberInstructions, this.standardDeviation,
						appId, uriNewRequestGenerator });

		this.arg_outboundPort.acceptApplication(appId, uriNewRequestGenerator);
	}

	private String getUriNewRequestGenerator() {
		String uriNewRequestGenerator = this.baseURIRequestGenerator
				+ compteurDeRG;
		this.compteurDeRG++;
		return uriNewRequestGenerator;
	}
}
