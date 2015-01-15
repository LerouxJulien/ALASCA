package fr.upmc.alasca.examples.main;

import java.util.ArrayList;

import fr.upmc.alasca.appgenerator.components.ApplicationRequestGenerator;
import fr.upmc.alasca.computer.components.Computer;
import fr.upmc.alasca.controleur.components.Controleur;
import fr.upmc.alasca.requestgen.utils.TimeProcessing;
import fr.upmc.components.ComponentI.ComponentTask;
import fr.upmc.components.cvm.AbstractDistributedCVM;
import fr.upmc.components.ports.PortI;

/**
 * Classe <code>DCVM</code>
 *
 * <p>
 * <strong>Description</strong>
 * </p>
 * 
 * <p>Classe paramètrant la simulation du centre de calcul.
 * 	Contient le main pour lancer la simulation en mode distribué.</p>
 * 
 * <p>Created on : 23 dec. 2014</p>
 * 
 * @author <a href="mailto:Mounier.Nicolas@etu.upmc.fr">Mounier Nicolas/a>
 *         <a href="mailto:Henri.Ng@etu.upmc.fr">Henri Ng/a>
 * @version $Name$ -- $Revision$ -- $Date$
 */
public class DCVM extends AbstractDistributedCVM{

	/************************ Paramètres de contrôles *************************/

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
	
	// JVM du centre de calcul (contrôleur, répartiteurs, machines et VM)
	protected static final String CONTROLER_JVM_URI = "controleur_jvm_uri";
	
	// JVM du client (gestionnaire de demandes et générateur de requêtes)
	protected static final String ARG_JVM_URI = "request_generator_jvm_uri";

	/*************************** Variables globales ***************************/
	
	// Contrôleur
	protected Controleur cont;
	
	// Gestionnaire de demandes d'application
	protected ApplicationRequestGenerator rg;

	// Listes des ID des applications à lancer
	private ArrayList<Integer> numberAppLaunched;
	
	// Listes des coeurs des machines
	private ArrayList<Double> core;
	
	// Paramètres de seuillage
	private String thresholds;
	
	/**************************************************************************/

	/**
	 * Constructeur de la DCVM
	 * 
	 * @param args
	 * @throws Exception
	 */
	public DCVM(String[] args) throws Exception {
		super(args);
	}
	
	@Override
	public void	initialise() throws Exception {
		
		super.initialise() ;
		
		// Processeur à <nbCore> coeurs cadencé à <freq>
		core = new ArrayList<Double>();
		for (int i = 0; i < nbCore; i++)
			core.add(freq);
		
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
		
		this.numberAppLaunched = new ArrayList<Integer>();
		numberAppLaunched.add(5);
		numberAppLaunched.add(7);
		numberAppLaunched.add(13);
	}
	
	@Override
	public void	instantiateAndPublish() throws Exception
	{
		if (thisJVMURI.equals(CONTROLER_JVM_URI)) {
			
			// create the controleur component (must set something to true, 
			// surement le fait que ce soit distribuee)
			this.cont = new Controleur(CONTROLER_OUTBOUNDPORT_URI,
					CONTROLER_INBOUNDPORT_URI, NB_COMPUTERS);
			
			// add it to the deployed components
			this.deployedComponents.add(cont);
			
			// deploiement des pc sur la meme jvm que le controleur
			for (int i = 1; i <= NB_COMPUTERS; ++i) {
				Computer comp = new Computer(COMPUTER_INBOUNDPORT_URI + i,
						i, core, 0.5, true, this);
				this.deployedComponents.add(comp);
			}
			
		} else if (thisJVMURI.equals(ARG_JVM_URI)) {
			this.rg = new ApplicationRequestGenerator(numberAppLaunched, 
					meanInterArrivalTime, meanNumberInstructions, 
					standardDeviation, thresholds, ARG_OUTBOUNDPORT_URI);
			this.deployedComponents.add(this.rg);

		} else {
			System.out.println("Unknown JVM URI... " + thisJVMURI) ;
		}

		super.instantiateAndPublish();
	}
	
	@Override
	public void	interconnect() throws Exception
	{
		assert this.instantiationAndPublicationDone ;

		if (thisJVMURI.equals(CONTROLER_JVM_URI)) {
			for (int i = 1; i <= NB_COMPUTERS; ++i) {
				PortI cont_port = this.cont
						.findPortFromURI(CONTROLER_OUTBOUNDPORT_URI + i);
				cont_port.doConnection(COMPUTER_INBOUNDPORT_URI + i,
						"fr.upmc.alasca.computer.connectors.ComputerConnector");
			}
			
		} else if (thisJVMURI.equals(ARG_JVM_URI)) {
			PortI generatorOutboundPort =
					this.rg.findPortFromURI(ARG_OUTBOUNDPORT_URI);
			generatorOutboundPort.doConnection(CONTROLER_INBOUNDPORT_URI,
				"fr.upmc.alasca.controleur.connectors." + 
				"ApplicationRequestConnector");

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
	 * TODO Solution temporaire pour assurer que le contrôleur accepte
	 * une application avant que la génération de requêtes ne commence
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
	public void	start() throws Exception
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
			Thread.sleep(30000L);
			a.shutdown();
			System.out.println("ending...");
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
