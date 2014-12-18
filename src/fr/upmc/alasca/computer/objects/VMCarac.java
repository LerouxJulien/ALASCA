package fr.upmc.alasca.computer.objects;

import java.util.List;

public class VMCarac {

	private int VMid;
	
	private  List<Double> frequencies;
	
	private List<Integer> timetable;
	
	private int mediumtime;

	public int getVMid() {
		return VMid;
	}

	public void setVMid(int vMid) {
		VMid = vMid;
	}

	public List<Double> getFrequencies() {
		return frequencies;
	}

	public void setFrequencies(List<Double> frequencies) {
		this.frequencies = frequencies;
	}

	public List<Integer> getTimetable() {
		return timetable;
	}

	public void setTimetable(List<Integer> timetable) {
		this.timetable = timetable;
	}

	public int getMediumtime() {
		return mediumtime;
	}

	public void setMediumtime(int mediumtime) {
		this.mediumtime = mediumtime;
	}
	
	/**
	 * 
	 * ajoute un temps de traitement et recalcule la moyenne
	 * 
	 * @param l
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
