package fr.upmc.alasca.controleurAdmission.exceptions;

public class NotEnoughRessourceException extends Exception {
	private static final long serialVersionUID = 1L;

	public NotEnoughRessourceException(String s){
		super(s);
	}
}
