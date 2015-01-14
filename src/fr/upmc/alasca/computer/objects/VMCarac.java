package fr.upmc.alasca.computer.objects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class VMCarac implements Serializable{

	private static final long serialVersionUID = 6460133008609177901L;
	
	private String VMid;
	
	private  List<Double> frequencies;
	
	private List<Integer> timetable;
	
	private int mediumtime;
	
	public VMCarac(String string,List<Double> list){
		
		this.VMid=string;
		
		this.frequencies=list;
		
		timetable = new ArrayList<Integer>();
		
		mediumtime = 0;
		
	}
	
	

	public String getVMid() {
		return VMid;
	}

	public void setVMid(String vMid) {
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
