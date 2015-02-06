package fr.upmc.alasca.controleur.components;

import java.util.ArrayList;

import fr.upmc.alasca.controleur.ports.RingComponentOutboundPort;

public class RingTask implements Runnable {
	
	private RingComponentOutboundPort port = null;
	private ArrayList<String> token = null;
	
	public RingTask(RingComponentOutboundPort port, ArrayList<String> token) {
		this.port = port;
		this.token = token;
	}

	@Override
	public void run() {
		this.sendTokenToNextComponent();
	}
	
	public void sendTokenToNextComponent(){
		this.port.sendTokenToNextComponent(token);
	}
	
}
