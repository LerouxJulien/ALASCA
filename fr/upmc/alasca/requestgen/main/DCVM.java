package fr.upmc.alasca.requestgen.main;

import fr.upmc.alasca.requestgen.components.RequestGenerator;
import fr.upmc.alasca.requestgen.components.ServiceProvider;
import fr.upmc.alasca.requestgen.utils.TimeProcessing;
import fr.upmc.components.ComponentI.ComponentTask;
import fr.upmc.components.cvm.AbstractDistributedCVM;
import fr.upmc.components.ports.PortI;

/**
 * The class <code>CVM</code> implements the component virtual machine for the
 * execution of a M/M/1 simulation.
 *
 * <p>
 * <strong>Description</strong>
 * </p>
 * 
 * <p>
 * <strong>Invariant</strong>
 * </p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 * 
 * <p>
 * Created on : 2 sept. 2014
 * </p>
 * 
 * @author <a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version $Name$ -- $Revision$ -- $Date$
 */
public class DCVM extends AbstractDistributedCVM {
	public DCVM(String[] args) throws Exception {
		super(args);
		// TODO Auto-generated constructor stub
	}

	protected static final String GENERATOR_URI = "generator";
	protected static final String ARRIVAL_URI = "arrival";
	protected static String URIGeneratorOutboundPortURI = "oport";
	protected static String URIArrivalInboundPortURI = "iport";

	protected RequestGenerator rg;

	protected ServiceProvider sp;

	@Override
	public void initialise() throws Exception {
		super.initialise();
		// any other application-specific initialisation must be put here
	}

	@Override
	public void instantiateAndPublish() throws Exception {
		if (thisJVMURI.equals(ARRIVAL_URI)) {

			// create the provider component
			this.sp = new ServiceProvider(URIArrivalInboundPortURI, true);
			// add it to the deployed components
			this.deployedComponents.add(sp);

		} else if (thisJVMURI.equals(GENERATOR_URI)) {

			// create the consumer component
			this.rg = new RequestGenerator(1000.0, 50000000, 20000000,
					URIGeneratorOutboundPortURI);
			// add it to the deployed components
			this.deployedComponents.add(rg);

		} else {

			System.out.println("Unknown JVM URI... " + thisJVMURI);

		}

		super.instantiateAndPublish();
	}

	@Override
	public void interconnect() throws Exception {
		assert this.instantiationAndPublicationDone;

		if (thisJVMURI.equals(ARRIVAL_URI)) {

		} else if (thisJVMURI.equals(GENERATOR_URI)) {

			// do the connection
			PortI generatorOutboundPort = this.rg
					.findPortFromURI(URIGeneratorOutboundPortURI);
			generatorOutboundPort.doConnection(URIArrivalInboundPortURI,
					"fr.upmc.alasca.requestgen.main.ClientArrivalConnector");

		} else {

			System.out.println("Unknown JVM URI... " + thisJVMURI);

		}

		super.interconnect();
	}

	/**
	 * creat a service provider and a request generator components, register
	 * them and connect them.
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
	 * @see fr.upmc.components.cvm.AbstractCVM#deploy()
	 */
	/*
	 * @Override public void deploy() throws Exception { ServiceProvider sp ;
	 * 
	 * sp = new ServiceProvider(REQUEST_ARRIVAL_INBOUNDPORT_URI, false) ;
	 * this.deployedComponents.add(sp) ; this.cg = new RequestGenerator(1000.0,
	 * 50000000, 20000000, REQUEST_GENERATOR_OUTBOUNDPORT_URI) ;
	 * this.deployedComponents.add(this.cg) ;
	 * 
	 * PortI rgport = this.cg.findPortFromURI(
	 * REQUEST_GENERATOR_OUTBOUNDPORT_URI) ;
	 * rgport.doConnection(REQUEST_ARRIVAL_INBOUNDPORT_URI,
	 * "projet.main.ClientArrivalConnector") ;
	 * 
	 * super.deploy() ; }
	 */

	/**
	 * disconnect the request generator from the service provider component and
	 * then shut down all of the components.
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
	 * @see fr.upmc.components.cvm.AbstractCVM#shutdown()
	 */
	@Override
	public void shutdown() throws Exception {
		if (thisJVMURI.equals(GENERATOR_URI)) {
			PortI rgport = this.rg
					.findPortFromURI(URIGeneratorOutboundPortURI);
			rgport.doDisconnection();
			// any disconnection not done yet should be performed here

		} else if (thisJVMURI.equals(ARRIVAL_URI)) {
			// any disconnection not done yet should be performed here

		}

		super.shutdown();
	}

	/**
	 * create the virtual machine, deploy the components, start them, launch the
	 * request generation and then shut down after 15 seconds of execution.
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
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			DCVM a = new DCVM(args);
			a.deploy();
			System.out.println("starting...");
			a.start();
			/*
			 * final RequestGenerator fcg = a.cg ;
			 * System.out.println("Scheduling request at " +
			 * TimeProcessing.toString(System.currentTimeMillis())) ;
			 * fcg.runTask(new ComponentTask() {
			 * 
			 * @Override public void run() { try { fcg.generateNextRequest() ; }
			 * catch (Exception e) { e.printStackTrace(); } } }) ;
			 */
			Thread.sleep(15000L);
			a.shutdown();
			System.out.println("ending...");
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
