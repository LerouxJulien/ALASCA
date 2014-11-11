package fr.upmc.alasca.controleur.main;

import java.util.ArrayList;
import fr.upmc.alasca.computer.components.Computer;
import fr.upmc.alasca.controleur.components.Controleur;
import fr.upmc.alasca.requestgen.components.RequestGenerator;
import fr.upmc.alasca.requestgen.utils.TimeProcessing;
import fr.upmc.components.ComponentI.ComponentTask;
import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.components.ports.PortI;

/**
 * Classe parametrant la simulation du centre de calcul Contient le main pour
 * lancer la simulation
 *
 */
public class CVM extends AbstractCVM {

	protected static final String COMPUTER_INBOUNDPORT_URI = "computer";
	protected static final String CONTROLER_OUTBOUNDPORT_URI = "controler_o";
	protected static final String CONTROLER_INBOUNDPORT_URI = "controler_i";
	protected static final String REQUEST_GENERATOR_OUTBOUNDPORT_URI = "client-generator";
	protected static final Integer NB_COMPUTERS = 4;

	protected Controleur cont;
	protected RequestGenerator rg;

	@Override
	public void deploy() throws Exception {
		// processeur a 8 coeurs : toutes les machines physiques ont ce
		// processeur
		ArrayList<Double> freq = new ArrayList<Double>();
		freq.add(4.0);
		freq.add(4.0);
		freq.add(4.0);
		freq.add(4.0);
		freq.add(4.0);
		freq.add(4.0);
		freq.add(4.0);
		freq.add(4.0);

		// les numeros des applications que le generateur de requetes utilisera
		ArrayList<Integer> numberAppLaunched = new ArrayList<Integer>();
		numberAppLaunched.add(5);
		numberAppLaunched.add(7);
		numberAppLaunched.add(13);

		// deploiement des pc
		for (int i = 0; i < NB_COMPUTERS; ++i) {
			Computer comp = new Computer(COMPUTER_INBOUNDPORT_URI + i, i, freq,
					0.5, false, this);
			this.deployedComponents.add(comp);
		}

		cont = new Controleur(CONTROLER_OUTBOUNDPORT_URI,
				CONTROLER_INBOUNDPORT_URI, NB_COMPUTERS);
		this.deployedComponents.add(cont);

		this.rg = new RequestGenerator(500.0, 50000000, 20000000,
				numberAppLaunched, REQUEST_GENERATOR_OUTBOUNDPORT_URI);
		this.deployedComponents.add(this.rg);

		// connexion du generateur de requete au controleur
		PortI rgport = this.rg
				.findPortFromURI(REQUEST_GENERATOR_OUTBOUNDPORT_URI);
		rgport.doConnection(CONTROLER_INBOUNDPORT_URI,
				"fr.upmc.alasca.requestgen.main.ClientArrivalConnector");

		// connexion des pc au controleur
		for (int i = 0; i < NB_COMPUTERS; ++i) {
			PortI cont_port = this.cont
					.findPortFromURI(CONTROLER_OUTBOUNDPORT_URI + i);
			cont_port.doConnection(COMPUTER_INBOUNDPORT_URI + i,
					"fr.upmc.alasca.computer.connectors.ComputerConnector");
		}

		super.deploy();
	}

	@Override
	public void shutdown() throws Exception {
		PortI rgport;
		for (int i = 0; i < NB_COMPUTERS; ++i) {
			rgport = this.cont.findPortFromURI(CONTROLER_OUTBOUNDPORT_URI + i);
			rgport.doDisconnection();
		}

		rgport = this.rg.findPortFromURI(REQUEST_GENERATOR_OUTBOUNDPORT_URI);
		rgport.doDisconnection();

		super.shutdown();
	}

	public static void main(String[] args) {
		CVM a = new CVM();
		try {
			a.deploy();
			System.out.println("starting...");
			a.start();
			final Controleur controleur = a.cont;
			final RequestGenerator fcg = a.rg;

			// creation des repartiteurs dedies a l'application dont l'id leur
			// est passe en parametre
			controleur.acceptApplication(5);
			controleur.acceptApplication(7);

			System.out.println("Scheduling request at "
					+ TimeProcessing.toString(System.currentTimeMillis()));
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
			a.shutdown();
			System.out.println("ending...");
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
