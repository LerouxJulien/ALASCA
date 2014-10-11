package fr.upmc.alasca.dispatcher;

import java.util.ArrayList;

/*classe bouchon en atente de l'implémentation*/
public class ControleurAdmission {

	public int idcmpt=0;
	
	ArrayList<VM> listVMtotal;
	
	public ControleurAdmission(int i){
		
		
		listVMtotal = new ArrayList<VM>();
		
		while(i!=0){
			
			listVMtotal.add(new VM(idcmpt));
			idcmpt++;
			i--;
		}
		
	}
	
	public ArrayList<VM> requestDeploy(int vMtoLaunch) {
		
		while(vMtoLaunch!=0){
			
			listVMtotal.add(new VM(idcmpt));
			idcmpt++;
			vMtoLaunch--;
		}
		
		return listVMtotal;
		
	}

}
