package fr.upmc.alasca.requestgen.objects;

import java.io.Serializable;

/**
 * The class <code>Request</code> defines objects representing requests in the
 * M/M/1 simulation example.
 *
 * <p>
 * <strong>Description</strong>
 * </p>
 * 
 * A request has a unique identifier (URI) and a processing time set at creation
 * time. When it arrives at a service provider, the arrival time can be set. The
 * arrival time can then be used later on when the execution of the request
 * finishes to compute the service time (waiting + processing) of the request.
 * 
 * As the object can be passed as parameter of a remote method call, the class
 * implements the Java interface <code>Serializable</code>.
 * 
 * <p>
 * <strong>Invariant</strong>
 * </p>
 * 
 * <pre>
 * invariant	processingTime > 0 && arrivalTime >= 0
 * </pre>
 * 
 * <p>
 * Created on : 2 sept. 2014
 * </p>
 * 
 * @author <a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class Request implements Serializable {
	private static final long serialVersionUID = 1L;

	/** unique identifier of the request, for tracing purposes. */
	protected int uri;
	/** number of instructions */
	protected long instructions;
	/** time at which it has been received by the service provider. */
	protected long arrivalTime;

	protected int application;

	/**
	 * create a new request with given uri and processing time.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	processingTime > 0
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param uri
	 *            unique identifier of the new request.
	 * @param instructions
	 *            number of instructions in the request
	 * @param application
	 *            application number
	 */
	public Request(int uri, long instructions, int application){
		super();

		assert instructions > 0;

		this.uri = uri;
		this.instructions = instructions;
		this.arrivalTime = 0;
		this.application = application;

		assert this.instructions > 0 && this.arrivalTime >= 0;
	}

	public int getUri() {
		return uri;
	}

	/**
	 * return the processing time of the request.
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
	 * @return the time required to execute the request.
	 */
	public long getInstructions() {
		return instructions;
	}

	public int getAppId() {
		return application;
	}

	/**
	 * return the time at which the request has been received by the service
	 * provider.
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
	 * @return the time at which the request has been received by the service
	 *         provider.
	 */
	public long getArrivalTime() {
		return arrivalTime;
	}

	/**
	 * sets the time at which the request has been received by the service
	 * provider.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	arrivalTime > 0
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param arrivalTime
	 */
	public void setArrivalTime(long arrivalTime) {
		assert arrivalTime > 0;

		this.arrivalTime = arrivalTime;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "" + this.uri;
	}
}
