package fr.upmc.alasca.controleur.exceptions;

public class BadDeploymentException extends Exception {
	private static final long serialVersionUID = -5083689331391719975L;

	public BadDeploymentException(String s){
		super(s);
	}
}
