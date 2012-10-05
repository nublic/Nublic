package com.nublic.app.init.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.init.client.Constants;
import com.nublic.app.init.client.controller.Controller;
import com.nublic.app.init.client.model.Step;
import com.nublic.app.init.client.ui.step.StepPanel;
import com.nublic.app.init.client.ui.users.UsersPage;
import com.nublic.util.error.ErrorPopup;

public class MainUi extends Composite {
	private static MainUiUiBinder uiBinder = GWT.create(MainUiUiBinder.class);
	interface MainUiUiBinder extends UiBinder<Widget, MainUi> {}

	@UiField StepPanel stepPanel;
	@UiField SimplePanel mainContainer;
	CentralPanel currentPanel;
	
	public MainUi() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	public void setStep(Step s) {
		if (Controller.INSTANCE == null || s != Controller.INSTANCE.getStep()) {
			stepPanel.setStep(s);
			currentPanel = s.getUiWidget();
			mainContainer.setWidget(currentPanel);
		}
	}

	public boolean canChangeTo(Step targetStep) {
		return currentPanel.canChangeTo(targetStep);
	}

	public void showCompleteFirstPopup() {
		ErrorPopup ep = new ErrorPopup(Constants.I18N.completeFirst());
		ep.setInnerHeight(170);
		ep.center();
		History.back();
	}

	public void addCompletedUser(String name) {
		if (currentPanel instanceof UsersPage) {
			((UsersPage) currentPanel).addCompletedUser(name);
		}
	}

}
