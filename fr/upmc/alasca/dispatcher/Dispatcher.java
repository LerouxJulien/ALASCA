package fr.upmc.alasca.dispatcher;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import fr.upmc.alasca.computer.components.VirtualMachine;
import fr.upmc.alasca.controleurAdmission.components.Controleur;
import fr.upmc.alasca.requestgen.objects.Request;
import fr.upmc.components.ports.PortI;

/* Classe représentant le composant interne du dispatcher
 * délèguant une application au répartiteur correspondant 
 *
 */

public class Dispatcher {
	
	/*controleur d'admission dont dépend le Dispatcher.*/
	
	private Controleur control;
	
	//Liste des VM provenant du controleur d'admissions **VM bouvhon utilisé**
	
	private ArrayList<String> VMList;
	
	// Map des couples applications/repartiteurs.
	
	private Map<Integer ,Repartitor > repartitorList;
	
	
	public Dispatcher(Controleur c){
		
		control = c;
		this.repartitorList = new HashMap<Integer,Repartitor>();
		
	}
	
	public void sendApplication(Request req,ArrayList<String> listVM){
		
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
		}else{
			
			repartitorList.put(req.getAppId(), new Repartitor(control));
			repart= repartitorList.get(req.getAppId());
		}
		 
		int VMtoLaunch =repart.dispatch(req,sendingList);
		if(VMtoLaunch!=0){
			
			ArrayList<String> newListVM = control.requestDeploy(VMtoLaunch);
			this.sendApplication(req, newListVM);
		}
	}

}
