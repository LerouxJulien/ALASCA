package fr.upmc.alasca.requestgen.main;

import fr.upmc.alasca.requestgen.components.RequestGenerator;
import fr.upmc.alasca.requestgen.components.ServiceProvider;
import fr.upmc.alasca.requestgen.utils.TimeProcessing;
import fr.upmc.components.ComponentI.ComponentTask;
import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.components.ports.PortI;

/**
 * The class <code>CVM</code> implements the component virtual machine for the
 * execution of a M/M/1 simulation.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 * 
 * <p>Created on : 2 sept. 2014</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public class			CVM
extends		AbstractCVM
{
	protected static final String	REQUEST_GENERATOR_OUTBOUNDPORT_URI = "client-generator" ;
	protected static final String	REQUEST_ARRIVAL_INBOUNDPORT_URI = "client-arrival" ;

	protected RequestGenerator		cg ;

	/**
	 * creat a service provider and a request generator components, register
	 * them and connect them.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true				// no more preconditions.
	 * post	true				// no more postconditions.
	 * </pre>
	 * 
	 * @see fr.upmc.components.cvm.AbstractCVM#deploy()
	 */
	@Override
	public void			deploy() throws Exception
	{
		ServiceProvider sp ;

		sp = new ServiceProvider(REQUEST_ARRIVAL_INBOUNDPORT_URI, false) ;
		this.deployedComponents.add(sp) ;
		this.cg = new RequestGenerator(1000.0, 50000000, 20000000,
									   REQUEST_GENERATOR_OUTBOUNDPORT_URI) ;
		this.deployedComponents.add(this.cg) ;

		PortI rgport = this.cg.findPortFromURI(
										REQUEST_GENERATOR_OUTBOUNDPORT_URI) ;
		rgport.doConnection(REQUEST_ARRIVAL_INBOUNDPORT_URI,
							"fr.upmc.alasca.requestgen.main.ClientArrivalConnector") ;

		super.deploy() ;
	}

	/**
	 * disconnect the request generator from the service provider component and
	 * then shut down all of the components.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true				// no more preconditions.
	 * post	true				// no more postconditions.
	 * </pre>
	 * 
	 * @see fr.upmc.components.cvm.AbstractCVM#shutdown()
	 */
	@Override
	public void			shutdown() throws Exception
	{
		PortI rgport = this.cg.findPortFromURI(
										REQUEST_GENERATOR_OUTBOUNDPORT_URI) ;
		rgport.doDisconnection() ;

		super.shutdown();
	}

	/**
	 * create the virtual machine, deploy the components, start them, launch the
	 * request generation and then shut down after 15 seconds of execution.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param args
	 */
	public static void	main(String[] args)
	{
		CVM a = new CVM() ;
		try {
			a.deploy() ;
			System.out.println("starting...") ;
			a.start() ;
			final RequestGenerator fcg = a.cg ;
			System.out.println("Scheduling request at " +
						TimeProcessing.toString(System.currentTimeMillis())) ;
			fcg.runTask(new ComponentTask() {
							@Override
							public void run() {
								try {
									fcg.generateNextRequest() ;
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}) ;
			Thread.sleep(15000L) ;
			a.shutdown() ;
			System.out.println("ending...") ;
			System.exit(0) ;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
