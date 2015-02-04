package fr.upmc.alasca.controleurAuto.interfaces;

import fr.upmc.components.interfaces.OfferedI;
import fr.upmc.components.interfaces.RequiredI;

public interface CAProviderI  extends OfferedI, RequiredI{
	public void deployFirstVM() throws Exception;
}
