package fr.upmc.alasca.dispatcher;

import java.net.URI;
import java.util.ArrayList;

import fr.upmc.alasca.computer.components.VirtualMachine;
import fr.upmc.alasca.controleurAdmission.components.Controleur;
import fr.upmc.alasca.requestgen.objects.Request;
import fr.upmc.components.ports.PortI;

/**
 * 
 * Classe <code> Repartitor </code> représentant un répartiteur lié a une if d'application.
 * 
 * 
 * 
 * @author Julien Leroux
 *
 */
public class Repartitor {

	//private static long VMMAXINST = 20000000;
	Controleur control;
	
	/**
	 * 
	 * Création du répartiteur
	 * 
	 * @param controleur
	 */
	
	public Repartitor(Controleur controleur){
		control = controleur;
		
	}
	
	
	
	
	/**
	 * Methode de repartition des requettes sur une liste de VM
	 * renvoi 0 si aucune création de VM n'est requise
	 * un entier représentant le nombre a creer sinon
	 * 
	 * @param req
	 * @param sendingList
	 * @return int
	 * @throws Exception
	 */
	public int dispatch(Request req, ArrayList<String> sendingList) throws Exception {
		for(int i =0; i<sendingList.size();i++){
			PortI port = control.findPortFromURI(sendingList.get(i));
			VirtualMachine VM = (VirtualMachine) port.getOwner();
		
			if(!VM.queueIsFull()){
				
				VM.requestArrivalEvent(req);
				return 0;
			}
		}
		return 1;
		}
	
		
		
		
	}

	


