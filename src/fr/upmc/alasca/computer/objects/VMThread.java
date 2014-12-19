package fr.upmc.alasca.computer.objects;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import fr.upmc.alasca.computer.components.VirtualMachine;
import fr.upmc.alasca.computer.enums.Status;
import fr.upmc.alasca.requestgen.objects.Request;
import fr.upmc.alasca.requestgen.utils.TimeProcessing;
import fr.upmc.components.AbstractComponent;

/**
 * La classe <code>VMThread</code> definit un thread dans la machine
 * virtuelle <code>VirtualMachine</code>. Ce thread recupere une requetes de
 * la file d'attente et la met dans son fil d'execution.
 * 
 * <p>
 * Created on : 16 oct. 2014
 * </p>
 * 
 * @author <a href="mailto:henri.ng@etu.upmc.fr">Henri NG</a>
 */
public class VMThread extends AbstractComponent {

	// Requete en cours de traitement
	protected Request servicing;

	// Machine virtuelle hote
	private VirtualMachine owner;

	// ID du thread de la VM
	private final String VMThreadID;

	// Frequence du coeur associe
	private final double frequence;

	// Etat du thread
	private boolean isWaiting;

	// Tache en cours de traitement avec date de fin determinee
	protected Future<?> nextEndServicingTaskFuture;

	/**
	 * Initialisation des threads internes a la VM (correspond a un fil
	 * d'execution dans le sujet)
	 * 
	 * Les VMThread sont inialises en meme temps que le deploiement de la
	 * VM. Un VMThread correspond a un coeur de la VM avec sa frequence
	 * associee.
	 * 
	 * @param VMThreadID
	 * @param frequence
	 * @param owner
	 */
	public VMThread(String VMThreadID, double frequence,
			VirtualMachine owner) {
		super(true, true);
		this.VMThreadID = VMThreadID;
		this.frequence = frequence;
		this.isWaiting = true;
		this.owner = owner;
		this.nextEndServicingTaskFuture = null;
	}

	/**
	 * Retourne la frequence du coeur associee de la VMThread
	 * 
	 * @return frequence
	 */
	public double getFrequence() {
		return frequence;
	}

	/**
	 * Retourne l'ID de la VMThread
	 * 
	 * @return VMThreadID
	 */
	public String getVMThreadID() {
		return VMThreadID;
	}

	/**
	 * Retourne l'etat de la VMThread. La fil d'execution est soit en
	 * attente de requetes, soit occupee.
	 * 
	 * @return VMThreadID
	 */
	public boolean isWaiting() {
		return isWaiting;
	}

	/**
	 * Retire une requete de la file d'attente de VM et la traite
	 * 
	 * @return time
	 */
	public long process() {
		this.servicing = owner.getQueue().remove();
		System.out.println("Begin servicing request " + this.servicing
				+ " at "
				+ TimeProcessing.toString(System.currentTimeMillis())
				+ " by vm " + this.owner.getMvID() + " with "
				+ this.servicing.getInstructions() + " instructions");
		this.isWaiting = false;
		final VMThread vmt = (VMThread) this;
		final long processingTime = (long) ((double) this.servicing
				.getInstructions() / ((double) frequence * 1000000));
		this.nextEndServicingTaskFuture = this.scheduleTask(
				new ComponentTask() {
					@Override
					public void run() {
						try {
							vmt.endServicingEvent();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}, processingTime, TimeUnit.SECONDS);
		return processingTime;
	}

	/**
	 * Methode appelee pour terminer le traitement d'une requete
	 * 
	 * @throws Exception
	 */
	public void endServicingEvent() throws Exception {
		long t = System.currentTimeMillis();
		long st = t - this.servicing.getArrivalTime();
		System.out.println("End servicing request   " + this.servicing
				+ " at " + TimeProcessing.toString(t)
				+ " with service time " + st + " by vm "
				+ this.owner.getMvID() + " --- Size queue : "
				+ this.owner.getQueue().size());
		if (owner.getQueue().isEmpty()) {
			this.servicing = null;
			this.isWaiting = true;
			this.nextEndServicingTaskFuture = null;
		} else {
			this.process();
		}
		owner.setStatus(Status.FREE);
		VMMessages m = new VMMessages(owner.getMvID(), owner.getStatus(),
				servicing.toString(), st);
		owner.getVMoport().notifyStatus(m);
		
	}

}
