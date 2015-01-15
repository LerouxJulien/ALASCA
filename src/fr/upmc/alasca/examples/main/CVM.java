package fr.upmc.alasca.examples.main;

import java.util.ArrayList;
import java.util.List;

import fr.upmc.alasca.appgenerator.components.ApplicationRequestGenerator;
import fr.upmc.alasca.computer.components.Computer;
import fr.upmc.alasca.controleur.components.Controleur;
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

	/************************ Paramètres de contrôles *************************/
	
	/** Général **/
	
	// Temps d'exécution des CVM (ms)
	protected static final long processingTime = 45000L;
	
	/** Machine **/
	
	// Nombre de machines du centre de calcul
	protected static final Integer NB_COMPUTERS = 8;
	
	// Nombre de coeurs des machines
	protected static final int nbCore = 8;
	
	// Fréquence des coeurs des machines (GHz)
	protected static final double freq = 2.0;
	
	/** Générateur de requêtes **/
	
	// Temps moyen d'arrivée entre chaque requête (ms)
	protected static final double meanInterArrivalTime = 500.0;
	
	// Nombre moyen d'instructions par requêtes
	protected static final int meanNumberInstructions = 50000000;
	
	// Ecart-type de temps traitement des requêtes (ms)
	protected static final Integer standardDeviation  = 20000000;

	/** Répartiteur de requêtes **/
	
	// Temps moyen de traitement des requêtes (ms)
	protected static final double meanProcessingTime = 
			meanNumberInstructions / (freq * 2 * 1000);
	
	// Seuil minimale avant diminution des fréquences
	protected static final double thFreqMin = -0.1;
	
	// Seuil maximale avant augmentation des fréquences
	protected static final double thFreqMax = 0.1;

	// Seuil minimale avant suppression d'un coeur à une VM
	protected static final double thCoreMin = -0.2;
	
	// Seuil maximale avant ajout d'un coeur à une VM
	protected static final double thCoreMax = 0.2;

	// Seuil minimale avant suppression d'une VM
	protected static final double thVMMin = -0.5;
	
	// Seuil maximale avant ajout d'une VM
	protected static final double thVMMax = 0.5;
	
	/********************* URI des différents composants **********************/
	
	// Nom de base de l'URI pour le port de des machines
	protected static final String COMPUTER_INBOUNDPORT_URI = "computer";
	
	// URI des ports du contrôleur
	protected static final String CONTROLER_OUTBOUNDPORT_URI = "controler_o";
	protected static final String CONTROLER_INBOUNDPORT_URI  = "controler_i";

	// URI du port pour le gestionnaire de demande de requêtes
	protected static final String ARG_OUTBOUNDPORT_URI = "client-generator";

	/*************************** Variables globales ***************************/
	
	// Contrôleur
	protected Controleur cont;
	
	// Gestionnaire de demandes d'application
	protected ApplicationRequestGenerator rg;

	// Listes des ID des applications à lancer
	protected List<Integer> numberAppLaunched;
	
	// Listes des coeurs des machines
	private ArrayList<Double> core;
	
	// Paramètres de seuillage
	private String thresholds;
	
	/**************************************************************************/

	@Override
	public void deploy() throws Exception {

		/*************** Déploiement des composants côté client ***************/
		
		// ID des applications utilisées
		this.numberAppLaunched = new ArrayList<>();
		numberAppLaunched.add(5);
		//numberAppLaunched.add(7);
		//numberAppLaunched.add(13);
		
		// Paramètres de contrôle des VM,
		// thresholds[0] = Temps d'exécution moyenne d'une requête
		// thresholds[1] = Seuil minimale avant diminution des fréquences
		// thresholds[2] = Seuil maximale avant augmentation des fréquences
		// thresholds[3] = Seuil minimale avant suppression d'un coeur à une VM
		// thresholds[4] = Seuil maximale avant ajout d'un coeur à une VM
		// thresholds[5] = Seuil minimale avant suppression d'une VM
		// thresholds[6] = Seuil maximale avant ajout d'une VM
		double[] values = {meanProcessingTime, thFreqMin, thFreqMax, thCoreMin, 
				thCoreMax, thVMMin, thVMMax};
		thresholds = "";
		for (double d : values)
			thresholds += d + ";";
		
		// Déploiement du générateur d'applications
		this.rg = new ApplicationRequestGenerator(numberAppLaunched, 
				meanInterArrivalTime, meanNumberInstructions, 
				standardDeviation, thresholds, ARG_OUTBOUNDPORT_URI);
		this.deployedComponents.add(rg);
		
		/*************** Déploiement des composants côté serveur **************/

		// Déploiement du contrôleur
		this.cont = new Controleur(CONTROLER_OUTBOUNDPORT_URI,
				CONTROLER_INBOUNDPORT_URI, NB_COMPUTERS);
		this.deployedComponents.add(cont);

		// Processeur à 4 coeurs
		core = new ArrayList<Double>();
		for (int i = 0; i < nbCore; i++)
			core.add(freq);
		
		// Déploiement des machines
		for (int i = 1; i <= NB_COMPUTERS; ++i) {
			Computer comp = new Computer(COMPUTER_INBOUNDPORT_URI + i,
					i, core, 0.5, true, this);
			this.deployedComponents.add(comp);
		}
		
		/************** Connexion entre les différents composants *************/
		
		// Connexion du générateur d'applications au contrôleur
		PortI generatorOutboundPort =
				this.rg.findPortFromURI(ARG_OUTBOUNDPORT_URI);
		generatorOutboundPort.doConnection(CONTROLER_INBOUNDPORT_URI,
			"fr.upmc.alasca.controleur.connectors." + 
			"ApplicationRequestConnector");

		// Connexion des machines au contrôleur
		for (int i = 1; i <= NB_COMPUTERS; ++i) {
			PortI cont_port = 
					this.cont.findPortFromURI(CONTROLER_OUTBOUNDPORT_URI + i);
			cont_port.doConnection(COMPUTER_INBOUNDPORT_URI + i,
					"fr.upmc.alasca.computer.connectors.ComputerConnector");
		}
		
		super.deploy();
	}

	@Override
	public void shutdown() throws Exception {
		
		// Déconnexion du générateur d'applications
		PortI agport = this.rg.findPortFromURI(ARG_OUTBOUNDPORT_URI);
		agport.doDisconnection();
		rg.shutdown();
		
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

			System.out.println("Scheduling request at "
					+ TimeProcessing.toString(System.currentTimeMillis()));

			final ApplicationRequestGenerator fcg = cvm.rg;
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
			Thread.sleep(processingTime);
			cvm.shutdown();
			System.out.println("ending...");
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
