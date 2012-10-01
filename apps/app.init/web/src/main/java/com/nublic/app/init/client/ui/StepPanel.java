package com.nublic.app.init.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.nublic.app.init.client.model.Step;

public class StepPanel extends Composite {
	private static StepPanelUiBinder uiBinder = GWT.create(StepPanelUiBinder.class);
	interface StepPanelUiBinder extends UiBinder<Widget, StepPanel> {}

	@UiField HorizontalPanel container;
	
	StepWidget userWidget;
	StepWidget masterWidget;
	StepWidget netWidget;
	StepWidget nameWidget;

	public StepPanel() {
		initWidget(uiBinder.createAndBindUi(this));

		// Create widgets
		userWidget = new StepWidget(Step.USERS.getName(), Step.USERS.getImage().getSafeUri().asString());
		masterWidget = new StepWidget(Step.MASTER_USER.getName(), Step.MASTER_USER.getImage().getSafeUri().asString());
		netWidget = new StepWidget(Step.NET_CONFIG.getName(), Step.NET_CONFIG.getImage().getSafeUri().asString());
		nameWidget = new StepWidget(Step.NAME.getName(), Step.NAME.getImage().getSafeUri().asString());
		
		// Add them to panel
		container.add(userWidget);
		container.add(masterWidget);
		container.add(netWidget);
		container.add(nameWidget);
	}
	
	public void setStep(Step s) {
		switch (s) {
		case WELCOME:
			userWidget.setStatus(StepStatus.UNREACHED);
			masterWidget.setStatus(StepStatus.UNREACHED);
			netWidget.setStatus(StepStatus.UNREACHED);
			nameWidget.setStatus(StepStatus.UNREACHED);
			break;
		case USERS:
			userWidget.setStatus(StepStatus.SELECTED);
			masterWidget.setStatus(StepStatus.UNREACHED);
			netWidget.setStatus(StepStatus.UNREACHED);
			nameWidget.setStatus(StepStatus.UNREACHED);
			break;
		case MASTER_USER:
			userWidget.setStatus(StepStatus.COMPLETED);
			masterWidget.setStatus(StepStatus.SELECTED);
			netWidget.setStatus(StepStatus.UNREACHED);
			nameWidget.setStatus(StepStatus.UNREACHED);
			break;
		case NET_CONFIG:
			userWidget.setStatus(StepStatus.COMPLETED);
			masterWidget.setStatus(StepStatus.COMPLETED);
			netWidget.setStatus(StepStatus.SELECTED);
			nameWidget.setStatus(StepStatus.UNREACHED);
			break;
		case NAME:
			userWidget.setStatus(StepStatus.COMPLETED);
			masterWidget.setStatus(StepStatus.COMPLETED);
			netWidget.setStatus(StepStatus.COMPLETED);
			nameWidget.setStatus(StepStatus.SELECTED);
			break;
		case FINISHED:
			userWidget.setStatus(StepStatus.COMPLETED);
			masterWidget.setStatus(StepStatus.COMPLETED);
			netWidget.setStatus(StepStatus.COMPLETED);
			nameWidget.setStatus(StepStatus.COMPLETED);
			break;
		}
	}
	
	
}
