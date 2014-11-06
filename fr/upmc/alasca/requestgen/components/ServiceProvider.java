package fr.upmc.alasca.requestgen.components;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import fr.upmc.alasca.requestgen.interfaces.RequestArrivalI;
import fr.upmc.alasca.requestgen.objects.Request;
import fr.upmc.alasca.requestgen.ports.RequestArrivalInboundPort;
import fr.upmc.alasca.requestgen.utils.TimeProcessing;
import fr.upmc.components.AbstractComponent;
import fr.upmc.components.exceptions.ComponentShutdownException;
import fr.upmc.components.ports.PortI;

/**
 * The class <code>ServiceProvider</code> implements a component that simulate
 * a single queue single server service provider.
 *
 * <p><strong>Description</strong></p>
 * 
 * The service provider component receives requests to be serviced through an
 * inbound port implementing the interface <code>RequestArrivalI</code>.  The
 * discrete-event simulation is based upon three kinds of events:
 * 
 * <ol>
 * <li>request arrival, upon which the request is queued, and then if the
 *   server is idle, a begin request processing event is immediately executed;
 *   </li>
 * <li>begin request processing, upon which a end request processing event is
 *   scheduled after a delay given by the request processing time; and,</li>
 * <li>end request processing, upon which if the queue is not empty a begin
 *   request processing event is immediately executed</li>
 * </ol>
 * 
 * Total service times (waiting + processing) of requests is accumulated in the
 * variable <code>totalServicingTime</code> while the number of serviced
 * requests is accumulated in the variable
 * <code>totalNumberOfServicedRequests</code>.  When the component is shut down,
 * any end processing event already scheduled is cancelled, and the component
 * outputs the average service time of the completely serviced requests.
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	serverIdle => (servicing == null && nextEndServicingTaskFuture == null)
 * invariant	!serverIdle => (servicing != null && nextEndServicingTaskFuture != null)
 * invariant	totalServicingTime >= 0 && totalNumberOfServicedRequests >= 0
 * </pre>
 * 
 * <p>Created on : 2 sept. 2014</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public class			ServiceProvider
extends		AbstractComponent
{
	// -------------------------------------------------------------------------
	// Constructors and instance variables
	// -------------------------------------------------------------------------

	/** true if the server is idle otherwise false.							*/
	protected boolean					serverIdle ;
	/** request currently being serviced, null if any.						*/
	protected Request					servicing ;
	/** queue of pending requests.											*/
	protected BlockingQueue<Request>	requestsQueue ;
	/** sum of the service time of all completed requets.					*/
	protected long						totalServicingTime ;
	/** total number of completely serviced requests.						*/
	protected int						totalNumberOfServicedRequests ;

	/** a future pointing to the next end servicing task.					*/
	protected Future<?>					nextEndServicingTaskFuture ;

	/**
	 * create a service provider.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param inboundPortURI	URI of the port used to received requests.
	 * @param isDistributed		true if the component is distributed.
	 * @throws Exception
	 */
	public				ServiceProvider(
		String inboundPortURI,
		boolean isDistributed
		) throws Exception
	{
		super(true, true) ;
		this.serverIdle = true ;
		this.servicing = null ;
		this.requestsQueue = new LinkedBlockingQueue<Request>() ;
		this.totalServicingTime = 0L ;
		this.totalNumberOfServicedRequests = 0 ;
		this.nextEndServicingTaskFuture = null ;

		this.addOfferedInterface(RequestArrivalI.class) ;
		PortI p = new RequestArrivalInboundPort(inboundPortURI, this) ;
		this.addPort(p) ;
		if (isDistributed) {
			p.publishPort() ;
		} else {
			p.localPublishPort() ;
		}

		assert	!serverIdle || (servicing == null && nextEndServicingTaskFuture == null) ;
		assert	serverIdle || (servicing != null && nextEndServicingTaskFuture != null) ;
		assert	totalServicingTime >= 0 && totalNumberOfServicedRequests >= 0 ;
	}

	// -------------------------------------------------------------------------
	// Component life-cycle
	// -------------------------------------------------------------------------

	/**
	 * shut down the component after canceling any pending end request
	 * processing task, and output the average service time of the completed
	 * service requests.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true				// no more preconditions.
	 * post	true				// no more postconditions.
	 * </pre>
	 * 
	 * @see fr.upmc.components.AbstractComponent#shutdown()
	 */
	@Override
	public void			shutdown() throws ComponentShutdownException
	{
		if (this.nextEndServicingTaskFuture != null &&
							!(this.nextEndServicingTaskFuture.isCancelled() ||
							  this.nextEndServicingTaskFuture.isDone())) {
			this.nextEndServicingTaskFuture.cancel(true) ;
		}

		System.out.println("Mean service time: " +
				(((double)this.totalServicingTime) /
								((double)this.totalNumberOfServicedRequests) +
				" milliseconds.")) ;

		super.shutdown() ;
	}

	// -------------------------------------------------------------------------
	// Component internal services
	// -------------------------------------------------------------------------

	/**
	 * process a request arrival event, queueing the request and the processing
	 * a begin sericing event if the server is currently idle.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	r != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param r
	 * @throws Exception
	 */
	public void			requestArrivalEvent(Request r) throws Exception
	{
		assert	r != null ;

		long t = System.currentTimeMillis() ;
		System.out.println("Accepting request       " + r + " at " +
												TimeProcessing.toString(t)) ;
		r.setArrivalTime(t) ;
		this.requestsQueue.add(r) ;
		if (!this.serverIdle) {
			System.out.println("Queueing request " + r) ;
		} else {
			this.beginServicingEvent() ;
		}
	}

	/**
	 * process a begin servicing event, e.g. schedule a end servicing event
	 * after a delay of the processing time of the request.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 */
	public void			beginServicingEvent()
	{
		this.servicing = this.requestsQueue.remove() ;
		System.out.println("Begin servicing request " + this.servicing + " at "
						+ TimeProcessing.toString(System.currentTimeMillis())) ;
		this.serverIdle = false ;
		final ServiceProvider sp = (ServiceProvider) this ;
		// Calcul de la durée
		final long instructions= this.servicing.getInstructions() ;
		final long dummyProcessor = 1000000; //processeur a 1 GHZ
		final long processingTime = (long) instructions / dummyProcessor;
		//Fin calcul durée
		this.nextEndServicingTaskFuture =
			this.scheduleTask(
				new ComponentTask() {
					@Override
					public void run() {
						try {
							sp.endServicingEvent() ;
						} catch (Exception e) {
							e.printStackTrace() ;
						}
					}},
					processingTime, TimeUnit.MILLISECONDS) ;

	}

	/**
	 * process a end servicing event, e.g. update the statistics for the average
	 * service time, and then if the queue is not empty execute a begin
	 * servicing event immediately.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @throws Exception
	 */
	public void			endServicingEvent() throws Exception
	{
		long t = System.currentTimeMillis() ;
		long st = t - this.servicing.getArrivalTime() ;
		System.out.println("End servicing request   " + this.servicing +
								" at " + TimeProcessing.toString(t) +
								" with service time " + st) ;
		this.totalServicingTime += st ;
		this.totalNumberOfServicedRequests++ ;
		if (this.requestsQueue.isEmpty()) {
			this.servicing = null ;
			this.serverIdle = true ;
			this.nextEndServicingTaskFuture = null ;
		} else {
			this.beginServicingEvent() ;
		}
	}
}
