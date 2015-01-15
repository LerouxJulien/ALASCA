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
 * <p>Classe param�trant la simulation du centre de calcul.
 * 	Contient le main pour lancer la simulation.</p>
 * 
 * <p>Created on : 23 dec. 2014</p>
 * 
 * @author <a href="mailto:Henri.Ng@etu.upmc.fr">Henri Ng/a>
 * @version $Name$ -- $Revision$ -- $Date$
 */
public class CVM extends AbstractCVM {

	/************************ Param�tres de contr�les *************************/
	
	/** G�n�ral **/
	
	// Temps d'ex�cution des CVM (ms)
	protected static final long processingTime = 45000L;
	
	/** Machine **/
	
	// Nombre de machines du centre de calcul
	protected static final Integer NB_COMPUTERS = 8;
	
	// Nombre de coeurs des machines
	protected static final int nbCore = 8;
	
	// Fr�quence des coeurs des machines (GHz)
	protected static final double freq = 2.0;
	
	/** G�n�rateur de requ�tes **/
	
	// Temps moyen d'arriv�e entre chaque requ�te (ms)
	protected static final double meanInterArrivalTime = 500.0;
	
	// Nombre moyen d'instructions par requ�tes
	protected static final int meanNumberInstructions = 50000000;
	
	// Ecart-type de temps traitement des requ�tes (ms)
	protected static final Integer standardDeviation  = 20000000;

	/** R�partiteur de requ�tes **/
	
	// Temps moyen de traitement des requ�tes (ms)
	protected static final double meanProcessingTime = 
			meanNumberInstructions / (freq * 2 * 1000);
	
	// Seuil minimale avant diminution des fr�quences
	protected static final double thFreqMin = -0.1;
	
	// Seuil maximale avant augmentation des fr�quences
	protected static final double thFreqMax = 0.1;

	// Seuil minimale avant suppression d'un coeur � une VM
	protected static final double thCoreMin = -0.2;
	
	// Seuil maximale avant ajout d'un coeur � une VM
	protected static final double thCoreMax = 0.2;

	// Seuil minimale avant suppression d'une VM
	protected static final double thVMMin = -0.5;
	
	// Seuil maximale avant ajout d'une VM
	protected static final double thVMMax = 0.5;
	
	/********************* URI des diff�rents composants **********************/
	
	// Nom de base de l'URI pour le port de des machines
	protected static final String COMPUTER_INBOUNDPORT_URI = "computer";
	
	// URI des ports du contr�leur
	protected static final String CONTROLER_OUTBOUNDPORT_URI = "controler_o";
	protected static final String CONTROLER_INBOUNDPORT_URI  = "controler_i";

	// URI du port pour le gestionnaire de demande de requ�tes
	protected static final String ARG_OUTBOUNDPORT_URI = "client-generator";

	/*************************** Variables globales ***************************/
	
	// Contr�leur
	protected Controleur cont;
	
	// Gestionnaire de demandes d'application
	protected ApplicationRequestGenerator rg;

	// Listes des ID des applications � lancer
	protected List<Integer> numberAppLaunched;
	
	// Listes des coeurs des machines
	private ArrayList<Double> core;
	
	// Param�tres de seuillage
	private String thresholds;
	
	/**************************************************************************/

	@Override
	public void deploy() throws Exception {

		/*************** D�ploiement des composants c�t� client ***************/
		
		// ID des applications utilis�es
		this.numberAppLaunched = new ArrayList<>();
		numberAppLaunched.add(5);
		//numberAppLaunched.add(7);
		//numberAppLaunched.add(13);
		
		// Param�tres de contr�le des VM,
		// thresholds[0] = Temps d'ex�cution moyenne d'une requ�te
		// thresholds[1] = Seuil minimale avant diminution des fr�quences
		// thresholds[2] = Seuil maximale avant augmentation des fr�quences
		// thresholds[3] = Seuil minimale avant suppression d'un coeur � une VM
		// thresholds[4] = Seuil maximale avant ajout d'un coeur � une VM
		// thresholds[5] = Seuil minimale avant suppression d'une VM
		// thresholds[6] = Seuil maximale avant ajout d'une VM
		double[] values = {meanProcessingTime, thFreqMin, thFreqMax, thCoreMin, 
				thCoreMax, thVMMin, thVMMax};
		thresholds = "";
		for (double d : values)
			thresholds += d + ";";
		
		// D�ploiement du g�n�rateur d'applications
		this.rg = new ApplicationRequestGenerator(numberAppLaunched, 
				meanInterArrivalTime, meanNumberInstructions, 
				standardDeviation, thresholds, ARG_OUTBOUNDPORT_URI);
		this.deployedComponents.add(rg);
		
		/*************** D�ploiement des composants c�t� serveur **************/

		// D�ploiement du contr�leur
		this.cont = new Controleur(CONTROLER_OUTBOUNDPORT_URI,
				CONTROLER_INBOUNDPORT_URI, NB_COMPUTERS);
		this.deployedComponents.add(cont);

		// Processeur � 4 coeurs
		core = new ArrayList<Double>();
		for (int i = 0; i < nbCore; i++)
			core.add(freq);
		
		// D�ploiement des machines
		for (int i = 1; i <= NB_COMPUTERS; ++i) {
			Computer comp = new Computer(COMPUTER_INBOUNDPORT_URI + i,
					i, core, 0.5, true, this);
			this.deployedComponents.add(comp);
		}
		
		/************** Connexion entre les diff�rents composants *************/
		
		// Connexion du g�n�rateur d'applications au contr�leur
		PortI generatorOutboundPort =
				this.rg.findPortFromURI(ARG_OUTBOUNDPORT_URI);
		generatorOutboundPort.doConnection(CONTROLER_INBOUNDPORT_URI,
			"fr.upmc.alasca.controleur.connectors." + 
			"ApplicationRequestConnector");

		// Connexion des machines au contr�leur
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
		
		// D�connexion du g�n�rateur d'applications
		PortI agport = this.rg.findPortFromURI(ARG_OUTBOUNDPORT_URI);
		agport.doDisconnection();
		rg.shutdown();
		
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
