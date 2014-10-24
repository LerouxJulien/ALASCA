package fr.upmc.alasca.controleurAdmission.interfaces;

import fr.upmc.alasca.requestgen.objects.Request;
import fr.upmc.components.interfaces.OfferedI;

public interface ControleurProviderClientI extends OfferedI{
	
	public void acceptRequest(Request r) throws Exception;
	
	public void acceptApplication(int id) throws Exception;
}
