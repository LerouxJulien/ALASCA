package fr.upmc.alasca.computer.exceptions;

public class NotEnoughCapacityVMException extends Exception {
	private static final long serialVersionUID = 1190925171580256858L;

	public NotEnoughCapacityVMException(String s){
		super(s);
	}
}
