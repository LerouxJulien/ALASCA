package fr.upmc.alasca.computer.connectors;

import fr.upmc.alasca.computer.interfaces.RefreshVMI;
import fr.upmc.components.connectors.AbstractConnector;


public class CompVMConnector extends AbstractConnector implements
RefreshVMI {

	@Override
	public void refreshVM(double freq) throws Exception {
		((RefreshVMI) this.offering).refreshVM(freq);
	}


}
