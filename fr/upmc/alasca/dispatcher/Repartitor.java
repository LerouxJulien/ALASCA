package fr.upmc.alasca.dispatcher;

import java.net.URI;
import java.util.ArrayList;

import fr.upmc.alasca.computer.components.VirtualMachine;
import fr.upmc.alasca.controleurAdmission.components.Controleur;
import fr.upmc.alasca.requestgen.objects.Request;
import fr.upmc.components.ports.PortI;

public class Repartitor {

	//private static long VMMAXINST = 20000000;
	Controleur control;
	
	public Repartitor(Controleur c){
		control = c;
		
	}
	
	
	/* Methode de repartition des requettes sur une liste de VM
	 * renvoi 0 si auvune créaion de VM n'est requise
	 * un entier représentant le nombre a creer sinon
	 */
	public int dispatch(Request req, ArrayList<String> sendingList) throws Exception {
		for(int i =0; i<sendingList.size();i++){
			PortI port = control.findPortFromURI(sendingList.get(i));
			VirtualMachine VM = (VirtualMachine) port.getOwner();
		
			if(!VM.queueIsFull()){
				
				VM.addRequest(req);
				return 0;
			}
		}
		return 1;
		}
	
		
		
		
	}

	


