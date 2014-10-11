package fr.upmc.alasca.dispatcher;

import fr.upmc.alasca.requestgen.objects.Request;

/* Classe VM bouchon pour test.*/

public class VM {

	public int ID;
	
	public int appID;
	
	public boolean queuisfull;
	
	public VM(int vmid){
		
		appID = 0;
		queuisfull = false;
		ID = vmid;
		
	}
	
	public int getAppID() {
		// TODO Auto-generated method stub
		return this.appID;
	}

	public boolean QueuIsFull() {

		
		return queuisfull;
	}

	public void deploy(Request request) {
		System.out.println("VM " + ID + " démarrée. Traitement de "+ request.getInstructions()+ " instructions.");
		
	}

}
