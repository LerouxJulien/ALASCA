package fr.upmc.alasca.examples.main;

import java.util.ArrayList;
import java.util.List;

import fr.upmc.alasca.appgenerator.components.ApplicationRequestGenerator;
import fr.upmc.alasca.computer.components.Computer;
import fr.upmc.alasca.controleur.components.Controleur;
import fr.upmc.alasca.requestgen.components.RequestGenerator;
import fr.upmc.alasca.requestgen.utils.TimeProcessing;
import fr.upmc.components.ComponentI.ComponentTask;
import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.components.ports.PortI;

/**
 * Classe <code>CVM</code>
 *
 * <p>
 * <strong>Description</strong>
 * </p>
 * 
 * <p>Classe param�trant la simulation du centre de calcul.
 * 	Contient le main pour lancer la simulation.</p>
 * 
 * <p>Created on : 23 dec. 2014</p>
 * 
 * @author <a href="mailto:Henri.Ng@etu.upmc.fr">Henri Ng/a>
 * @version $Name$ -- $Revision$ -- $Date$
 */
public class CVM extends AbstractCVM {

	protected static final Integer NB_COMPUTERS = 4;
	
	protected static final String APPGEN_OUTBOUNDPORT_URI    = "appgen_o";
	protected static final String COMPUTER_INBOUNDPORT_URI   = "computer_i";
	protected static final String CONTROLER_OUTBOUNDPORT_URI = "controler_o";
	protected static final String CONTROLER_INBOUNDPORT_URI  = "controler_i";
	protected static final String REQGEN_OUTBOUNDPORT_URI    = "reqgen_o";

	protected Controleur cont;
	protected ApplicationRequestGenerator ag;
	protected static List<Integer> appIDLaunched = new ArrayList<>();
	protected static List<RequestGenerator> rgs  = new ArrayList<>();

	@Override
	public void deploy() throws Exception {

		/*************** D�ploiement des composants c�t� client ***************/
		
		// ID des applications utilis�es
		appIDLaunched.add(5);
		appIDLaunched.add(7);
		appIDLaunched.add(13);
		
		// D�ploiement du g�n�rateur d'applications
		ag = new ApplicationRequestGenerator(appIDLaunched, 500.0, 50000000,
				20000000, APPGEN_OUTBOUNDPORT_URI);
		this.deployedComponents.add(ag);
		
		/*************** D�ploiement des composants c�t� serveur **************/

		// D�ploiement du contr�leur
		cont = new Controleur(CONTROLER_OUTBOUNDPORT_URI,
				CONTROLER_INBOUNDPORT_URI, NB_COMPUTERS);
		this.deployedComponents.add(cont);

		// Processeur � 4 coeurs
		ArrayList<Double> freq = new ArrayList<Double>();
		for (int i = 0; i < 4; i++)
			freq.add(4.0);
		
		// D�ploiement des machines
		for (int i = 0; i < NB_COMPUTERS; i++) {
			Computer comp = new Computer(COMPUTER_INBOUNDPORT_URI + i, i, freq,
					0.5, false, this);
			this.deployedComponents.add(comp);
		}
		
		/************** Connexion entre les diff�rents composants *************/
		
		// Connexion du g�n�rateur d'applications au contr�leur
		PortI agport = ag.findPortFromURI(APPGEN_OUTBOUNDPORT_URI);
		agport.doConnection(CONTROLER_INBOUNDPORT_URI,
				"fr.upmc.alasca.controleur.connectors." +
				"ApplicationRequestConnector");

		// Connexion des machines au contr�leur
		for (int i = 0; i < NB_COMPUTERS; i++) {
			PortI cont_port = this.cont
					.findPortFromURI(CONTROLER_OUTBOUNDPORT_URI + i);
			cont_port.doConnection(COMPUTER_INBOUNDPORT_URI + i,
					"fr.upmc.alasca.computer.connectors.ComputerConnector");
		}

		super.deploy();
	}

	@Override
	public void shutdown() throws Exception {
		
		// D�connexion du g�n�rateur d'applications
		PortI agport = this.ag.findPortFromURI(APPGEN_OUTBOUNDPORT_URI);
		agport.doDisconnection();
		ag.shutdown();
		
		// D�connexion des machines
		PortI mport;
		for (int i = 0; i < NB_COMPUTERS; i++) {
			mport = this.cont.findPortFromURI(CONTROLER_OUTBOUNDPORT_URI + i);
			mport.doDisconnection();
		}
		
		// D�connexion du contr�leur
		PortI cport;
		cport = this.cont.findPortFromURI(CONTROLER_INBOUNDPORT_URI);
		cport.doDisconnection();
		
		super.shutdown();
	}

	public static void main(String[] args) {
		CVM cvm = new CVM();
		try {
			cvm.deploy();
			System.out.println("starting...");
			cvm.start();

			System.out.println("Scheduling request at "
					+ TimeProcessing.toString(System.currentTimeMillis()));

			final ApplicationRequestGenerator fcg = cvm.ag;
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
			Thread.sleep(20000L);
			cvm.shutdown();
			System.out.println("ending...");
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}