package fr.upmc.alasca.examples.main;

import java.util.ArrayList;

import fr.upmc.alasca.appgenerator.components.ApplicationRequestGenerator;
import fr.upmc.alasca.computer.components.Computer;
import fr.upmc.alasca.controleur.components.Controleur;
import fr.upmc.alasca.requestgen.components.RequestGenerator;
import fr.upmc.alasca.requestgen.utils.TimeProcessing;
import fr.upmc.components.ComponentI.ComponentTask;
import fr.upmc.components.cvm.AbstractDistributedCVM;
import fr.upmc.components.ports.PortI;

public class DCVM extends		AbstractDistributedCVM{

	protected static final String COMPUTER_INBOUNDPORT_URI = "computer";
	
	protected static final String CONTROLER_OUTBOUNDPORT_URI = "controler_o";
	protected static final String CONTROLER_INBOUNDPORT_URI = "controler_i";

	
	protected static final String ARG_OUTBOUNDPORT_URI = "client-generator";
	

	protected static final String CONTROLER_JVM_URI = "controleur_jvm_uri" ;
	protected static final String ARG_JVM_URI = "request_generator_jvm_uri" ;
	
	protected static final Integer NB_COMPUTERS = 4;

	protected Controleur cont;
	protected ApplicationRequestGenerator rg;

	ArrayList<Integer> numberAppLaunched;
	ArrayList<Double> freq;

	public DCVM(String[] args) throws Exception {
		super(args);
	}
	
	@Override
	public void			initialise() throws Exception
	{
		super.initialise() ;
		// any other application-specific initialisation must be put here
		
		// processeur a 8 coeurs : toutes les machines physiques ont ce
		// processeur
		freq = new ArrayList<Double>();
		freq.add(4.0);
		freq.add(4.0);
		freq.add(4.0);
		freq.add(4.0);
		freq.add(4.0);
		freq.add(4.0);
		freq.add(4.0);
		freq.add(4.0);
		
		this.numberAppLaunched = new ArrayList<Integer>();
		numberAppLaunched.add(5);
		numberAppLaunched.add(7);
		numberAppLaunched.add(13);
	}
	
	@Override
	public void			instantiateAndPublish() throws Exception
	{
		if (thisJVMURI.equals(CONTROLER_JVM_URI)) {

			// create the controleur component (must set something to true, surement le fait que ce soit distribuee)
			this.cont = new Controleur(CONTROLER_OUTBOUNDPORT_URI,
					CONTROLER_INBOUNDPORT_URI, NB_COMPUTERS);
			// add it to the deployed components
			this.deployedComponents.add(cont);
			
			// deploiement des pc sur la meme jvm que le controleur
			for (int i = 0; i < NB_COMPUTERS; ++i) {
				Computer comp = new Computer(COMPUTER_INBOUNDPORT_URI + i, i, freq,
						0.5, true, this);
				this.deployedComponents.add(comp);
			}

		} else if (thisJVMURI.equals(ARG_JVM_URI)) {

			this.rg = new ApplicationRequestGenerator(numberAppLaunched, 500.0, 50000000, 20000000,
					 ARG_OUTBOUNDPORT_URI);
			this.deployedComponents.add(this.rg);

		} else {

			System.out.println("Unknown JVM URI... " + thisJVMURI) ;

		}

		super.instantiateAndPublish();
	}
	
	/**
	 * TODO
	 */
	@Override
	public void			interconnect() throws Exception
	{
		assert	this.instantiationAndPublicationDone ;

		if (thisJVMURI.equals(CONTROLER_JVM_URI)) {
			for (int i = 0; i < NB_COMPUTERS; ++i) {
				PortI cont_port = this.cont
						.findPortFromURI(CONTROLER_OUTBOUNDPORT_URI + i);
				cont_port.doConnection(COMPUTER_INBOUNDPORT_URI + i,
						"fr.upmc.alasca.computer.connectors.ComputerConnector");
			}
			
		} else if (thisJVMURI.equals(ARG_JVM_URI)) {

			// do the connection
			PortI generatorOutboundPort =
					this.rg.findPortFromURI(ARG_OUTBOUNDPORT_URI) ;
			generatorOutboundPort.doConnection(
					CONTROLER_INBOUNDPORT_URI,
				"fr.upmc.alasca.controleur.connectors.ApplicationRequestConnector") ;

		} else {

			System.out.println("Unknown JVM URI... " + thisJVMURI) ;

		}

		super.interconnect();
	}

	@Override
	public void shutdown() throws Exception {
		// TODO

		super.shutdown();
	}
	
	/**
	 * TODO solution temporaire pour assurer que le controleur accepte appli avant que generation requetes
	 * commence
	 */
	@Override
	public void deploy() throws Exception {
		super.deploy();
		/*if (thisJVMURI.equals(CONTROLER_JVM_URI)) {
			final Controleur controleur = this.cont;

			// creation des repartiteurs dedies a l'application dont l'id leur
			// est passe en parametre
			controleur.acceptApplication(5);
			controleur.acceptApplication(7);
		}*/
		
	}
	
	
	@Override
	public void			start() throws Exception
	{
		super.start() ;

	}

	public static void main(String[] args) {
		try {
			DCVM a = new DCVM(args);
			a.deploy();
			System.out.println("starting...");
			a.start();
			

			if (thisJVMURI.equals(ARG_JVM_URI)) {
				System.out.println("Scheduling request at "
						+ TimeProcessing.toString(System.currentTimeMillis()));
				final ApplicationRequestGenerator fcg = a.rg;
				fcg.runTask(new ComponentTask() {
					@Override
					public void run() {
						try {
							fcg.generateNextRequest();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
			Thread.sleep(130000L);
			a.shutdown();
			System.out.println("ending...");
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
