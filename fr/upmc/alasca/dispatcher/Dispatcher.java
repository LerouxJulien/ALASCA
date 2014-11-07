package fr.upmc.alasca.dispatcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import fr.upmc.alasca.computer.components.VirtualMachine;
import fr.upmc.alasca.controleurAdmission.components.Controleur;
import fr.upmc.alasca.requestgen.objects.Request;
import fr.upmc.components.ports.PortI;

/** 
 * Classe <code>Dispatcher</code> représentant le composant interne du dispatcher
 * délèguant une application au répartiteur correspondant 
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 *
 *@author Julien Leroux
 *
 */

public class Dispatcher {
	
	/*controleur d'admission dont dépend le Dispatcher.*/
	
	private Controleur control;
	
	
	
	// Map des couples applications/repartiteurs.
	
	private Map<Integer ,Repartitor > repartitorList;
	
	/**
	 * 
	 * Cr�ation du dispatcher
	 * 
	 * @param controleur
	 */
	public Dispatcher(Controleur controleur){
		
		control = controleur;
		this.repartitorList = new HashMap<Integer,Repartitor>();
		
	}
	
	/**
	 * Envoi d'une application au dispatcher
	 * 
	 * @param id
	 * @throws Exception 
	 */
	public void createApplication(int id) throws Exception{
		
		repartitorList.put(id, new Repartitor("repartitor"+id, control));
		
		
	}
	
	
	/**
	 * 
	 * Envoi d'une requette au dispatcher
	 * 
	 * @param req
	 * 
	 * @throws Exception 
	 */
	public void processRequest(Request req) throws Exception{
		
		
		
		
		Repartitor repart;
		
		if(repartitorList.containsKey(req.getAppId())){
			//acces au repartiteur de l'application
			repart= repartitorList.get(req.getAppId());
			// envoi de la requette + retour du nombre de VM a lancer (0 si la requette a ete envoy�e
			int VMtoLaunch =repart.dispatch(req);
			// Cas o� on doit lancer une nouvelle VM, creation d'un port sur le repartiteur, demande de lien au controleur
			if(VMtoLaunch!=0){
				
				String randomString = req.getAppId() + java.util.UUID.randomUUID().toString();
				repart.addNewPort(randomString);
				control.demandeVM(VMtoLaunch,req.getAppId(),req,randomString);
				
			}
		
		}else{
			
			System.out.println("Rejet de la requette, application correspondante non trouv�e.");
		}
		 
		
	}

}
