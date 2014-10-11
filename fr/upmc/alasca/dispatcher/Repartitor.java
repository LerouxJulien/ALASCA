package fr.upmc.alasca.dispatcher;

import java.util.ArrayList;

import fr.upmc.alasca.requestgen.objects.Request;

public class Repartitor {

	private static long VMMAXINST = 20000000;
	
	
	public Repartitor(){
		
		
	}
	
	
	/* Methode de repartition des requettes sur une liste de VM
	 * renvoi 0 si auvune cr�aion de VM n'est requise
	 * un entier repr�sentant le nombre a creer sinon
	 */
	public int dispatch(Request req, ArrayList<VM> sendingList) {
		
		/* Etablie le nombre de vm requises minimum pour le traitement*/
		
		int nbVMneeded = 0;
		long temp = req.getInstructions();
		while (temp>VMMAXINST){
			
			temp -= VMMAXINST;
			nbVMneeded ++;
		}
		nbVMneeded ++;
		
		/*Etablie la liste des VM utilis�es par l'application ou libre qui peucent etre utilis�es*/
		
		int nbVMavailable = 0;
		ArrayList<VM> readyVMlist = new ArrayList<VM>();
				
		for(int i = 0;i<sendingList.size();i++){
			
			VM currentVM = sendingList.get(i);
			
			if(!currentVM.QueuIsFull() || currentVM.appID==0){
				
				nbVMavailable++;
				readyVMlist.add(currentVM);
				
			}
			
		}
				
		
		/* Comparaison du nombre requis contre nombre disponible*/
		
		System.out.println("Etat VM, needed: "+ nbVMneeded + " / available :" + nbVMavailable);
		//Cas o� il n'y en a pas assez => retour du nombre a demarer
		if(nbVMneeded > nbVMavailable){
			
			return nbVMneeded - nbVMavailable;
			
		}
		
		//Cas o� il y en a assez => d�ploiement des requettes sur les VM
		else{
			/*D�coupe des requettes */
			long tempInst = req.getInstructions();
			ArrayList<Request> listReq = new ArrayList<Request>();
			
			
			while (tempInst!=0){
				
				while(tempInst>this.VMMAXINST){
					
					listReq.add(new Request(req.getURI(), this.VMMAXINST, req.getAppId()));
					tempInst-=VMMAXINST;
					System.out.println("d�coupe des requettes " + tempInst);
				}
				
				listReq.add(new Request(req.getURI(), tempInst, req.getAppId()));
				tempInst=0;
				
			}
			
			/* d�ploiement */
			
			for(int j = 0; j<listReq.size();j++){
				
				readyVMlist.get(j).deploy(listReq.get(j));
				
			}
			
			
		}
		
		return 0;
		
		
	}

	

}
