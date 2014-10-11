package fr.upmc.alasca.dispatcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import fr.upmc.alasca.requestgen.objects.Request;

/* Classe représentant le composant interne du dispatcher
 * délèguant une application au répartiteur correspondant 
 *
 */

public class Dispatcher {
	
	/*controleur d'admission dont dépend le Dispatcher.*/
	
	private ControleurAdmission control;
	
	//Liste des VM provenant du controleur d'admissions **VM bouvhon utilisé**
	
	private ArrayList<VM> VMList;
	
	// Map des couples applications/repartiteurs.
	
	private Map<Integer ,Repartitor > repartitorList;
	
	
	public Dispatcher(ControleurAdmission c){
		
		control = c;
		this.repartitorList = new HashMap<Integer,Repartitor>();
		
	}
	
	public void sendApplication(Request req,ArrayList<VM> listVM){
		
		VMList = listVM;
		ArrayList<VM> sendingList = new ArrayList<VM>();
		
		for(int i =0; i<VMList.size();i++){
			
			if (this.VMList.get(i).getAppID()==req.getAppId() || this.VMList.get(i).getAppID()==0){
				
				sendingList.add(this.VMList.get(i));
				
			}
			
		}
		
		
		Repartitor repart;
		
		if(repartitorList.containsKey(req.getAppId())){
			repart= repartitorList.get(req.getAppId());
		}else{
			
			repartitorList.put(req.getAppId(), new Repartitor());
			repart= repartitorList.get(req.getAppId());
		}
		 
		int VMtoLaunch =repart.dispatch(req,sendingList);
		if(VMtoLaunch!=0){
			
			ArrayList<VM> newListVM = control.requestDeploy(VMtoLaunch);
			this.sendApplication(req, newListVM);
		}
	}

}
