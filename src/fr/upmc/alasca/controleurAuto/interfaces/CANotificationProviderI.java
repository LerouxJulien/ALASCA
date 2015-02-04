package fr.upmc.alasca.controleurAuto.interfaces;

import fr.upmc.alasca.computer.objects.VMCarac;
import fr.upmc.alasca.computer.objects.VMMessages;

public interface CANotificationProviderI {
	public void notifyStatus(VMMessages m) throws Exception;
	
	public void notifyCarac(String id, VMCarac c) throws Exception;
}
