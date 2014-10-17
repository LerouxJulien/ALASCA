package fr.upmc.alasca.computer.main;

import java.util.ArrayList;
import java.util.List;

import fr.upmc.alasca.computer.components.Computer;
import fr.upmc.alasca.controleurBouchon.components.Controleur;
import fr.upmc.alasca.requestgen.components.RequestGenerator;
import fr.upmc.alasca.requestgen.utils.TimeProcessing;
import fr.upmc.components.ComponentI.ComponentTask;
import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.components.ports.PortI;

public class CVM extends AbstractCVM {

	protected static final String COMPUTER_INBOUNDPORT_URI = "computer";
	protected static final String CONTROLER_OUTBOUNDPORT_URI = "controler_o";
	protected static final String CONTROLER_INBOUNDPORT_URI = "controler_i";
	protected static final String	REQUEST_GENERATOR_OUTBOUNDPORT_URI = "client-generator" ;

	protected Controleur cont;
	protected RequestGenerator		rg ;
	
	@Override
	public void deploy() throws Exception {
		List<Double> freq = new ArrayList<Double>();
		freq.add(4.0);
		freq.add(4.0);
		freq.add(4.0);
		freq.add(4.0);

		Computer comp = new Computer(COMPUTER_INBOUNDPORT_URI, 0, freq, 0.5,
				false);
		this.deployedComponents.add(comp);

		cont = new Controleur(CONTROLER_OUTBOUNDPORT_URI,CONTROLER_INBOUNDPORT_URI);
		this.deployedComponents.add(cont);
		
		this.rg = new RequestGenerator(1000.0, 50000000, 20000000,
				   REQUEST_GENERATOR_OUTBOUNDPORT_URI) ;
		this.deployedComponents.add(this.rg) ;
		
		PortI rgport = this.rg.findPortFromURI(
				REQUEST_GENERATOR_OUTBOUNDPORT_URI) ;
		rgport.doConnection(CONTROLER_INBOUNDPORT_URI,
				"fr.upmc.alasca.requestgen.main.ClientArrivalConnector") ;

		PortI cont_port = this.cont
				.findPortFromURI(CONTROLER_OUTBOUNDPORT_URI);
		cont_port.doConnection(COMPUTER_INBOUNDPORT_URI,
				"fr.upmc.alasca.computer.main.ComputerConnector");

		super.deploy();
		/*
		 * this.cg = new RequestGenerator(1000.0, 50000000, 20000000,
		 * REQUEST_GENERATOR_OUTBOUNDPORT_URI) ;
		 * this.deployedComponents.add(this.cg) ;
		 * 
		 * PortI rgport = this.cg.findPortFromURI(
		 * REQUEST_GENERATOR_OUTBOUNDPORT_URI) ;
		 * rgport.doConnection(REQUEST_ARRIVAL_INBOUNDPORT_URI,
		 * "fr.upmc.alasca.requestgen.main.ClientArrivalConnector") ;
		 */
	}
	
	@Override
	public void			shutdown() throws Exception
	{
		PortI rgport = this.cont.findPortFromURI(
				CONTROLER_OUTBOUNDPORT_URI) ;
		rgport.doDisconnection() ;
		
		rgport = this.rg.findPortFromURI(REQUEST_GENERATOR_OUTBOUNDPORT_URI);
		rgport.doDisconnection();

		super.shutdown();
	}
	
	public static void	main(String[] args)
	{
		CVM a = new CVM() ;
		try {
			a.deploy() ;
			System.out.println("starting...") ;
			a.start() ;
			final Controleur controleur = a.cont ;
			System.out.println("Debut test") ;
			final RequestGenerator fcg = a.rg ;
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
			Thread.sleep(7000L) ;
			controleur.testConnexion();
			Thread.sleep(2000L) ;
			a.shutdown() ;
			System.out.println("ending...") ;
			System.exit(0) ;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
