package fr.upmc.alasca.computer.objects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * La classe <code>VMCarac</code> definit le message des caracteristiques d'une
 * machine virtuelle. Elle renseigne le l'ID de la VM,les fr�quences de ses coeurs, 
 * les temps d'execution et le temps moyen de traitement de ses requ�tes.
 */
public class VMCarac implements Serializable{

	private static final long serialVersionUID = 6460133008609177901L;
	
	// l'ID de la VM
	private String VMid;
	
	//La liste des fr�quences de ses coeurs
	private  List<Double> frequencies;
	
	//La liste des temps de traitement des requ�tes
	private List<Integer> timetable;
	
	//Le temps moyen de traitement d'une requ�te
	private int mediumtime;
	
	/**
	 * Cr�er un message de caracterisitiques de la VM
	 * 
	 * @param string l'ID de la VM
	 * @param list la liste des fr�quences de ses coeurs
	 */
	public VMCarac(String string,List<Double> list){
		
		this.VMid=string;
		
		this.frequencies=list;
		
		timetable = new ArrayList<Integer>();
		
		mediumtime = 0;
		
	}
	
	/**
	 * Retourne l'ID de la VM
	 * 
	 * @return VMid
	 */
	public String getVMid() {
		return VMid;
	}

	/**
	 * Modifie l'ID de la VM
	 * 
	 * @param vMid le nouvel ID
	 */
	public void setVMid(String vMid) {
		VMid = vMid;
	}

	/**
	 * Retourne la liste des fr�quences des coeurs de la VM
	 * 
	 * @return frequencies
	 */
	public List<Double> getFrequencies() {
		return frequencies;
	}

	/**
	 * Modifie la liste des fr�quences des coeurs de la VM
	 * 
	 * @param frequencies la nouvelle liste de fr�quences
	 */
	public void setFrequencies(List<Double> frequencies) {
		this.frequencies = frequencies;
	}

	/**
	 * Retourne la liste des temps de traitement des requ�tes
	 * 
	 * @return timetable
	 */
	public List<Integer> getTimetable() {
		return timetable;
	}

	/**
	 * Modifie la liste des temps de traitement des requ�tes
	 * 
	 * @param timetable la nouvelle liste des temps de traitement
	 */
	public void setTimetable(List<Integer> timetable) {
		this.timetable = timetable;
	}

	/**
	 * Retourne le temps moyen de traitement d'une requ�te
	 * 
	 * @return mediumtime
	 */
	public int getMediumtime() {
		return mediumtime;
	}

	/**
	 * Modifie le temps moyen de traitement d'une requ�te
	 * 
	 * @param mediumtime le nouveau temps moyen
	 */
	public void setMediumtime(int mediumtime) {
		this.mediumtime = mediumtime;
	}
	
	/**
	 * ajoute un temps de traitement et recalcule la moyenne
	 * 
	 * @param l le temps de traitement a ajouter
	 */
	public void addTime(long l){
		
		this.timetable.add((int) l);
		
		int total=0; 
		
		for(Integer i : timetable){
			
			total += i;
			
		}
		
		this.mediumtime = total/timetable.size();
	}
	
}
