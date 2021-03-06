package fr.upmc.alasca.requestgen.components;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.random.RandomDataGenerator;

import fr.upmc.alasca.requestgen.interfaces.RequestArrivalI;
import fr.upmc.alasca.requestgen.objects.Request;
import fr.upmc.alasca.requestgen.ports.RequestGeneratorOutboundPort;
import fr.upmc.alasca.requestgen.utils.TimeProcessing;
import fr.upmc.components.AbstractComponent;
import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.components.cvm.pre.dcc.DynamicComponentCreationI;
import fr.upmc.components.cvm.pre.dcc.DynamicallyConnectableComponentI;
import fr.upmc.components.cvm.pre.dcc.DynamicallyConnectableComponentInboundPort;
import fr.upmc.components.cvm.pre.dcc.DynamicallyConnectableI;
import fr.upmc.components.exceptions.ComponentShutdownException;
import fr.upmc.components.exceptions.ComponentStartException;
import fr.upmc.components.ports.PortI;

/**
 * The class <code>RequestGenerator</code> implements a component that generates
 * requests for a service provider in a discrete-event based simulation.
 *
 * <p>
 * <strong>Description</strong>
 * </p>
 *
 * A request has a processing time and an arrival process that both follow an
 * exponential probability distribution. The generation process is started by
 * executing the method <code>generateNextRequest</code> as a component task. It
 * generates an instance of the class <code>Request</code>, with a processing
 * time generated from its exponential distribution, and then schedule its next
 * run after the interarrival time also generated from its exponential
 * distribution. To stop the generation process, the method
 * <code>shutdown</code> uses the future returned when scheduling the next
 * request generation to cancel its execution.
 * 
 * <p>
 * <strong>Invariant</strong>
 * </p>
 * 
 * <pre>
 * invariant	rng != null && counter >= 0
 * invariant	meanInterArrivalTime > 0.0 && meanProcessingTime > 0.0
 * invariant	rgop != null && rgop instanceof RequestArrivalI
 * </pre>
 * 
 * <p>
 * Created on : 2 sept. 2014
 * </p>
 * 
 * @author <a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class RequestGenerator extends AbstractComponent implements	DynamicallyConnectableI{
	// -------------------------------------------------------------------------
	// Constructors and instance variables
	// -------------------------------------------------------------------------

	/** a random number generator used to generate processing times. */
	protected NormalDistribution nd;
	/** a counter used to generate request URI. */
	protected int counter;
	/** the mean interarrival time of requests in ms. */
	protected double meanInterArrivalTime;
	/** the mean processing time of requests in ms. */
	protected double meanProcessingTime;

	protected RandomDataGenerator rng;

	protected int meanNumberInstructions;

	/** the output port used to send requests to the service provider. */
	protected RequestGeneratorOutboundPort rgop;
	/** a future pointing to the next request generation task. */
	protected Future<?> nextRequestTaskFuture;

	protected Integer appId;
	
	protected DynamicallyConnectableComponentInboundPort dccInboundPort;
	
	/**
	 * create a request generator component.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	meanInterArrivalTime > 0.0 && meanProcessingTime > 0.0
	 * pre	outboundPortURI != null
	 * </pre>
	 *
	 * @param meanInterArrivalTime
	 *            mean interarrival time of the requests in ms
	 * @param meanNumberInstructions
	 *            mean number of instructions
	 * @param outboundPortURI
	 *            URI of the outbound port to connect to the service provider
	 * @param numberAppLaunched
	 *            numeros des applications lancees
	 * @throws Exception
	 */
	public RequestGenerator(Double meanInterArrivalTime,
			Integer meanNumberInstructions, Integer standardDeviation,
			Integer appId,
			String outboundPortURI)
			throws Exception {
		super(true, true);

		assert meanInterArrivalTime > 0.0 && meanProcessingTime > 0.0;
		assert outboundPortURI != null;

		this.counter = 0;
		this.meanInterArrivalTime = meanInterArrivalTime;
		this.nd = new NormalDistribution(meanNumberInstructions,
				standardDeviation);
		this.rng = new RandomDataGenerator();
		this.rng.reSeed();
		this.nextRequestTaskFuture = null;
		this.appId = appId;
		
		this.dccInboundPort = new DynamicallyConnectableComponentInboundPort(
				outboundPortURI + "-dcc", this);
		if (AbstractCVM.isDistributed) {
			this.dccInboundPort.publishPort();
		} else {
			this.dccInboundPort.localPublishPort();
		}
		this.addPort(dccInboundPort);

		// Component management
		this.addRequiredInterface(RequestArrivalI.class);
		this.rgop = new RequestGeneratorOutboundPort(outboundPortURI, this);
		this.addPort(this.rgop);
		if (AbstractCVM.isDistributed) {
			this.rgop.publishPort();
		} else {
			this.rgop.localPublishPort();
		}
		
		this.addRequiredInterface(DynamicComponentCreationI.class);
		this.addRequiredInterface(DynamicallyConnectableComponentI.class);

		assert nd != null && counter >= 0;
		assert meanInterArrivalTime > 0.0;
		assert rgop != null && rgop instanceof RequestArrivalI;
	}

	// -------------------------------------------------------------------------
	// Component life-cycle
	// -------------------------------------------------------------------------

	/**
	 * shut down the component, first cancelling any future request generation
	 * already scheduled.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	true				// no more preconditions.
	 * post	true				// no more postconditions.
	 * </pre>
	 * 
	 * @see fr.upmc.components.AbstractComponent#shutdown()
	 */
	@Override
	public void shutdown() throws ComponentShutdownException {
		if (this.nextRequestTaskFuture != null
				&& !(this.nextRequestTaskFuture.isCancelled() || this.nextRequestTaskFuture
						.isDone())) {
			this.nextRequestTaskFuture.cancel(true);
		}
		super.shutdown();
	}

	// -------------------------------------------------------------------------
	// Component internal services
	// -------------------------------------------------------------------------

	/**
	 * generate a new request with some processing time following an exponential
	 * distribution and then schedule the next request generation in a delay
	 * also following an exponential distribution.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @throws Exception
	 */
	public void generateNextRequest() throws Exception {
		long instructions = (long) nd.sample();
		this.rgop.acceptRequest(new Request(this.counter++, instructions,
				appId));
		final RequestGenerator cg = this;
		long interArrivalDelay = (long) this.rng
				.nextExponential(this.meanInterArrivalTime);
		System.out.println("Scheduling request at "
				+ TimeProcessing.toString(System.currentTimeMillis()
						+ interArrivalDelay) + " with " + instructions
				+ " instructions");
		this.nextRequestTaskFuture = this.scheduleTask(new ComponentTask() {
			@Override
			public void run() {
				try {
					cg.generateNextRequest();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, interArrivalDelay, TimeUnit.MILLISECONDS);
	}

	@Override
	public void start() throws ComponentStartException {
		super.start();
		final RequestGenerator rg = this;
		// Schedule the first service method invocation in one second.
		this.scheduleTask(new ComponentTask() {
			@Override
			public void run() {
				try {
					rg.generateNextRequest();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, 1000, TimeUnit.MILLISECONDS);
	}

	@Override
	public void connectWith(String serverPortURI, String clientPortURI,
			String ccname) throws Exception {
		PortI uriConsumerPort = this.findPortFromURI(clientPortURI) ;
		uriConsumerPort.doConnection(serverPortURI, ccname) ;
	}

	@Override
	public void disconnectWith(String serverPortURI, String clientPortURI)
			throws Exception {
		PortI uriConsumerPort = this.findPortFromURI(clientPortURI) ;
		uriConsumerPort.doDisconnection() ;
	}
}
