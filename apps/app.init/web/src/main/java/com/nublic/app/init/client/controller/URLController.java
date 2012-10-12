package com.nublic.app.init.client.controller;

import com.nublic.app.init.client.Constants;
import com.nublic.app.init.client.model.InitModel;
import com.nublic.app.init.client.model.Step;
import com.nublic.app.init.client.ui.MainUi;
import com.nublic.util.messages.ParamsHashMap;

public class URLController {
	InitModel model;
	MainUi ui;
	Step currentStep = null;

	protected URLController(InitModel model, MainUi ui) {
		this.ui = ui;
		this.model = model;
	}
	
	public Step getStep() {
		return currentStep;
	}

	// +++++ Handle history state change ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// When URL changes this method is called
	public void changeState(ParamsHashMap hmap) {
		String page = hmap.get(Constants.PARAM_PAGE);
		
		Step targetStep = Step.parseString(page);
		
		if (targetStep == null) {
			ui.setStep(Step.WELCOME);
			currentStep = Step.WELCOME;
			return;
		}
		
		if (ui.canChangeTo(targetStep)) {
			ui.setStep(targetStep);
			currentStep = targetStep;
		} else {
			ui.showCompleteFirstPopup();
		}
	}
}
