package com.nublic.app.init.client.controller;

import com.nublic.app.init.client.model.InitModel;
import com.nublic.app.init.client.model.Step;
import com.nublic.app.init.client.ui.MainUi;

public class Controller extends URLController {
	public static Controller INSTANCE = null;
	
	public static void create(InitModel model, MainUi ui) {
		if (INSTANCE == null) {
			INSTANCE = new Controller(model, ui);
		}
	}
	
	private Controller(InitModel model, MainUi ui) {
		super(model, ui);
		
		// get status
		ui.setStep(Step.WELCOME);
	}
	
	public InitModel getModel() {
		return model;
	}
	
	public MainUi getUi() {
		return ui;
	}
}
