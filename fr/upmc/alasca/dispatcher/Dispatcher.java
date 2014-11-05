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
	
	//Liste des VM provenant du controleur d'admissions **VM bouvhon utilisé**
	
	private ArrayList<String> VMList;
	
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
	 */
	public void createApplication(int id){
		
		repartitorList.put(id, new Repartitor(control));
		
		
	}
	
	
	/**
	 * 
	 * Envoi d'une requette au dispatcher
	 * 
	 * @param req
	 * @param listVM
	 * @throws Exception 
	 */
	public void processRequest(Request req,ArrayList<String> listVM) throws Exception{
		
		VMList = listVM;
		ArrayList<String> sendingList = new ArrayList<String>();
		
		for(int i =0; i<VMList.size();i++){
			PortI port = control.findPortFromURI(this.VMList.get(i));
			VirtualMachine VM = (VirtualMachine) port.getOwner();
			
			
			if (VM.getAppID()==req.getAppId() || VM.getAppID()==0){
				
				sendingList.add(this.VMList.get(i));
				
			}
			
		}
		
		
		Repartitor repart;
		
		if(repartitorList.containsKey(req.getAppId())){
			repart= repartitorList.get(req.getAppId());
			int VMtoLaunch =repart.dispatch(req,sendingList);
			if(VMtoLaunch!=0){
				
				control.demandeVM(VMtoLaunch,req.getAppId(),req);
				
			}
		
		}else{
			
			System.out.println("Rejet de la requette, application correspondante non trouv�e.");
		}
		 
		
	}

}
