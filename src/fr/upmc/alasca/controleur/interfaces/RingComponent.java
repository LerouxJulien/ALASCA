package fr.upmc.alasca.controleur.interfaces;

import java.util.ArrayList;

public interface RingComponent {
	
	public void sendTokenToNextComponent(ArrayList<String> freeVM);
	
}
