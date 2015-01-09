package fr.upmc.alasca.examples.main;

import java.util.ArrayList;
import java.util.List;

import fr.upmc.alasca.appgen.components.ApplicationGenerator;
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
 * <p>Classe paramètrant la simulation du centre de calcul.
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
	protected ApplicationGenerator ag;
	protected static List<Integer> appIDLaunched = new ArrayList<>();
	protected static List<RequestGenerator> rgs  = new ArrayList<>();

	@Override
	public void deploy() throws Exception {

		/*************** Déploiement des composants côté client ***************/
		
		// ID des applications utilisées
		appIDLaunched.add(5);
		appIDLaunched.add(7);
		appIDLaunched.add(13);
		
		// Déploiement du générateur d'applications
		ag = new ApplicationGenerator(appIDLaunched, 
				APPGEN_OUTBOUNDPORT_URI, false, this);
		this.deployedComponents.add(ag);

		// Déploiement des générateurs de requêtes
		for (int i = 0; i < appIDLaunched.size(); i++) {
			RequestGenerator rg = new RequestGenerator(500.0, 50000000,
					20000000, appIDLaunched.subList(i, i + 1),
					REQGEN_OUTBOUNDPORT_URI + appIDLaunched.get(i));
			this.deployedComponents.add(rg);
			CVM.rgs.add(rg);
		}
		
		/*************** Déploiement des composants côté serveur **************/

		// Déploiement du contrôleur
		cont = new Controleur(CONTROLER_OUTBOUNDPORT_URI,
				CONTROLER_INBOUNDPORT_URI, NB_COMPUTERS);
		this.deployedComponents.add(cont);

		// Processeur à 4 coeurs
		ArrayList<Double> freq = new ArrayList<Double>();
		for (int i = 0; i < 4; i++)
			freq.add(4.0);
		
		// Déploiement des machines
		for (int i = 0; i < NB_COMPUTERS; i++) {
			Computer comp = new Computer(COMPUTER_INBOUNDPORT_URI + i, i, freq,
					0.5, false, this);
			this.deployedComponents.add(comp);
		}
		
		/************** Connexion entre les différents composants *************/
		
		// Connexion du générateur d'applications au contrôleur
		PortI agport = ag.findPortFromURI(APPGEN_OUTBOUNDPORT_URI);
		agport.doConnection(CONTROLER_INBOUNDPORT_URI,
				"fr.upmc.alasca.appgen.connectors." +
				"ApplicationGeneratorConnector");
		
		// Connexion des générateurs de requêtes au contrôleur
//		for (int i = 0; i < numberAppLaunched.size(); i++) {
//			PortI rgport = rgs.get(i).findPortFromURI(REQGEN_OUTBOUNDPORT_URI +
//					numberAppLaunched.get(i));
//			rgport.doConnection(CONTROLER_INBOUNDPORT_URI,
//					"fr.upmc.alasca.requestgen.main.ClientArrivalConnector");
//		}

		// Connexion des machines au contrôleur
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
		
		// Déconnexion du générateur d'applications
		PortI agport = this.ag.findPortFromURI(APPGEN_OUTBOUNDPORT_URI);
		agport.doDisconnection();
		ag.shutdown();

		// Déconnexion des générateurs de requêtes
		PortI rgport;
		for (RequestGenerator rg : this.rgs) {
			rgport = rg.findPortFromURI(REQGEN_OUTBOUNDPORT_URI);
			rgport.doDisconnection();
		}
		
		// Déconnexion des machines
		PortI mport;
		for (int i = 0; i < NB_COMPUTERS; i++) {
			mport = this.cont.findPortFromURI(CONTROLER_OUTBOUNDPORT_URI + i);
			mport.doDisconnection();
		}
		
		// Déconnexion du contrôleur
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
			final Controleur controleur = cvm.cont;

			// Création des répartiteurs dédiés à l'application
			// dont l'id leur est passée en paramètre
			for (Integer appID : appIDLaunched) {
				String URIReqGen = REQGEN_OUTBOUNDPORT_URI + appID;
				controleur.connectRequestGenerator(appID, URIReqGen,
						URIReqGen + "-dcc");
				//controleur.acceptApplication(appID);
			}

			System.out.println("Scheduling request at "
					+ TimeProcessing.toString(System.currentTimeMillis()));

			for (final RequestGenerator rg : rgs) {
				rg.runTask(new ComponentTask() {
					@Override
					public void run() {
						try {
							rg.generateNextRequest();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
			Thread.sleep(20000L);
			cvm.shutdown();
			System.out.println("ending...");
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
