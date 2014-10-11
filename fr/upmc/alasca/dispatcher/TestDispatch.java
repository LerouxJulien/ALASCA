package fr.upmc.alasca.dispatcher;

import fr.upmc.alasca.requestgen.components.RequestGenerator;
import fr.upmc.alasca.requestgen.objects.Request;

public class TestDispatch {

	public static void main(String[] args) {


		ControleurAdmission ctrl = new ControleurAdmission(3);
		Dispatcher disp = new Dispatcher(ctrl);
		
		Request req = new Request(0, 600000001, 1);
		
		disp.sendApplication(req, ctrl.listVMtotal);

	}

}
